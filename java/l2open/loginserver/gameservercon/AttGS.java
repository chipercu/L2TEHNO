package l2open.loginserver.gameservercon;

import javolution.util.FastList;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.loginserver.GameServerTable;
import l2open.loginserver.LoginController;
import l2open.loginserver.crypt.ConnectionCrypt;
import l2open.loginserver.crypt.ConnectionCryptDummy;
import l2open.loginserver.crypt.NewCrypt;
import l2open.loginserver.gameservercon.gspackets.ClientBasePacket;
import l2open.loginserver.gameservercon.lspackets.KickPlayer;
import l2open.loginserver.gameservercon.lspackets.ServerBasePacket;
import l2open.loginserver.gameservercon.lspackets.TestConnection;
import l2open.util.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.logging.Logger;

public class AttGS
{
	private static final Logger log = Logger.getLogger(AttGS.class.getName());

	private final ByteBuffer readBuffer = ByteBuffer.allocate(64 * 1024).order(ByteOrder.LITTLE_ENDIAN);
	private final ArrayDeque<ServerBasePacket> sendQueue = new ArrayDeque<ServerBasePacket>();
	private final FastList<String> accountsInGameServer = FastList.newInstance();

	private final SelectionKey key;
	private RSACrypt rsa;
	private ConnectionCrypt crypt;
	private int serverId = -1, ProtocolVersion = 0;
	private boolean _isAuthed;
	private GameServerInfo gameServerInfo;
	private long pingRequested = 0, lastPingResponse = 0;
	private int _online = 0;

	public AttGS(SelectionKey sc)
	{
		key = sc;
		new KeyTask(this);

		//if(ConfigValue.DEBUG_LS_GS)
		//	log.info("LS Debug: RSAKey task started");
	}

	private void requestPing()
	{
		pingRequested = System.currentTimeMillis();
		sendPacket(new TestConnection()); // посылаем пинг
	}

	public void notifyPingResponse()
	{
		lastPingResponse = System.currentTimeMillis();
		pingRequested = 0;
	}

	public boolean isPingTimedOut()
	{
		long now = System.currentTimeMillis();
		if(pingRequested == 0)
		{
			if(now - lastPingResponse > 1000)
				requestPing();
			return false;
		}

		return now - pingRequested > ConfigValue.LoginWatchdogTimeout;
	}

