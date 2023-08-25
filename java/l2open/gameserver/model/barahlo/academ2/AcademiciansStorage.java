package l2open.gameserver.model.barahlo.academ2;

import java.util.HashMap;
import java.util.logging.Logger;
import java.sql.ResultSet;

import l2open.common.ThreadPoolManager;
import l2open.common.RunnableImpl;
import l2open.database.*;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.ClanTable;

public class AcademiciansStorage
{
	private static final Logger _log = Logger.getLogger(AcademiciansStorage.class.getName());

	private static HashMap<Integer, Academicians> academicians = new HashMap<Integer,Academicians>();

	private static final AcademiciansStorage _instance = new AcademiciansStorage();

	public AcademiciansStorage()
	{
		load();
		ThreadPoolManager.getInstance().schedule(new AcademicCheck(), 60000L);
	}

	public static AcademiciansStorage getInstance()
	{
		return _instance;
	}

	public HashMap<Integer, Academicians> getAcademicMap()
	{
		return academicians;
	}

	public void addAcademic(Academicians a)
	{
		academicians.put(a.obj_id, a);
		insert(a);
	}

	public void delAcademic(Academicians a, boolean reward)
	{
		academicians.remove(a.obj_id);
		delete(a);
		if(reward)
		{
			L2Player player = L2ObjectsStorage.getPlayer(a.obj_id);
			if(player != null)
			{
				player.getInventory().addItem(57, a.price);
				player.sendPacket(SystemMessage.obtainItems(57, a.price, 0));
			}
			else
			{
				// TODO: Выдача бабла, когда чар афк, используется в случае, если игрока исключили из клана.
			}
		}
		else
		{
			L2Clan clan = ClanTable.getInstance().getClan(a.clan_id);
			if(clan != null)
				clan.getWarehouse().addItem(57, a.price, "Academicians.delAcademic");
		}
	}
	// ------------------------------------------------------------------------
	public void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM academicians");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int obj_id = rset.getInt("obj_id");
				int clan_id = rset.getInt("clan_id");
				long time = rset.getLong("time");
				long price = rset.getLong("price");

				academicians.put(obj_id, new Academicians(time, obj_id, clan_id, price, false));
			}
		}
		catch(Exception e)
		{
			_log.warning("AcademiciansStorage.load():" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_log.info("AcademiciansStorage: Loaded "+academicians.size()+" academicians.");
	}

	private void insert(Academicians academic)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO academicians (obj_id, clan_id, time, price) VALUES (?,?,?,?)");
			statement.setInt(1, academic.obj_id);
			statement.setInt(2, academic.clan_id);
			statement.setLong(3, academic.time);
			statement.setLong(4, academic.price);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademiciansStorage.insert(Academicians):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void delete(Academicians academic)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM academicians WHERE obj_id=?");
			statement.setInt(1, academic.obj_id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademiciansStorage.insert(Academicians):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
	// ------------------------------------------------------------------------
	public class AcademicCheck extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Academicians academic : AcademiciansStorage.getInstance().getAcademicMap().values())
			{
				//L2Player player = L2ObjectsStorage.getPlayer(academic.obj_id);

				if((academic.time + 3*24*60*60*1000) < System.currentTimeMillis())
					AcademiciansStorage.getInstance().delAcademic(academic, false); // удаляем с академии за то, что не успел вовремя пройти...
				/*else if(player != null)
				{
					int time = (int)(((academic.time + 3*24*60*60) - System.currentTimeMillis()/1000));
					if(time / 3600 < 1)
						player.sendPacket(new ExShowScreenMessage("Сообщение: " + time / 3600 + " ч.", 3000, ScreenMessageAlign.TOP_CENTER, true));
				}*/
			}

			ThreadPoolManager.getInstance().schedule(this, 60000L);
		}
	}
}
