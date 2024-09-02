package l2open.loginserver.serverpackets;

import javolution.util.FastList;

import l2open.config.ConfigValue;
import l2open.gameserver.loginservercon.AdvIP;
import l2open.loginserver.*;
import l2open.loginserver.gameservercon.GameServerInfo;
import l2open.loginserver.gameservercon.gspackets.ServerStatus;
import l2open.util.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import java.util.logging.Logger;

/**
 * ServerList
 * Format: cc [cddcchhcdc]
 *
 * c: server list size (number of servers)
 * c: last server
 * [ (repeat for each servers)
 * c: server id (ignored by client?)
 * d: server ip
 * d: server port
 * c: age limit (used by client?)
 * c: pvp or not (used by client?)
 * h: current number of players
 * h: max number of players
 * c: 0 if server is down
 * d: 2nd bit: clock
 *    3rd bit: wont dsiplay server name
 *    4th bit: test server (used by client?)
 * c: 0 if you dont want to display brackets in front of sever name
 * ]
 *
 * Server will be considered as Good when the number of  online players
 * is less than half the maximum. as Normal between half and 4/5
 * and Full when there's more than 4/5 of the maximum number of players
 */
public final class ServerList extends L2LoginServerPacket
{
	//private static final Logger _log = Logger.getLogger(ServerList.class.getName());
	private List<ServerData> _servers;
	private int _lastServer;
	private int _paddedBytes;
	private Map<Integer, Integer> _charsOnServers;
	private Map<Integer, long[]> _charsToDelete;

	class ServerData
	{
		String ip;
		int port;
		boolean pvp;
		int currentPlayers;
		int maxPlayers;
		int bits;

		boolean brackets;
		boolean online;
		public int server_id;

		ServerData(String pIp, int pPort, boolean pPvp, int pCurrentPlayers, int pMaxPlayers, boolean pBrackets, boolean pStatus, int pServer_id, int bit)
		{
			ip = pIp;
			port = pPort;
			pvp = pPvp;
			currentPlayers = pCurrentPlayers;
			maxPlayers = pMaxPlayers;
			brackets = pBrackets;
			online = pStatus;
			server_id = pServer_id;
			bits = bit;
		}
	}

	public ServerList(L2LoginClient client)
	{
		_paddedBytes = 1;

		_servers = new FastList<ServerData>();
		_lastServer = client.getLastServer();
		_charsOnServers = client.getCharsOnServ();
		_charsToDelete = client.getCharsWaitingDelOnServ();
		for(GameServerInfo gsi : GameServerTable.getInstance().getRegisteredGameServers().values())
		{
			boolean added = false;
			if(client.getIpAddress().equals("Null IP"))
				continue;
			String ipAddr = Util.isInternalIP(client.getIpAddress()) ? gsi.getInternalHost() : gsi.getExternalHost();
			if(ipAddr == null || ipAddr.equals("Null IP"))
				continue;

			boolean online = gsi.isOnline();
			if(gsi.isGMOnly() && client.getAccessLevel() <= 0)
				online = false;

			//_paddedBytes += (3 + (4 * deleteChars.length));
			if(ConfigValue.LoginProxyEnable && gsi.gi != null)
			{
				for(Entry<Integer, Proxy> entry : gsi.gi._proxy.entrySet())
				{
					int id = entry.getKey();
					Proxy p = entry.getValue();

					//_log.info("id="+id+" p.region="+p.region+" client.region="+client.region+" online="+online);
					if(p.region == -1 || p.region == client.region || p.region == client.proxy_add)
						addServer(p.ip, gsi.getPort(), gsi.isPvp(), gsi.getCurrentPlayerCount(p.ip_in), p.max_player, gsi.isShowingBrackets(), online, id, p.bit_mask);
				}
			}
			else
			{
				if(gsi.getAdvIP() != null)
					for(AdvIP ip : gsi.getAdvIP())
					{
						if(ConfigValue.AltAdvIPSystem)
						{
							added = true;
							if(ip.ipadress.contains(":"))
								addServer(ip.ipadress.split(":")[0], Integer.parseInt(ip.ipadress.split(":")[1]), gsi.isPvp(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), online, Integer.parseInt(ip.ipadress.split(":")[2]), ip.ipadress.split(":").length == 4 ? Integer.parseInt(ip.ipadress.split(":")[3]) : gsi.getBitMask());
							else
								addServer(ip.ipadress, gsi.getPort(), gsi.isPvp(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), online, gsi.getId(), gsi.getBitMask());
						}
						else if(!added && GameServerTable.getInstance().CheckSubNet(client.getConnection().getSocket().getInetAddress().getHostAddress(), ip))
						{
							added = true;
							addServer(ip.ipadress, gsi.getPort(), gsi.isPvp(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), online, gsi.getId(), gsi.getBitMask());
						}
					}
				if(!added || client.getAccessLevel() == 99)
					if(ipAddr.equals("*"))
						addServer(client.getConnection().getSocket().getLocalAddress().getHostAddress(), gsi.getPort(), gsi.isPvp(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), online, gsi.getId(), gsi.getBitMask());
					else
						addServer(ipAddr, gsi.getPort(), gsi.isPvp(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), online, gsi.getId(), gsi.getBitMask());
			}
		}
		//addServer("134.249.131.72", 7778, false, 11, 1111, false, true, 1, 0);
		//addServer("134.249.131.72", 7778, false, 11, 1111, false, true, 1, 0);
	}

