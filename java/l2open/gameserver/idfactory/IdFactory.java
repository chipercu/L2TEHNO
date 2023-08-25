package l2open.gameserver.idfactory;

import l2open.config.ConfigValue;
import l2open.Server;
import l2open.common.ParallelExecutor;
import l2open.database.*;
import l2open.gameserver.idfactory.Tasks.ClearQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class IdFactory
{
	private static Logger _log = Logger.getLogger(IdFactory.class.getName());

	protected boolean initialized;

	protected long releasedCount = 0;

	public static final int FIRST_OID = 0x10000000;
	public static final int LAST_OID = 0x7FFFFFFF;
	public static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

	protected static final IdFactory _instance = new BitSetIDFactory();

	protected IdFactory()
	{
		setAllCharacterOffline();
		if(!ConfigValue.DisableDbClean)
		{
			if(ConfigValue.MultiThreadedIdFactoryCleaner)
				mt_cleanUpDB();
			else
				cleanUpDB();
		}
	}

	private void setAllCharacterOffline()
	{
		ThreadConnection conn = null;
		FiltredStatement stmt = null;
		try
		{
			conn = L2DatabaseFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE characters SET online = 0");
			stmt.executeUpdate("UPDATE characters SET accesslevel = 0 WHERE accesslevel = -1");
			_log.info("Clear characters online status and accesslevel.");
		}
		catch(SQLException e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(conn, stmt);
		}
	}

	/**
	 * Cleans up Database
	 */
	private void cleanUpDB()
	{
		long time = (System.currentTimeMillis()/1000)-(ConfigValue.lastAccessTime * 24 * 60 * 60);
		int level = ConfigValue.delLevel;//85;

		if(ConfigValue.deleteFastAccessChar)
			mysql.set("DELETE characters, character_subclasses FROM characters, character_subclasses WHERE character_subclasses.char_obj_id=characters.obj_Id AND character_subclasses.isBase='1' AND characters.lastAccess<="+time+" AND character_subclasses.level<="+level+";");	
		for(ClearQuery q : ClearQuery.values())
			q.run();
		_log.info("Total cleaned: " + ClearQuery.totalDeleted + ", updated: " + ClearQuery.totalUpdated + " elements in database.");
	}

	private void mt_cleanUpDB()
	{
		ParallelExecutor executor = new ParallelExecutor("cleanUpDB", Thread.NORM_PRIORITY, ClearQuery.values().length);
		try
		{
			for(ClearQuery q : ClearQuery.values())
				executor.execute(q);
			executor.waitForFinishAndDestroy();
			_log.info("Total cleaned: " + ClearQuery.totalDeleted + ", updated: " + ClearQuery.totalUpdated + " elements in database.");
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			Server.exit(0, "cleanUpDB");
		}
	}

	protected int[] extractUsedObjectIDTable()
	{
		return ConfigValue.MultiThreadedIdFactoryExtractor ? extractUsedObjectIDTable2() : extractUsedObjectIDTable1();
	}

	protected int[] extractUsedObjectIDTable1()
	{
		ThreadConnection con = null;
		FiltredStatement s = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			s = con.createStatement();

			String query = "SELECT " + Tasks.objTables[0][1] + ", 0 AS i FROM " + Tasks.objTables[0][0];
			for(int i = 1; i < Tasks.objTables.length; i++)
				query += " UNION SELECT " + Tasks.objTables[i][1] + ", " + i + " FROM " + Tasks.objTables[i][0];

			rs = s.executeQuery("SELECT COUNT(*), COUNT(DISTINCT " + Tasks.objTables[0][1] + ") FROM ( " + query + " ) AS all_ids");
			if(!rs.next())
				throw new Exception("IdFactory: can't extract count ids");
			if(rs.getInt(1) != rs.getInt(2))
				throw new Exception("IdFactory: there are duplicates in object ids");

			int[] result = new int[rs.getInt(1)];
			DatabaseUtils.closeResultSet(rs);
			_log.info("IdFactory: Extracting " + result.length + " used id's from data tables...");

			rs = s.executeQuery(query);
			int idx = 0;
			while(rs.next())
				result[idx++] = rs.getInt(1);

			_log.info("IdFactory: Successfully extracted " + idx + " used id's from data tables.");
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Server.exit(0, "IdFactory");
			return null;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, s, rs);
		}
	}

	protected int[] extractUsedObjectIDTable2()
	{
		// считаем количество по всем таблицам распаралеленно
		final int[][] objCounts = new int[Tasks.objTables.length][];
		ParallelExecutor multiextractor = new ParallelExecutor("extractUsedObjectIDTable::CountObjectIds", Thread.NORM_PRIORITY, Tasks.objTables.length);
		try
		{
			for(int i = 0; i < Tasks.objTables.length; i++)
			{
				objCounts[i] = new int[2];
				multiextractor.execute(new Tasks.CountObjectIds(Tasks.objTables[i], objCounts[i]));
			}
			multiextractor.waitForFinishAndDestroy();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			Server.exit(0, "IdFactory::CountObjectIds");
		}

		// выставляем смещения для каждой таблици
		int idx = 0;
		for(int i = 0; i < objCounts.length; i++)
		{
			objCounts[i][1] = idx;
			idx += objCounts[i][0];
		}
		int[] result = new int[idx];
		_log.info("IdFactory: Extracting " + result.length + " used id's from data tables...");

		// извлекаем идентификаторы по всем таблицам распаралеленно
		multiextractor = new ParallelExecutor("extractUsedObjectIDTable::ExtractObjectIds", Thread.NORM_PRIORITY, Tasks.objTables.length);
		try
		{
			for(int i = 0; i < Tasks.objTables.length; i++)
				multiextractor.execute(new Tasks.ExtractObjectIds(Tasks.objTables[i], objCounts[i], result));
			multiextractor.waitForFinishAndDestroy();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			Server.exit(0, "IdFactory::ExtractObjectIds");
		}
		_log.info("IdFactory: Successfully extracted " + result.length + " used id's from data tables.");
		return result;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	public static IdFactory getInstance()
	{
		return _instance;
	}

	public abstract int getNextId();

	/**
	 * return a used Object ID back to the pool
	 * @param object ID
	 */
	public void releaseId(int id)
	{
		releasedCount++;
	}

	public long getReleasedCount()
	{
		return releasedCount;
	}

	public abstract int size();

	public static void unload()
	{
		if(_instance != null)
			((BitSetIDFactory) _instance)._unload();
	}
}