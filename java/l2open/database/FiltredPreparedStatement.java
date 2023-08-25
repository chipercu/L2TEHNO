package l2open.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FiltredPreparedStatement implements FiltredStatementInterface
{
	private final PreparedStatement myStatement;

	public FiltredPreparedStatement(PreparedStatement statement)
	{
		myStatement = statement;
	}

	public ResultSet executeQuery() throws SQLException
	{
		return myStatement.executeQuery();
	}

	public PreparedStatement getStatement()
	{
		return myStatement;
	}

	@Override
	public void close()
	{
		try
		{
			myStatement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public boolean execute() throws SQLException
	{
		return myStatement.execute();
	}

	public ResultSet executeQuery(String sql) throws SQLException
	{
		return myStatement.executeQuery(sql);
	}

	public void setInt(int index, int val) throws SQLException
	{
		myStatement.setInt(index, val);
	}

	//private static final char _92 = 92;

	public void setString(int index, String val) throws SQLException
	{
		/*
		if(val != null && val.length() > 0)
		{
			StringBuilder sb = new StringBuilder();

			char[] arr = val.toCharArray();
			for(int i = 0; i < arr.length; i++)
				// Защита от инъекций, экранируем 34 " 39 ' 92 \
				if(arr[i] == 34 || arr[i] == 39 || arr[i] == 92)
					sb.append(_92).append(arr[i]);
				else
					sb.append(arr[i]);
			val = sb.toString();
		}
		*/
		myStatement.setString(index, val);
	}

	public void setLong(int index, long val) throws SQLException
	{
		myStatement.setLong(index, val);
	}

	public void setNull(int index, int val) throws SQLException
	{
		myStatement.setNull(index, val);
	}

	public void setDouble(int index, double val) throws SQLException
	{
		myStatement.setDouble(index, val);
	}

	public void setBytes(int index, byte[] data) throws SQLException
	{
		myStatement.setBytes(index, data);
	}

	public int executeUpdate() throws SQLException
	{
		return myStatement.executeUpdate();
	}

	public void setBoolean(int index, boolean val) throws SQLException
	{
		myStatement.setBoolean(index, val);
	}

	public void setEscapeProcessing(boolean val) throws SQLException
	{
		myStatement.setEscapeProcessing(val);
	}

	public void setByte(int index, byte val) throws SQLException
	{
		myStatement.setByte(index, val);
	}

	public void setVars(Object... vars) throws SQLException
	{
		Number n;
		long long_val;
		double double_val;
		for(int i = 0; i < vars.length; i++)
		{
			if(vars[i] instanceof Number)
			{
				n = (Number) vars[i];
				long_val = n.longValue();
				double_val = n.doubleValue();
				if(long_val == double_val)
					setLong(i + 1, long_val);
				else
					setDouble(i + 1, double_val);
			}
			else if(vars[i] instanceof String)
				setString(i + 1, (String) vars[i]);
		}
	}
}