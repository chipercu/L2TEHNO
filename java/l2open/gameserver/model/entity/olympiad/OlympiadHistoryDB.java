package l2open.gameserver.model.entity.olympiad;

import l2open.database.*;

import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Logger;

public class OlympiadHistoryDB
{
	private static final Logger _log = Logger.getLogger(OlympiadHistoryDB.class.getName());
	private static final OlympiadHistoryDB _instance = new OlympiadHistoryDB();
	public static final String SELECT_SQL_QUERY = "SELECT * FROM olympiad_history ORDER BY game_start_time";
	public static final String DELETE_SQL_QUERY = "DELETE FROM olympiad_history WHERE old=1";
	public static final String UPDATE_SQL_QUERY = "UPDATE olympiad_history SET old=1";
	public static final String INSERT_SQL_QUERY = "INSERT INTO olympiad_history(object_id_1, object_id_2, class_id_1, class_id_2, name_1, name_2, game_start_time, game_time, game_status, game_type, old) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

	public static OlympiadHistoryDB getInstance()
	{
		return _instance;
	}

	public Map<Boolean, List<OlympiadHistory>> select()
	{
		Map<Boolean, List<OlympiadHistory>> map = null;
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery(SELECT_SQL_QUERY);
			map = new HashMap<Boolean, List<OlympiadHistory>>(2);
			map.put(Boolean.TRUE, new ArrayList<OlympiadHistory>());
			map.put(Boolean.FALSE, new ArrayList<OlympiadHistory>());

			while (rset.next())
			{
				int objectId1 = rset.getInt("object_id_1");
				int objectId2 = rset.getInt("object_id_2");

				int classId1 = rset.getInt("class_id_1");
				int classId2 = rset.getInt("class_id_2");

				String name1 = rset.getString("name_1");
				String name2 = rset.getString("name_2");

				boolean old = rset.getBoolean("old");

				OlympiadHistory history = new OlympiadHistory(objectId1, objectId2, classId1, classId2, name1, name2, rset.getLong("game_start_time"), rset.getInt("game_time"), rset.getInt("game_status"), rset.getInt("game_type"));

				map.get(old).add(history);
			}
		}
		catch (Exception e)
		{
			map = Collections.emptyMap();
			_log.warning("OlympiadHistoryDB(59): select(): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return map;
	}

	public void insert(OlympiadHistory history)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, history.getObjectId1());
			statement.setInt(2, history.getObjectId2());
			statement.setInt(3, history.getClassId1());
			statement.setInt(4, history.getClassId2());
			statement.setString(5, history.getName1());
			statement.setString(6, history.getName2());
			statement.setLong(7, history.getGameStartTime());
			statement.setInt(8, history.getGameTime());
			statement.setInt(9, history.getGameStatus());
			statement.setInt(10, history.getGameType());
			statement.setInt(11, 0);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.warning("OlympiadHistoryDB: insert(OlympiadHistory): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void switchData()
	{
		if(mysql.set(DELETE_SQL_QUERY))
		{
			if(!mysql.set(UPDATE_SQL_QUERY))
				_log.warning("OlympiadHistoryDB(104): UPDATE_SQL_QUERY("+UPDATE_SQL_QUERY+")");
		}
		else
			_log.warning("OlympiadHistoryDB(107): DELETE_SQL_QUERY("+DELETE_SQL_QUERY+")");
	}
}
