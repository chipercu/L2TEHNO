package l2open.gameserver.model.barahlo.academ.dao;

import java.sql.ResultSet;

import l2open.database.*;
import l2open.gameserver.model.barahlo.academ.Academicians;
import l2open.gameserver.model.barahlo.academ.AcademiciansStorage;
import java.util.logging.Logger;

public class AcademiciansDAO
{
	private static final Logger _log = Logger.getLogger(AcademiciansDAO.class.getName());
	private static final AcademiciansDAO _instance = new AcademiciansDAO();

	private static final String SELECT_SQL_QUERY = "SELECT * FROM academicians";
	private static final String INSERT_SQL_QUERY = "INSERT INTO academicians (objId, clanId, end_time) VALUES (?,?,?)";
	private static final String DELETE_SQL_QUERY = "DELETE FROM academicians WHERE objId=?";

	public static AcademiciansDAO getInstance()
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
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			rset = statement.executeQuery();
			while(rset.next())
			{
				int objId = rset.getInt("objId");
				int clanId = rset.getInt("clanId");
				long end_time = rset.getLong("end_time");
				new Academicians(end_time, objId, clanId);
			}
		}
		catch(Exception e)
		{
			_log.warning("AcademiciansDAO.load():" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void insert(Academicians academic)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, academic.getObjId());
			statement.setInt(2, academic.getClanId());
			statement.setLong(3, academic.getTime());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademiciansDAO.insert(Academicians):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void delete(Academicians academic)
	{
		AcademiciansStorage.getInstance().get().remove(academic);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, academic.getObjId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("AcademiciansDAO.delete(Academicians):" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}
