package l2open.loginserver;

import javolution.util.FastList;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.util.BannedIp;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class IpManager
{
	private static final Logger _log = Logger.getLogger(IpManager.class.getName());
	private static final IpManager _instance = new IpManager();

	public static IpManager getInstance()
	{
		return _instance;
	}

	public IpManager()
	{}

	public void BanIp(String ip, String admin, int time, String comments)
	{
		if(CheckIp(ip))
			return;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			long expiretime = 0;
			if(time != 0)
				expiretime = System.currentTimeMillis() / 1000 + time;
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO banned_ips (ip,admin,expiretime,comments) values(?,?,?,?)");
			statement.setString(1, ip);
			statement.setString(2, admin);
			statement.setLong(3, expiretime);
			statement.setString(4, comments);
			statement.execute();
			_log.warning("Banning ip: " + ip + " for " + time + " seconds.");
		}
		catch(Exception e)
		{
			_log.info("error1 while reading banned_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
			Log.LoginLog(Log.Login_BanIp, "", ip, admin, 0, 0);
		}
	}

	public void UnbanIp(String ip)
	{
		//		who`s care exist ban or not? ;)
		//		if(!CheckIp(ip))
		//			return;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM banned_ips WHERE ip=?");
			statement.setString(1, ip);
			statement.execute();
			_log.warning("Removed ban for ip: " + ip);
		}
		catch(Exception e)
		{
			_log.info("error2 while reading banned_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
			Log.LoginLog(Log.Login_UnBanIp, "", ip, "", 0, 0);
		}
	}

	public boolean CheckIp(String ip)
	{
		String[] netMy = ip.replace(".", ":").split(":");
		for(BannedIp bi : IpManager.getInstance().getBanList())
		{
			String[] net = bi.ip.replace(".", ":").split(":");
			if(net[0].equals("*") || netMy[0].equals(net[0]))
				if(net[1].equals("*") || netMy[1].equals(net[1]))
					if(net[2].equals("*") || netMy[2].equals(net[2]))
						if(net[3].equals("*") || netMy[3].equals(net[3]))
							return true;
		}
		return false;
	}

	public int getBannedCount()
	{
		int result = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT count(*) AS num FROM banned_ips");
			rset = statement.executeQuery();
			if(rset.next())
				result = rset.getInt("num");
		}
		catch(Exception e)
		{
			_log.info("error4 while reading banned_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return result;
	}

	public FastList<BannedIp> getBanList()
	{
		FastList<BannedIp> result = FastList.newInstance();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			BannedIp temp;
			statement = con.prepareStatement("SELECT ip,admin FROM banned_ips");
			rset = statement.executeQuery();
			while(rset.next())
			{
				temp = new BannedIp();
				temp.ip = rset.getString("ip");
				temp.admin = rset.getString("admin");
				result.add(temp);
			}
		}
		catch(Exception e)
		{
			_log.info("error5 while reading banned_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return result;
	}
}