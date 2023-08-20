package l2open.loginserver.gameservercon;

import javolution.util.FastList;
import l2open.config.ConfigValue;
import l2open.Server;
import l2open.loginserver.gameservercon.lspackets.ServerBasePacket;
import l2open.util.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @Author: Death
 * @Date: 12/11/2007
 * @Time: 17:08:29
 */
public class GSConnection extends Thread
{
	// Включение дебага: java -DenableDebugLsGs
	private static final Logger log = Logger.getLogger(GSConnection.class.getName());

	private static final GSConnection instance = new GSConnection();
	private static final FastList<AttGS> gameservers = FastList.newInstance();

	private Selector selector;
	private boolean shutdown;

	public static GSConnection getInstance()
	{
		return instance;
	}

	private GSConnection()
	{
		try
		{
			selector = Selector.open();
			ServerSocketChannel server = ServerSocketChannel.open();
			server.configureBlocking(false);

			int port = ConfigValue.LoginPort;
			String host = ConfigValue.LoginHost;

			server.socket().bind(host.equals("*") ? new InetSocketAddress(port) : new InetSocketAddress(InetAddress.getByName(host), port));
			server.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("LoginServer: Can't init GameServer Listener.");
			Server.exit(0, "LoginServer: Can't init GameServer Listener.");
		}

		//if(ConfigValue.DEBUG_LS_GS)
		//	log.info("LS Debug: Listening for gameservers.");
	}

	@Override
	public void run()
	{
		log.info("LoginServer: GS listener started.");
		Set<SelectionKey> keys;
		Iterator<SelectionKey> keys_iterator;
		SelectionKey key;
		int keyNum, opts;

		while(!isShutdown())
			try
			{
				keyNum = selector.selectNow();

				if(keyNum > 0)
				{
					keys = selector.selectedKeys();
					keys_iterator = keys.iterator();

					while(keys_iterator.hasNext())
					{
						key = keys_iterator.next();
						keys_iterator.remove();

						if(!key.isValid())
						{
							log.info("Login conect close 1.");
							close(key);
							continue;
						}

						opts = key.readyOps();

						//if(ConfigValue.DEBUG_LS_GS)
						//	log.info("LS Debug: Seletor: key selected, readyOpts: " + opts);

						switch(opts)
						{
							case SelectionKey.OP_CONNECT:
								log.info("Login conect close 2.");
								close(key);
								break;
							case SelectionKey.OP_ACCEPT:
								accept(key);
								break;
							case SelectionKey.OP_WRITE:
								write(key);
								break;
							case SelectionKey.OP_READ:
								read(key);
								break;
							case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
								write(key);
								read(key);
								break;
							default:
								log.severe("GSConnection: Unknow readyOpts: " + opts);
						}
					}
					keys.clear();
				}
				Thread.sleep(ConfigValue.GSLSConnectionSleep);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("LoginServer: GameServer Listener - NIO Down... Restarting...");
				Server.exit(2, "LoginServer: GameServer Listener - NIO Down... Restarting...");
			}
	}

	public void accept(SelectionKey key)
	{
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;

		try
		{
			sc = ssc.accept();
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			close(key);
			return;
		}

		SelectionKey gsKey = sc.keyFor(selector);
		gsKey.attach(new AttGS(gsKey));

		//if(ConfigValue.DEBUG_LS_GS)
		//	log.info("LS Debug: key accepted.");
	}

	public void read(SelectionKey key)
	{
		SocketChannel channel = (SocketChannel) key.channel();
		AttGS att = (AttGS) key.attachment();

		ByteBuffer readBuffer = att.getReadBuffer();

		int numRead;
		try
		{
			numRead = channel.read(readBuffer);
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			close(key);
			return;
		}

		if(numRead == -1)
		{
			log.info("Login conect close 1-1.");
			close(key);
		}

		if(numRead == 0)
			return;

		att.processData();

		//if(ConfigValue.DEBUG_LS_GS)
		//	log.info("LS Debug: Data readed.");
	}

	public void write(SelectionKey key)
	{
		AttGS att = (AttGS) key.attachment();
		SocketChannel channel = (SocketChannel) key.channel();

		ArrayDeque<ServerBasePacket> sendQueue = att.getSendQueue();
		synchronized (sendQueue)
		{
			ServerBasePacket packet;
			while((packet = sendQueue.poll()) != null)
				try
				{
					byte[] data = att.encrypt(packet.getBytes());
					data = Util.writeLenght(data);
					channel.write(ByteBuffer.wrap(data));

					if(ConfigValue.DEBUG_LS_GS)
						log.info("LoginServer -> GameServer [" + att.getServerId() + "]: " + packet.getClass().getSimpleName());
				}
				catch(IOException e)
				{
					e.printStackTrace();
					close(key);
					return;
				}
		}

		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

		//if(ConfigValue.DEBUG_LS_GS)
		//	log.info("LS Debug: Data sended.");
	}

	public void close(SelectionKey key)
	{
		try
		{
			AttGS att = (AttGS) key.attachment();
			if(att != null)
				att.onClose();

			key.cancel();

			key.channel().close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//if(ConfigValue.DEBUG_LS_GS)
		//	log.info("LS Debug: Closing connection with GS.");
	}

	public void addGameServer(AttGS gs)
	{
		synchronized (gameservers)
		{
			gameservers.add(gs);
		}
	}

	public void removeGameserver(AttGS gs)
	{
		synchronized (gameservers)
		{
			log.info("Removing GameServer");
			gameservers.remove(gs);
		}
	}

	public boolean isShutdown()
	{
		return shutdown;
	}

	public void setShutdown(boolean shutdown)
	{
		this.shutdown = shutdown;
	}

	public void broadcastPacket(ServerBasePacket packet)
	{
		synchronized (gameservers)
		{
			for(AttGS gs : gameservers)
				gs.sendPacket(packet);
		}
	}

	public AttGS getGameServerByServerId(int id)
	{
		synchronized (gameservers)
		{
			for(AttGS gs : gameservers)
				if(gs.getServerId() == id)
					return gs;
		}
		return null;
	}
}
