package l2open.gameserver.instancemanager;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;

import java.sql.ResultSet;

public class ServerVariables
{
	private static StatsSet server_vars = null;

	private static StatsSet getVars()
	{
		if(server_vars == null)
		{
			server_vars = new StatsSet();
			LoadFromDB();
		}
		return server_vars;
	}

	private static void LoadFromDB()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM server_variables");
			rs = statement.executeQuery();
			while(rs.next())
				server_vars.set(rs.getString("name"), rs.getString("value"));
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	private static void SaveToDB(String name)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			String value = getVars().getString(name, "");
			if(value.isEmpty())
			{
				statement = con.prepareStatement("DELETE FROM server_variables WHERE name = ?");
				statement.setString(1, name);
				statement.execute();
			}
			else
			{
				statement = con.prepareStatement("REPLACE INTO server_variables (name, value) VALUES (?,?)");
				statement.setString(1, name);
				statement.setString(2, value);
				statement.execute();
			}
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static boolean getBool(String name)
	{
		return getVars().getBool(name);
	}

	public static boolean getBool(String name, boolean defult)
	{
		return getVars().getBool(name, defult);
	}

	public static int getInt(String name)
	{
		return getVars().getInteger(name);
	}

	public static int getInt(String name, int defult)
	{
		return getVars().getInteger(name, defult);
	}

	public static long getLong(String name)
	{
		return getVars().getLong(name);
	}

	public static long getLong(String name, long defult)
	{
		return getVars().getLong(name, defult);
	}

	public static float getFloat(String name)
	{
		return getVars().getFloat(name);
	}

	public static float getFloat(String name, float defult)
	{
		return getVars().getFloat(name, defult);
	}

	public static String getString(String name)
	{
		return getVars().getString(name);
	}

	public static String getString(String name, String defult)
	{
		return getVars().getString(name, defult);
	}

	public static void set(String name, boolean value)
	{
		getVars().set(name, value);
		SaveToDB(name);
	}

	public static void set(String name, int value)
	{
		getVars().set(name, value);
		SaveToDB(name);
	}

	public static void set(String name, long value)
	{
		getVars().set(name, value);
		SaveToDB(name);
	}

	public static void set(String name, double value)
	{
		getVars().set(name, value);
		SaveToDB(name);
	}

	public static void set(String name, String value)
	{
		getVars().set(name, value);
		SaveToDB(name);
	}

	public static void unset(String name)
	{
		getVars().unset(name);
		SaveToDB(name);
	}
}