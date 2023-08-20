package l2open.gameserver.model.barahlo.academ.dao;

import java.sql.ResultSet;

import l2open.database.*;
import l2open.gameserver.model.barahlo.academ.AcademyRequest;
import l2open.gameserver.model.barahlo.academ.AcademyStorage;
import java.util.logging.Logger;

public class AcademyRequestDAO
{
	private static final Logger _log = Logger.getLogger(AcademyRequestDAO.class.getName());
	private static final AcademyRequestDAO _instance = new AcademyRequestDAO();

	private static final String LOAD_SQL_QUERY = "SELECT * FROM academy_request";
	private static final String UPDATE_SQL_QUERY = "UPDATE academy_request SET seats=? WHERE clanId=?";
	private static final String INSERT_SQL_QUERY = "INSERT INTO academy_request (time, clanId, seats, price, item) VALUES (?,?,?,?,?)";
	private static final String DELETE_SQL_QUERY = "DELETE FROM academy_request WHERE clanId=?";

	public static AcademyRequestDAO getInstance()
	{
		return _instance;
	}

	public void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(LOAD_SQL_QUERY);
			rset = statement.executeQuery();
			while(rset.next())
			{
				int time = rset.getInt("time");
				int clanId = rset.getInt("clanId");
				int seats = rset.getInt("seats");
				int item = rset.getInt("item");
				long price = rset.getLong("price");
				new AcademyRequest(time, clanId, seats, price, item);
			}
		}
		catch(Exception e)
		{
			_log.warning("AcademyRequestDAO.load():" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void update(AcademyRequest request)
	{
		AcademyStorage.getInstance().updateList();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_SQL_QUERY);
			statement.setInt(1, request.getSeats());
			statement.setInt(2, request.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademyRequestDAO.update(AcademyRequest):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void insert(AcademyRequest request)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, request.getTime());
			statement.setInt(2, request.getClanId());
			statement.setInt(3, request.getSeats());
			statement.setLong(4, request.getPrice());
			statement.setLong(5, request.getItem());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademyRequestDAO.insert(AcademyRequest):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void delete(int id)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademyRequestDAO.delete(id):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
	// ---------
	public static final String UPDATE_CLAN_DESCRIPTION = "UPDATE clan_description SET description=? WHERE clan_id=?";
	public static final String INSERT_CLAN_DESCRIPTION = "INSERT INTO clan_description (clan_id, description) VALUES (?, ?)";

	public void updateDescription(int id, String description)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_CLAN_DESCRIPTION);
			statement.setString(1, description);
			statement.setInt(2, id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("ClanDataDAO.updateDescription(int, String)"+e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void insertDescription(int id, String description)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_CLAN_DESCRIPTION);
			statement.setInt(1, id);
			statement.setString(2, description);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("ClanDataDAO.updateDescription(int, String)"+e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}
