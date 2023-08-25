package l2open.gameserver;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.network.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.network.L2GameClient;
import l2open.util.Log;

import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.sql.ResultSet;
import java.util.logging.Logger;

import javolution.util.FastMap;

import java.net.InetAddress;
import java.net.UnknownHostException;

// TODO: сделать очистку листа конектов
public class SelectorHelperGS implements IAcceptFilter<L2GameClient>
{
	private static final Logger _log = Logger.getLogger(SelectorHelperGS.class.getName());
	private Map<InetAddress, ConnectInfo> _accept_list = new FastMap<InetAddress, ConnectInfo>().setShared(true);
	private Map<InetAddress, BanInfo> _bannedIps = new FastMap<InetAddress, BanInfo>().setShared(true);
	private ConcurrentHashMap<L2GameClient, ClientInfo> _client_list = new ConcurrentHashMap<L2GameClient, ClientInfo>();

	public SelectorHelperGS()
	{
		loadBanList();
	}

	@Override
	public boolean accept(SocketChannel sc)
	{
		if(!ConfigValue.GameServerProtectAcceptEnable)
			return true;
		InetAddress address = sc.socket().getInetAddress();
		boolean is_banned = isBannedAddress(address);
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

		if(connect_count >= ConfigValue.GameServerConnectCount)
		{
			Log.add("DDOS IP: " + address.getHostAddress(), "logins_ip_ddos");
			BanIp(address, -1, "DDOS ATTACK: pps="+connect_count);
			return false;
		}
		return true;
	}

	@Override
	public void addConnect(L2GameClient client)
	{
		ClientInfo ci = new ClientInfo(client);
		_client_list.put(client, ci);

		client._c_info = ci;
	}

	@Override
	public void removeConnect(L2GameClient client)
	{
		_client_list.remove(client);
	}

	@Override
	public void incReceivablePacket(ReceivablePacket rp, L2GameClient client)
	{
		if(rp == null || rp.isFilter())
			client._c_info.increaseCounter();
	}

	@Override
	public void incSendablePacket(SendablePacket sp, L2GameClient client)
	{
		//closeConnectionImpl(client);
	}

	private void closeConnectionImpl(L2GameClient client)
	{
		L2Player activeChar = client.getActiveChar();
		if(activeChar != null)
			activeChar.logout(false, false, true, true);
		else
			client.closeNow(true);
	}
	// ----------------------------------------------------------
	public class ClientInfo
	{
		private L2GameClient _client;
		private final AtomicLong _atomic_count = new AtomicLong();
		private final AtomicLong _atomic_time = new AtomicLong();

		private final AtomicLong _atomic_count_pps = new AtomicLong();
		private final AtomicLong _atomic_time_pps = new AtomicLong();

		public ClientInfo(L2GameClient client)
		{
			_client = client;
			_atomic_count.getAndSet(1);
			_atomic_count_pps.getAndSet(1);
			_atomic_time.getAndSet(System.currentTimeMillis());
			_atomic_time_pps.getAndSet(System.currentTimeMillis());
		}

		public void increaseCounter()
		{
			// считаем PPS
			if(System.currentTimeMillis() - _atomic_time.get() < ConfigValue.GameServerPacketTryCheckDuration)
				_atomic_count.incrementAndGet();
			else
				_atomic_count.getAndSet(1);
			_atomic_time.getAndSet(System.currentTimeMillis());

			// количество пакетов за GameServerPacketTryCheckDuration времени
			long packet_count = getAtomicCount();
			if(packet_count >= ConfigValue.GameServerPacketCount)
			{
				_atomic_count.getAndSet(1);

				// количество овер пакетов за GameServerPPSTryCheckDuration времени
				if(System.currentTimeMillis() - _atomic_time_pps.get() < ConfigValue.GameServerPPSTryCheckDuration)
					_atomic_count_pps.incrementAndGet();
				else
					_atomic_count_pps.getAndSet(1);
				_atomic_time_pps.getAndSet(System.currentTimeMillis());

				Log.add("DDOS CLIENT: PS:"+packet_count+": " + _client, "client_packet_pps");
				// количество овер PPS
				long fail_count = getAtomicCountPPS();
				if(fail_count >= ConfigValue.GameServerPPSCount)
				{
					Log.add("DDOS CLIENT: PPS:"+fail_count+": " + _client, "client_packet_ddos");
					closeConnectionImpl(_client);
					
					BanIp(_client.getConnection().getSocket().getInetAddress(), -1, "DDOS CLIENT: pps="+fail_count);
				}
			}
		}

		public long getAtomicCount()
		{
			return _atomic_count.longValue();
		}

		public long getAtomicCountPPS()
		{
			return _atomic_count_pps.longValue();
		}
	}
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
			if (System.currentTimeMillis() - _lastAttempTime < ConfigValue.GameServerTryCheckDuration)
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
	public class BanInfo
	{
		private InetAddress _ipAddress;
		private long _expiration;

		public BanInfo(InetAddress ipAddress, long expiration)
		{
			_ipAddress = ipAddress;
			_expiration = expiration;
		}

		public InetAddress getAddress()
		{
			return _ipAddress;
		}

		public boolean hasExpired()
		{
			return System.currentTimeMillis() > _expiration;
		}
	}
	// ----------------------------------------------------------
	@Override
	public void BanIp(InetAddress address, int time, String comments)
	{
		addBanForAddress(address, ConfigValue.GameServerConnectBanTime * 1000);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			long expiretime = 0;
			if(time != 0)
				expiretime = System.currentTimeMillis() / 1000 + time;
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("REPLACE INTO ddos_ips (ip,admin,expiretime,comments) values(?,?,?,?)");
			statement.setString(1, address.getHostAddress());
			statement.setString(2, "AUTO_BAN_GS");
			statement.setLong(3, expiretime);
			statement.setString(4, comments);
			statement.execute();
			//_log.warning("Banning ip: " + ip + " for " + time + " seconds.");
		}
		catch(Exception e)
		{
			//_log.info("error1 while reading ddos_ips");
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
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT ip,admin FROM ddos_ips");
			rset = statement.executeQuery();
			while(rset.next())
				addBanForAddress(rset.getString("ip"), System.currentTimeMillis()+ConfigValue.GameServerConnectBanTime * 1000);
		}
		catch(Exception e)
		{
			//_log.info("error5 while reading ddos_ips");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
			_log.info("Loaded "+getBannedIps().size()+ " black ips.");
		}
	}
	// ----------------------------------------------------------
	public void addBanForAddress(String address, long expiration) throws UnknownHostException
	{
		InetAddress netAddress = InetAddress.getByName(address);
		_bannedIps.put(netAddress, new BanInfo(netAddress, expiration));
	}

	public void addBanForAddress(InetAddress address, long duration)
	{
		_bannedIps.put(address, new BanInfo(address, System.currentTimeMillis() + duration));
	}

	public boolean isBannedAddress(InetAddress address)
	{
		BanInfo bi = _bannedIps.get(address);
		if(bi != null)
		{
			if(bi.hasExpired())
			{
				_bannedIps.remove(address);
				return false;
			}
			return true;
		}
		return false;
	}

	public Map<InetAddress, BanInfo> getBannedIps()
	{
		return _bannedIps;
	}

	public boolean removeBanForAddress(InetAddress address)
	{
		return _bannedIps.remove(address) != null;
	}
}