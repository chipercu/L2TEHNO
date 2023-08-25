package l2open.loginserver.gameservercon;

import javolution.util.FastList;

import l2open.gameserver.loginservercon.AdvIP;
import l2open.loginserver.GameServerTable;
import l2open.loginserver.GameInfo;
import l2open.loginserver.Watchdog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class GameServerInfo
{
	private static Logger log = Logger.getLogger(GameServerInfo.class.getName());
	public ConcurrentHashMap<String, Integer> _online_ip = new ConcurrentHashMap<String, Integer>();

	// auth
	private int _id;
	private byte[] _hexId;
	private boolean _isAuthed;

	// status
	private AttGS _gst;
	private boolean _online;

	// network
	private String _internalHost, _externalHost;
	private final PortListSelector _ports = new PortListSelector();
	private int _port;

	// config
	private boolean _isPvp = true;
	private boolean _isTestServer;
	private boolean _isGMOnly;
	private boolean _isShowingClock;
	private boolean _isShowingBrackets;
	private int _mask = 0;

	private int _maxPlayers;
	private FastList<AdvIP> _ips;

	public GameInfo gi = null;

	public GameServerInfo(int id, byte[] hexId, AttGS gameserver)
	{
		_id = id;
		_hexId = hexId;
		_gst = gameserver;

		if(GameServerTable._game_proxy.containsKey(id))
			gi = GameServerTable._game_proxy.get(id);
	}

	public GameServerInfo(int id, byte[] hexId)
	{
		this(id, hexId, null);
	}

	public void setId(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public byte[] getHexId()
	{
		return _hexId;
	}

	public void setAuthed(boolean isAuthed)
	{
		_isAuthed = isAuthed;
	}

	public boolean isAuthed()
	{
		return _isAuthed;
	}

	public void setGameServer(AttGS gameserver)
	{
		_gst = gameserver;
	}

	public AttGS getGameServer()
	{
		return _gst;
	}

	public void setGMOnly(boolean status)
	{
		_isGMOnly = status;
	}

	public boolean isGMOnly()
	{
		return _isGMOnly;
	}

	public int getCurrentPlayerCount()
	{
		if(_gst == null)
			return 0;
		return _gst.getPlayerCount();
	}

	public int getCurrentPlayerCount(String ip)
	{
		try
		{
		if(_gst == null || !_online_ip.containsKey(ip))
			return 0;
		}
		catch(Exception e)
		{
			return 0;
		}
		return _online_ip.get(ip);
	}

	public String getInternalHost()
	{
		return _internalHost;
	}

	public void setInternalHost(String internalHost)
	{
		_internalHost = internalHost;
	}

	public void setExternalHost(String externalHost)
	{
		_externalHost = externalHost;
	}

	public String getExternalHost()
	{
		return _externalHost;
	}

	public int getPort()
	{
		return _ports.next();
	}

	public void setPorts(int[] ports)
	{
		_ports.set(ports);
	}

	public void setMaxPlayers(int maxPlayers)
	{
		_maxPlayers = maxPlayers;
	}

	public int getMaxPlayers()
	{
		return _maxPlayers;
	}

	public boolean isPvp()
	{
		return _isPvp;
	}

	public void setBitmask(int val)
	{
		_mask = val;
	}

	public int getBitMask()
	{
		return _mask;
	}

	public void setTestServer(boolean val)
	{
		_isTestServer = val;
	}

	public boolean isTestServer()
	{
		return _isTestServer;
	}

	public void setShowingClock(boolean clock)
	{
		_isShowingClock = clock;
	}

	public boolean isShowingClock()
	{
		return _isShowingClock;
	}

	public void setShowingBrackets(boolean val)
	{
		_isShowingBrackets = val;
	}

	public boolean isShowingBrackets()
	{
		return _isShowingBrackets;
	}

	public void setAdvIP(FastList<AdvIP> val)
	{
		_ips = val;
	}

	public FastList<AdvIP> getAdvIP()
	{
		return _ips;
	}

	public void setDown()
	{
		log.info("Setting GameServer down");
		setAuthed(false);
		_ports.set(null);
		setGameServer(null);
		_online = false;
	}

	@Override
	public String toString()
	{
		StringBuilder tb = new StringBuilder();

		tb.append("GameServer: ");
		if(_gst != null)
		{
			tb.append(_gst.getName());
			tb.append(" id:");
			tb.append(_id);
			tb.append(" hex:");
			tb.append(_hexId);
			tb.append(" ip:");
			tb.append(_gst.getConnectionIpAddress());
			tb.append(":");
			tb.append(_port);
			tb.append(" online: ");
			tb.append(_online);
		}
		else
		{
			tb.append(GameServerTable.getInstance().getServerNames().get(_id));
			tb.append(" id:");
			tb.append(_id);
			tb.append(" hex:");
			tb.append(_hexId);
			tb.append(" online: ");
			tb.append(_online);
		}

		return tb.toString();
	}

	public void setGameHosts(String gameExternalHost, String gameInternalHost, FastList<AdvIP> ips)
	{
		setExternalHost(gameExternalHost);
		setInternalHost(gameInternalHost);
		if(!getExternalHost().equals("*"))
			try
			{
				InetAddress.getByName(getExternalHost()).getHostAddress();
			}
			catch(UnknownHostException e)
			{
				e.printStackTrace();
			}

		if(!getInternalHost().equals("*"))
			try
			{
				InetAddress.getByName(getInternalHost()).getHostAddress();
			}
			catch(UnknownHostException e)
			{
				e.printStackTrace();
			}

		setAdvIP(ips);

		//Watchdog.init();

		log.info("Updated Gameserver " + GameServerTable.getInstance().getServerNameById(getId()) + " Hostname's:");
		log.info("InternalHostname: " + getInternalHost());
		log.info("ExternalHostname: " + getExternalHost());
	}

	static class PortListSelector
	{
		private static int[] nullports = new int[0];
		private int cursor;
		private int[] ports = nullports;

		public synchronized void set(int[] _ports)
		{
			ports = _ports == null ? nullports : _ports;
			cursor = 0;
		}

		public synchronized int next()
		{
			if(ports.length == 0)
				return 0;
			if(cursor >= ports.length)
				cursor = 0;
			return ports[cursor++];
		}
	}

	public boolean isOnline()
	{
		return _online;
	}

	public void setOnline(boolean online)
	{
		_online = online;
	}
}