package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public class CharNameTable
{
	private static final Logger _log = Logger.getLogger(CharNameTable.class.getName());

	private static CharNameTable _instance;

	public static CharNameTable getInstance()
	{
		if(_instance == null)
			_instance = new CharNameTable();
		return _instance;
	}

	public boolean doesCharNameExist(String name)
	{
		boolean result = true;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
			statement.setString(1, name);
			rset = statement.executeQuery();
			result = rset.next();
		}
		catch(SQLException e)
		{
			_log.warning("could not check existing charname:" + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		if(Util.contains_s(ConfigValue.NotCreateName, name))
			return true;
		return result;
	}

	public int accountCharNumber(String account)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int number = 0;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(char_name) FROM characters WHERE account_name=?");
			statement.setString(1, account);
			rset = statement.executeQuery();
			while(rset.next())
				number = rset.getInt(1);
		}
		catch(SQLException e)
		{
			_log.warning("could not check existing char number:" + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return number;
	}

	public int getObjectIdByName(String name)
	{
		int result = 0;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
			statement.setString(1, name);
			rset = statement.executeQuery();
			if(rset.next())
				result = rset.getInt(1);
		}
		catch (Exception e)
		{
			_log.info("CharNameTable.getObjectIdByName(String): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return result;
	}

	public String getNameByObjectId(int objectId)
	{
		String result = "";
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name FROM characters WHERE obj_Id=?");
			statement.setInt(1, objectId);
			rset = statement.executeQuery();
			if (rset.next())
				result = rset.getString(1);
		}
		catch (Exception e)
		{
			_log.info("CharNameTable.getObjectIdByName(int): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return result;
	}
}