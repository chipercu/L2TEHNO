package l2open.loginserver;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.network.*;
import l2open.loginserver.L2LoginClient;
import l2open.util.Log;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.sql.ResultSet;
import java.util.logging.Logger;

import javolution.util.FastMap;

import java.net.InetAddress;

public class SelectorHelper implements IAcceptFilter<L2LoginClient>
{
	private static final Logger _log = Logger.getLogger(SelectorHelper.class.getName());
	private Map<InetAddress, ConnectInfo> _accept_list = new FastMap<InetAddress, ConnectInfo>().setShared(true);

	public SelectorHelper()
	{
		loadBanList();
	}

	@Override
	public boolean accept(SocketChannel sc)
	{
		InetAddress address = sc.socket().getInetAddress();
		boolean is_banned = LoginController.getInstance().isBannedAddress(address);
		if(is_banned)
			return false;

		ConnectInfo c_info = _accept_list.get(address);
		long connect_count = 1;
		if(c_info == null)
			_accept_list.put(address, new ConnectInfo(address));
		else 
		{
			c_info.increaseCounter();
			connect_count = c_info.getCount();
		}

		if(connect_count >= ConfigValue.LoginServerConnectCount)
		{
			Log.add("DDOS IP: " + address.getHostAddress(), "logins_ip_ddos");
			BanIp(address, -1, "DDOS ATTACK: pps="+connect_count);
			return false;
		}
		return true;
	}
	public void incReceivablePacket(ReceivablePacket rp, L2LoginClient client){}
	public void incSendablePacket(SendablePacket sp, L2LoginClient client){}
	public void addConnect(L2LoginClient client){}
	public void removeConnect(L2LoginClient client){}

	// ----------------------------------------------------------
	class ConnectInfo
	{
		private InetAddress _ipAddress;
		private long _count;
		private long _lastAttempTime;

		public ConnectInfo(InetAddress address)
		{
			_ipAddress = address;
			_count = 1;
			_lastAttempTime = System.currentTimeMillis();
		}

		public void increaseCounter()
		{
			if (System.currentTimeMillis() - _lastAttempTime < ConfigValue.LoginServerTryCheckDuration)
				_count++;
			else
				_count = 1;
			_lastAttempTime = System.currentTimeMillis();
		}

		public long getCount()
		{
			return _count;
		}
	}
	// ----------------------------------------------------------
	@Override
	public void BanIp(InetAddress address, int time, String comments)
	{
		LoginController.getInstance().addBanForAddress(address, ConfigValue.LoginServerConnectBanTime * 1000);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			long expiretime = 0;
			if(time != 0)
				expiretime = System.currentTimeMillis() / 1000 + time;
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO ddos_ips (ip,admin,expiretime,comments) values(?,?,?,?)");
			statement.setString(1, address.getHostAddress());
			statement.setString(2, "AUTO_BAN");
			statement.setLong(3, expiretime);
			statement.setString(4, comments);
			statement.execute();
			//_log.warning("Banning ip: " + ip + " for " + time + " seconds.");
		}
		catch(Exception e)
		{
			_log.info("error1 while reading ddos_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void loadBanList()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT ip,admin FROM ddos_ips");
			rset = statement.executeQuery();
			while(rset.next())
				LoginController.getInstance().addBanForAddress(rset.getString("ip"), System.currentTimeMillis()+ConfigValue.LoginServerConnectBanTime * 1000);
		}
		catch(Exception e)
		{
			_log.info("error5 while reading ddos_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
			_log.info("Loaded "+LoginController.getInstance().getBannedIps().size()+ " black ips.");
		}
	}
}