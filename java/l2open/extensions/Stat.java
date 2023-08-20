package l2open.extensions;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.ServerVariables;

import java.sql.ResultSet;

public class Stat
{
	/* database statistics */
	private static long _insertItemCounter = 0;
	private static long _deleteItemCounter = 0;
	private static long _updateItemCounter = 0;
	private static long _lazyUpdateItem = 0;
	private static long _updatePlayerBase = 0;

	private static long _taxSum;
	private static long _taxLastUpdate;
	private static long _rouletteSum;
	private static long _rouletteLastUpdate;
	private static long _adenaSum;

	public static void init()
	{
		_taxSum = ServerVariables.getLong("taxsum", 0);
		_rouletteSum = ServerVariables.getLong("rouletteSum", 0);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT (SELECT SUM(count) FROM items WHERE item_id=57) + (SELECT SUM(treasury) FROM castle) AS `count`");
			rset = statement.executeQuery();
			if(rset.next())
				_adenaSum = rset.getLong("count");
			DatabaseUtils.closeDatabaseSR(statement, rset);
		}
		catch(Exception e)
		{
			System.out.println("Unable to load extended RRD stats");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	// database statistic methods
	// items
	public static void increaseInsertItemCount()
	{
		_insertItemCounter++;
	}

	public static long getInsertItemCount()
	{
		return _insertItemCounter;
	}

	public static void increaseDeleteItemCount()
	{
		_deleteItemCounter++;
	}

	public static long getDeleteItemCount()
	{
		return _deleteItemCounter;
	}

	public static void increaseUpdateItemCount()
	{
		_updateItemCounter++;
	}

	public static long getUpdateItemCount()
	{
		return _updateItemCounter;
	}

	public static void increaseLazyUpdateItem()
	{
		_lazyUpdateItem++;
	}

	public static long getLazyUpdateItem()
	{
		return _lazyUpdateItem;
	}

	// players
	public static void increaseUpdatePlayerBase()
	{
		_updatePlayerBase++;
	}

	public static long getUpdatePlayerBase()
	{
		return _updatePlayerBase;
	}

	public static void addTax(long sum)
	{
		_taxSum += sum;
		if(System.currentTimeMillis() - _taxLastUpdate < 10000)
			return;
		_taxLastUpdate = System.currentTimeMillis();
		ServerVariables.set("taxsum", _taxSum);
	}

	public static void addRoulette(long sum)
	{
		_rouletteSum += sum;
		if(System.currentTimeMillis() - _rouletteLastUpdate < 10000)
			return;
		_rouletteLastUpdate = System.currentTimeMillis();
		ServerVariables.set("rouletteSum", _rouletteSum);
	}

	public static long getTaxSum()
	{
		return _taxSum;
	}

	public static long getRouletteSum()
	{
		return _rouletteSum;
	}

	public static void addAdena(long sum)
	{
		_adenaSum += sum;
	}

	public static long getAdena()
	{
		return _adenaSum;
	}
}