	public void addServer(String ip, int port, boolean pvp, int currentPlayer, int maxPlayer, boolean brackets, boolean status, int server_id, int bit)
	{
		try
		{
		_log.info("addServer: ip="+ip+"["+InetAddress.getByName(ip)+"] port="+port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		_servers.add(new ServerData(ip, port, pvp, currentPlayer, maxPlayer, brackets, status, server_id, bit));
	}

	/*@Override
	public void write_impl()
	{
		writeC(0x04);
		writeC(2);
		writeC(_lastServer);
		// for
			writeC(1); // server id

			writeC(127);
			writeC(0);
			writeC(0);
			writeC(1);

			writeD(7778);
			writeC(18); // age limit
			writeC(0x00);
			writeH(0x00);
			writeH(1000);
			writeC(0x01);
			writeD(0x00);
			writeC(0x00);
		// for
			writeC(2); // server id

			writeC(127);
			writeC(0);
			writeC(0);
			writeC(1);

			writeD(7778);
			writeC(18); // age limit
			writeC(0x00);
			writeH(0x00);
			writeH(1000);
			writeC(0x01);
			writeD(0x00);
			writeC(0x00);
		// for
		writeH(0);
		writeC(0);
	}*/
	@Override
	public void write_impl()
	{
		writeC(0x04);
		writeC(_servers.size());
		writeC(_lastServer);
		for(ServerData server : _servers)
		{
			writeC(server.server_id); // server id

			try
			{
				InetAddress i4 = InetAddress.getByName(server.ip);
				byte[] raw = i4.getAddress();
				writeC(raw[0] & 0xff);
				writeC(raw[1] & 0xff);
				writeC(raw[2] & 0xff);
				writeC(raw[3] & 0xff);
			}
			catch(UnknownHostException e)
			{
				e.printStackTrace();
				writeC(127);
				writeC(0);
				writeC(0);
				writeC(1);
			}

			writeD(server.port - 1);
			writeC(18); // age limit
			writeC(server.pvp ? 0x01 : 0x00);
			writeH(server.currentPlayers);
			writeH(server.maxPlayers);
			writeC(server.online ? 1 : 0);
			writeD(server.bits);
			writeC(server.brackets ? 0x01 : 0x00);
		}
		writeH(0);
		if(_charsOnServers != null)
		{
			writeC(_servers.size());
			for(ServerData server : _servers)
			{
				writeC(server.server_id);
				int _count = _charsOnServers.get(server.server_id) == null ? 0 : _charsOnServers.get(server.server_id);
				writeC(_count);
				if(_charsToDelete == null || !_charsToDelete.containsKey(server.server_id))
					writeC(0);
				else
				{
					writeC(_charsToDelete.get(server.server_id).length);
					for(long deleteTime : _charsToDelete.get(server.server_id))
						writeD((int)((deleteTime - System.currentTimeMillis()) / 1000));
				}
			}
		}
		else
			writeC(0);
	}
}