	public void sendPacket(ServerBasePacket packet)
	{
		try
		{
			if(!key.isValid())
				return;

			//if(ConfigValue.DEBUG_LS_GS)
			//	log.info("LS Debug: adding packet to sendQueue: " + packet.getClass().getName());

			synchronized (sendQueue)
			{
				sendQueue.add(packet);
			}
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);

			//if(ConfigValue.DEBUG_LS_GS)
			//	log.info("LS Debug: Packet added");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onClose()
	{
		try
		{
			if(isAuthed())
			{
				setAuthed(false);
				log.info("LoginServer: Connection with gameserver " + getServerId() + " [" + getName() + "] lost.");
			}

			clearAccountInGameServer();
			GSConnection.getInstance().removeGameserver(this);
			sendQueue.clear();
			if(gameServerInfo != null)
				gameServerInfo.setDown();
			gameServerInfo = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public ByteBuffer getReadBuffer()
	{
		return readBuffer;
	}

	public ArrayDeque<ServerBasePacket> getSendQueue()
	{
		return sendQueue;
	}

	public byte[] encrypt(byte[] data) throws IOException
	{
		if(crypt == null)
			return data;
		return crypt.crypt(data);
	}

	public byte[] decrypt(byte[] data) throws IOException
	{
		if(crypt == null)
			return data;
		return crypt.decrypt(data);
	}

	public void processData()
	{
		try
		{
			ByteBuffer buf = getReadBuffer();

			int position = buf.position();
			if(position < 2) // У нас недостаточно данных для получения длинны пакета
				return;

			// Получаем длинну пакета
			int lenght = Util.getPacketLength(buf.get(0), buf.get(1));

			// Пакетик не дошел целиком, ждем дальше
			if(lenght > position)
				return;

			byte[] data = new byte[position];
			for(int i = 0; i < position; i++)
				data[i] = buf.get(i);

			buf.clear();

			while((lenght = Util.getPacketLength(data[0], data[1])) <= data.length)
			{
				data = processPacket(data, lenght);
				if(data.length < 2)
					break;
			}

			buf.put(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private byte[] processPacket(byte[] data, int lenght)
	{
		try
		{
			byte[] remaining = new byte[data.length - lenght];
			byte[] packet = new byte[lenght - 2];

			System.arraycopy(data, 2, packet, 0, lenght - 2);
			System.arraycopy(data, lenght, remaining, 0, remaining.length);

			ClientBasePacket runnable = PacketHandler.handlePacket(packet, this);
			if(runnable != null)
			{
				if(ConfigValue.DEBUG_LS_GS)
					log.info("GameServer -> LoginServer [" + getServerId() + "]: " + runnable.getClass().getSimpleName());
				ThreadPoolManager.getInstance().execute(runnable);
			}

			return remaining;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new byte[] {};
		}
	}

	public void initBlowfish(byte[] key)
	{
		crypt = key == null ? ConnectionCryptDummy.instance : new NewCrypt(key);
		log.info("Init connection crypt for gameserver " + getConnectionIpAddress() + ": " + crypt.getClass().getSimpleName());
	}

	public byte[] RSADecrypt(byte[] data) throws Exception
	{
		return rsa.decryptRSA(data);
	}

	public byte[] RSAEncrypt(byte[] data) throws Exception
	{
		return rsa.encryptRSA(data);
	}

	public byte[] getRSAPublicKey()
	{
		return rsa.getRSAPublicKey();
	}

	public void setRSA(RSACrypt rsa)
	{
		this.rsa = rsa;
	}

	public int getServerId()
	{
		return serverId;
	}

	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}

	public boolean isAuthed()
	{
		return _isAuthed;
	}

	public void setAuthed(boolean authed)
	{
		_isAuthed = authed;
	}

	public void addAccountInGameServer(String account)
	{
		synchronized (accountsInGameServer)
		{
			if(accountsInGameServer.contains(account))
				return;
			accountsInGameServer.add(account);
		}
	}

	public void addAccountsInGameServer(String... accounts)
	{
		synchronized (accountsInGameServer)
		{
			for(String account : accounts)
				if(!accountsInGameServer.contains(account))
					accountsInGameServer.add(account);
		}
	}

	public void removeAccountFromGameServer(String account)
	{
		synchronized (accountsInGameServer)
		{
			accountsInGameServer.remove(account);
		}
	}

	public boolean isAccountInGameServer(String account)
	{
		synchronized (accountsInGameServer)
		{
			return accountsInGameServer.contains(account);
		}
	}

	public void clearAccountInGameServer()
	{
		synchronized (accountsInGameServer)
		{
			accountsInGameServer.clear();
		}
	}

	public int getPlayerCount()
	{
		return _online;
	}

	public void setPlayerCount(int i)
	{
		_online = i;
	}

	public int getProtocolVersion()
	{
		return ProtocolVersion;
	}

	public void setProtocolVersion(int ver)
	{
		ProtocolVersion = ver;
	}

	public GameServerInfo getGameServerInfo()
	{
		return gameServerInfo;
	}

	public void setGameServerInfo(GameServerInfo gameServerInfo)
	{
		this.gameServerInfo = gameServerInfo;
	}

	public String getName()
	{
		return GameServerTable.getInstance().getServerNames().get(getServerId());
	}

	public String getConnectionIpAddress()
	{
		SocketChannel channel = (SocketChannel) key.channel();
		return channel.socket().getInetAddress().getHostAddress();
	}

	public void kickPlayer(String account)
	{
		sendPacket(new KickPlayer(account));
		removeAccountFromGameServer(account);
		LoginController.getInstance().removeAuthedLoginClient(account);
	}

	public SelectionKey getSelectionKey()
	{
		return key;
	}

	public boolean isCryptInitialized()
	{
		return crypt != null;
	}

	@Override
	public String toString()
	{
		return "AttGS - " + gameServerInfo;
	}

	public void addPlayerCount(String ip, int count)
	{
		gameServerInfo._online_ip.put(ip, count);
	}

	public void clear()
	{
		gameServerInfo._online_ip.clear();
	}
}