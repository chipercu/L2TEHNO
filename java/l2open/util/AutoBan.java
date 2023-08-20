package l2open.util;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.tables.player.PlayerData;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public final class AutoBan
{
	private static Logger _log = Logger.getLogger(AutoBan.class.getName());

	public static boolean isBanned(int ObjectId)
	{
		boolean res = false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT MAX(endban) AS endban FROM bans WHERE obj_Id=? AND endban IS NOT NULL");
			statement.setInt(1, ObjectId);
			rset = statement.executeQuery();

			if(rset.next())
			{
				Long endban = rset.getLong("endban") * 1000L;
				res = endban > System.currentTimeMillis();
			}
		}
		catch(Exception e)
		{
			_log.warning("Could not restore ban data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return res;
	}

	public static void Banned(L2Player actor, int period, String msg, String GM)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			int endban = 0;
			if(period == -1)
				endban = Integer.MAX_VALUE;
			else if(period > 0)
			{
				Calendar end = Calendar.getInstance();
				end.add(Calendar.DAY_OF_MONTH, period);
				endban = (int) (end.getTimeInMillis() / 1000);
			}
			else
			{
				_log.warning("Negative ban period: " + period);
				return;
			}

			String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
			String enddate = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date(endban * 1000L));
			if(endban * 1000L <= Calendar.getInstance().getTimeInMillis())
			{
				_log.warning("Negative ban period | From " + date + " to " + enddate);
				return;
			}

			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?,?)");
			statement.setString(1, actor.getAccountName());
			statement.setInt(2, actor.getObjectId());
			statement.setString(3, date);
			statement.setString(4, enddate);
			statement.setString(5, msg);
			statement.setString(6, GM);
			statement.setLong(7, endban);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("could not store bans data:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static void UnBanned(String actor)
	{
		int obj_id = Util.GetCharIDbyName(actor);
		boolean res = obj_id > 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		if(res)
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET accesslevel=0 WHERE obj_Id=?");
				statement.setInt(1, obj_id);
				statement.executeUpdate();
				DatabaseUtils.closeStatement(statement);
				statement = con.prepareStatement("DELETE FROM bans WHERE obj_id=?");
				statement.setInt(1, obj_id);
				statement.execute();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.warning("could not store unbans data:" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
	}

	//offline
	public static boolean Banned(String actor, int acc_level, int period, String msg, String GM)
	{
		boolean res;
		int obj_id = Util.GetCharIDbyName(actor);
		res = obj_id > 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		if(res)
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE obj_Id=?");
				statement.setInt(1, acc_level);
				statement.setInt(2, obj_id);
				statement.executeUpdate();
				DatabaseUtils.closeStatement(statement);
				if(acc_level < 0)
				{
					int endban = 0;
					if(period == -1)
						endban = Integer.MAX_VALUE;
					else if(period > 0)
					{
						Calendar end = Calendar.getInstance();
						end.add(Calendar.DAY_OF_MONTH, period);
						endban = (int) (end.getTimeInMillis() / 1000);
					}
					else
					{
						_log.warning("Negative ban period: " + period);
						return false;
					}

					String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
					String enddate = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date(endban * 1000L));
					if(endban * 1000L <= Calendar.getInstance().getTimeInMillis())
					{
						_log.warning("Negative ban period | From " + date + " to " + enddate);
						return false;
					}

					statement = con.prepareStatement("INSERT INTO bans (obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?)");
					statement.setInt(1, obj_id);
					statement.setString(2, date);
					statement.setString(3, enddate);
					statement.setString(4, msg);
					statement.setString(5, GM);
					statement.setLong(6, endban);
					statement.execute();
				}
				else
				{
					statement = con.prepareStatement("DELETE FROM bans WHERE obj_id=?");
					statement.setInt(1, obj_id);
					statement.execute();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.warning("could not store bans data:" + e);
				res = false;
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		return res;
	}

	public static void Karma(L2Player actor, int karma, String msg, String GM)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
			msg = "Add karma(" + karma + ") " + msg;
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, reason, GM) VALUES(?,?,?,?,?)");
			statement.setString(1, actor.getAccountName());
			statement.setInt(2, actor.getObjectId());
			statement.setString(3, date);
			statement.setString(4, msg);
			statement.setString(5, GM);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("could not store bans data:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	//offline
	public static boolean Karma(String actor, int karma, String msg, String GM)
	{
		boolean res;
		int obj_id = Util.GetCharIDbyName(actor);
		res = obj_id > 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		if(res)
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();

				statement = con.prepareStatement("update characters set karma=karma + ? where obj_Id=?");
				statement.setInt(1, karma);
				statement.setInt(2, obj_id);
				statement.execute();
				DatabaseUtils.closeStatement(statement);

				String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
				msg = "Add karma(" + karma + ") " + msg;
				statement = con.prepareStatement("INSERT INTO bans (obj_id, baned, reason, GM) VALUES(?,?,?,?)");
				statement.setInt(1, obj_id);
				statement.setString(2, date);
				statement.setString(3, msg);
				statement.setString(4, GM);
				statement.execute();
			}
			catch(Exception e)
			{
				_log.warning("could not store bans data:" + e);
				res = false;
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		return res;
	}

	public static void Banned(L2Player actor, int period, String msg)
	{
		Banned(actor, period, msg, "AutoBan");
	}

	public static boolean ChatBan(String actor, int period, String msg, String GM)
	{
		boolean res = true;
		long NoChannel = period * 60000;
		int obj_id = Util.GetCharIDbyName(actor);
		if(obj_id == 0)
			return false;
		L2Player plyr = L2World.getPlayer(actor);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		if(plyr != null)
		{

			plyr.sendMessage(new CustomMessage("l2open.Util.AutoBan.ChatBan", actor).addString(GM).addNumber(period));
			PlayerData.getInstance().updateNoChannel(plyr, NoChannel);
		}
		else
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
				statement.setLong(1, NoChannel > 0 ? NoChannel / 1000 : NoChannel);
				statement.setInt(2, obj_id);
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				res = false;
				_log.warning("Could not activate nochannel:" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

		return res;
	}

	public static boolean ChatUnBan(String actor, String GM)
	{
		boolean res = true;
		L2Player plyr = L2World.getPlayer(actor);
		int obj_id = Util.GetCharIDbyName(actor);
		if(obj_id == 0)
			return false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		if(plyr != null)
		{
			plyr.sendMessage(new CustomMessage("l2open.Util.AutoBan.ChatUnBan", actor).addString(GM));
			PlayerData.getInstance().updateNoChannel(plyr, 0);
		}
		else
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
				statement.setLong(1, 0);
				statement.setInt(2, obj_id);
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				res = false;
				_log.warning("Could not activate nochannel:" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

		return res;
	}

	public static void UnBannedAcc(String account)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET access_level = '0', comments = \"\", banExpires = '0' WHERE login=?");
			statement.setString(1, account);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.warning("Could not set accessLevel: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}