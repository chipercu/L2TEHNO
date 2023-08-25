package l2open.extensions.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import java.util.logging.Logger;
import l2open.gameserver.network.L2GameClient;
import l2open.config.ConfigValue;
import l2open.util.Log;

@SuppressWarnings("unchecked")
public class SelectorThread<T extends MMOClient> extends Thread
{
	//private static final Logger _log = LoggerFactory.getLogger(SelectorThread.class);
	protected static final Logger _log = Logger.getLogger(SelectorThread.class.getName());

	private final Selector _selector;

	// Implementations
	private final IPacketHandler<T> _packetHandler;
	private final IMMOExecutor<T> _executor;
	private final IClientFactory<T> _clientFactory;
	private final IAcceptFilter _acceptFilter;

	// Configs
	private final int HELPER_BUFFER_SIZE;

	// MAIN BUFFERS
	private ByteBuffer DIRECT_WRITE_BUFFER;
	private final ByteBuffer WRITE_BUFFER, READ_BUFFER;
	private T WRITE_CLIENT;

	// ByteBuffers General Purpose Pool
	private final Queue<ByteBuffer> _bufferPool;
	private final List<MMOConnection<T>> _connections;

	public static SelectorStats _stats;

	private boolean _shutdown;

	public SelectorThread(SelectorStats stats, IPacketHandler<T> packetHandler, IMMOExecutor<T> executor, IClientFactory<T> clientFactory, IAcceptFilter acceptFilter) throws IOException
	{
		_selector = Selector.open();

		_stats = stats;
		_acceptFilter = acceptFilter;
		_packetHandler = packetHandler;
		_clientFactory = clientFactory;
		_executor = executor;

		_bufferPool = new ArrayDeque<ByteBuffer>(ConfigValue.HELPER_BUFFER_COUNT);
		_connections = new CopyOnWriteArrayList<MMOConnection<T>>();

		DIRECT_WRITE_BUFFER = ByteBuffer.wrap(new byte[ConfigValue.WRITE_BUFFER_SIZE]).order(ConfigValue.BYTE_ORDER);
		WRITE_BUFFER = ByteBuffer.wrap(new byte[ConfigValue.WRITE_BUFFER_SIZE]).order(ConfigValue.BYTE_ORDER);
		READ_BUFFER = ByteBuffer.wrap(new byte[ConfigValue.READ_BUFFER_SIZE]).order(ConfigValue.BYTE_ORDER);
		HELPER_BUFFER_SIZE = Math.max(ConfigValue.READ_BUFFER_SIZE, ConfigValue.WRITE_BUFFER_SIZE);

		for(int i = 0; i < ConfigValue.HELPER_BUFFER_COUNT; i++)
			_bufferPool.add(ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(ConfigValue.BYTE_ORDER));
	}

	public void openServerSocket(InetAddress address, int tcpPort) throws IOException
	{
		ServerSocketChannel selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);

		selectable.socket().bind(address == null ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort), ConfigValue.BACKLOG);
		selectable.register(getSelector(), selectable.validOps());
		setName("SelectorThread:" + selectable.socket().getLocalPort());
	}

	protected ByteBuffer getPooledBuffer()
	{
		if(_bufferPool.isEmpty())
			return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(ConfigValue.BYTE_ORDER);
		return _bufferPool.poll();
	}

	protected void recycleBuffer(ByteBuffer buf)
	{
		if(_bufferPool.size() < ConfigValue.HELPER_BUFFER_COUNT)
		{
			buf.clear();
			_bufferPool.add(buf);
		}
	}

	protected void freeBuffer(ByteBuffer buf, MMOConnection<T> con)
	{
		if(buf == READ_BUFFER)
			READ_BUFFER.clear();
		else
		{
			con.setReadBuffer(null);
			recycleBuffer(buf);
		}
	}

	@Override
	public void run()
	{
		int totalKeys = 0;
		Set<SelectionKey> keys = null;
		Iterator<SelectionKey> itr = null;
		Iterator<MMOConnection<T>> conItr = null;
		SelectionKey key = null;
		MMOConnection<T> con = null;
		long currentMillis = 0L;

		// main loop
		for(;;)
			try
		{

				if(isShuttingDown())
				{
					closeSelectorThread();
					break;
				}

				currentMillis = System.currentTimeMillis();

				conItr = _connections.iterator();
				while(conItr.hasNext())
				{
					con = conItr.next();
					if(!(con.getClient()).isAuthed() && currentMillis - con.getConnectionOpenTime() >= ConfigValue.AUTH_TIMEOUT)
					{
						closeConnectionImpl(con, 333);
						continue;
					}
					if (con.isPengingClose())
						if (!con.isPendingWrite() || currentMillis - con.getPendingCloseTime() >= ConfigValue.CLOSEWAIT_TIMEOUT)
						{
							closeConnectionImpl(con, 999);
							continue;
						}
					if (con.isPendingWrite())
						if (currentMillis - con.getPendingWriteTime() >= ConfigValue.INTEREST_DELAY)
							con.enableWriteInterest();
				}

				totalKeys = getSelector().selectNow();

				if(totalKeys > 0)
				{
					keys = getSelector().selectedKeys();
					itr = keys.iterator();

					while(itr.hasNext())
					{
						key = itr.next();
						itr.remove();

						if(key.isValid())
							try
							{
								//_log.info("key="+key+" isValid="+key.isValid()+" isAcceptable="+key.isAcceptable()+" isConnectable="+key.isConnectable()+" isReadable="+key.isReadable()+" isWritable="+key.isWritable());
									if(key.isAcceptable())
									{
										acceptConnection(key);
										continue;
									}
									else if(key.isConnectable())
									{
										finishConnection(key);
										continue;
									}


									if(key.isReadable())
										readPacket(key);
									if(key.isValid())
										if(key.isWritable())
											writePacket(key);
							}
							catch(CancelledKeyException cke)
							{
								
							}
					}

					//не делаем паузы, в случае если были какие-либо операции I/O
					continue;
				}

				try
				{
					Thread.sleep(ConfigValue.SLEEP_TIME);
				}
				catch(InterruptedException ie)
				{

				}
		}
		catch(IOException e)
		{
			_log.info("Error in " + getName()+ e);

			try
			{
				Thread.sleep(1000L);
			}
			catch(InterruptedException ie)
			{

			}
		}
	}

	protected void finishConnection(SelectionKey key)
	{
		try
		{
			((SocketChannel) key.channel()).finishConnect();
		}
		catch(IOException e)
		{
			MMOConnection<T> con = (MMOConnection<T>) key.attachment();
			T client = con.getClient();
			client.getConnection().onForcedDisconnection();
			closeConnectionImpl(client.getConnection(), 666);
		}
	}

	protected void acceptConnection(SelectionKey key)
	{
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;
		SelectionKey clientKey;
		try
		{
			while((sc = ssc.accept()) != null)
				if(getAcceptFilter() == null || getAcceptFilter().accept(sc))
				{
					sc.configureBlocking(false);

					byte[] proxy_ip = null;

					clientKey = sc.register(getSelector(), SelectionKey.OP_READ);
					
					

					MMOConnection<T> con = new MMOConnection<T>(this, sc.socket(), clientKey, proxy_ip);
					T client = getClientFactory().create(con);
					
					//_log.info("clientKey="+clientKey+" con="+con+" client="+client);

					if(client != null)
					{
						client.setConnection(con);
						con.setClient(client);
						clientKey.attach(con);

						if(getAcceptFilter() != null && ConfigValue.GameServerProtectEnablePacketCheck)
							getAcceptFilter().addConnect(client);

						_connections.add(con);
						_stats.increaseOpenedConnections(sc.socket().getInetAddress().getHostAddress());
					}
					else
						sc.close();
				}
				else
				{
					sc.close();
				}
		}
		catch(IOException e)
		{
			_log.info("Error in " + getName()+ e);
		}
	}

	protected void readPacket(SelectionKey key)
	{
		MMOConnection<T> con = (MMOConnection<T>) key.attachment();

		if(con.isClosed())
			return;

		ByteBuffer buf;
		int result = -2;

		if((buf = con.getReadBuffer()) == null)
			buf = READ_BUFFER;

		// if we try to to do a read with no space in the buffer it will read 0 bytes
		// going into infinite loop
		if(buf.position() == buf.limit())
		{
			_log.info("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == READ_BUFFER) + ". Closing connection.");
			closeConnectionImpl(con, buf.limit());
		}
		else
		{

			try
			{
				result = con.getReadableByteChannel().read(buf);
			}
			catch(IOException e)
			{
				//error handling goes bellow
			}

			if(result > 0)
			{
				buf.flip();

				_stats.increaseIncomingBytes(result);

				int i;
				// читаем из буфера максимум пакетов
				for(i = 0; this.tryReadPacket2(key, con, buf); i++)
				{}
			}
			else if(result == 0)
				closeConnectionImpl(con, result);
			else if(result == -1)
				closeConnectionImpl(con, result);
			else
			{
				con.onForcedDisconnection();
				closeConnectionImpl(con, result);
			}
		}

		if(buf == READ_BUFFER)
			buf.clear();
	}

	private int max_p_size=0;
	protected boolean tryReadPacket2(SelectionKey key, MMOConnection<T> con, ByteBuffer buf)
	{
		if(con.isClosed())
			return false;

		int pos = buf.position();
		// проверяем, хватает ли нам байт для чтения заголовка и не пустого тела пакета
		if(buf.remaining() > ConfigValue.HEADER_SIZE)
		{
			// получаем ожидаемый размер пакета
			int size = buf.getShort() & 0xffff;

			// TODO: убрать!!!
			// 5057 - максимально замеченный пакет...
			if(max_p_size < size)
			{
				max_p_size = size;
				_log.info("max_p_size: "+max_p_size);
			}
			if(ConfigValue.EnableLogPacketSize)
				Log.add(con.getSocket().getInetAddress().getHostAddress()+": "+size, "logins_packet_size");

			// проверяем корректность размера
			if(size <= ConfigValue.HEADER_SIZE || size > ConfigValue.PACKET_SIZE)
			{
				if(ConfigValue.GameServerProtectBanErrPacketSize && getAcceptFilter() != null)
				{
					Log.add("DDOS Client: " + con.getClient()+": "+size, "logins_ip_ddos_p_size");
					getAcceptFilter().BanIp(con.getSocket().getInetAddress(), -1, "DDOS ATTACK: size="+size);
				}
				else
					_log.info("Incorrect packet size : " + size + "! Client : " + con.getClient() + ". Closing connection.");
				closeConnectionImpl(con, size);
				return false;
			}

			//ожидаемый размер тела пакета
			size -= ConfigValue.HEADER_SIZE;

			// проверяем, хватает ли байт на чтение тела
			if(size <= buf.remaining())
			{
				_stats.increaseIncomingPacketsCount();
				parseClientPacket(getPacketHandler(), buf, size, con);
				buf.position(pos + size + ConfigValue.HEADER_SIZE);

				// закончили чтение из буфера, почистим
				if(!buf.hasRemaining())
				{
					freeBuffer(buf, con);
					return false;
				}

				return true;
			}

			// не хватает данных на чтение тела пакета, сбрасываем позицию
			buf.position(pos);
		}

		if(pos == buf.capacity())
			_log.info("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == READ_BUFFER) + ".");

		// не хватает данных, переносим содержимое первичного буфера во вторичный
		if(buf == READ_BUFFER)
			allocateReadBuffer(con);
		else
			buf.compact();

		return false;
	}

	protected void allocateReadBuffer(MMOConnection<T> con)
	{
		con.setReadBuffer(getPooledBuffer().put(READ_BUFFER));
		READ_BUFFER.clear();
	}

	protected boolean parseClientPacket(IPacketHandler<T> handler, ByteBuffer buf, int dataSize, MMOConnection<T> con)
	{
		T client = con.getClient();

		int pos = buf.position();

		try
		{
			client.decrypt(buf, dataSize);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			con.onForcedDisconnection();
			closeConnectionImpl(con, pos);
			buf.clear();
			return false;
		}
		buf.position(pos);

		if(buf.hasRemaining())
		{
			//  apply limit
			int limit = buf.limit();
			buf.limit(pos + dataSize);
			ReceivablePacket<T> rp = handler.handlePacket(buf, client);

			if(rp != null)
			{
				//Log.add(con.getSocket().getInetAddress().getHostAddress()+": "+rp, "logins_r_packet");
				rp.setByteBuffer(buf);
				rp.setClient(client);

				if(getAcceptFilter() != null)
					getAcceptFilter().incReceivablePacket(rp, client);
				if(rp.read())
					con.recvPacket(rp);

				rp.setByteBuffer(null);
			}
			else if(getAcceptFilter() != null)
				getAcceptFilter().incReceivablePacket(rp, client);
			buf.limit(limit);
		}
		return true;
	}

	protected void writePacket(SelectionKey key)
	{
		MMOConnection<T> con = (MMOConnection<T>) key.attachment();

		prepareWriteBuffer(con);

		DIRECT_WRITE_BUFFER.flip();
		int size = DIRECT_WRITE_BUFFER.remaining();

		int result = -1;

		try
		{
			result = con.getWritableChannel().write(DIRECT_WRITE_BUFFER);
		}
		catch(IOException e)
		{
			// error handling goes on the if bellow
		}

		// check if no error happened
		if(result >= 0)
		{
			_stats.increaseOutgoingBytes(result);

			// check if we written everything
			if(result != size)
				con.createWriteBuffer(DIRECT_WRITE_BUFFER);

			if(!con.getSendQueue().isEmpty() || con.hasPendingWriteBuffer())
				// запись не завершена
				con.scheduleWriteInterest();
			else
				con.disableWriteInterest();
		}
		else
		{
			con.onForcedDisconnection();
			closeConnectionImpl(con, result);
		}
	}

	public T getWriteClient()
	{
		return WRITE_CLIENT;
	}

	public ByteBuffer getWriteBuffer()
	{
		return WRITE_BUFFER;
	}

	protected void prepareWriteBuffer(MMOConnection<T> con)
	{
		WRITE_CLIENT = con.getClient();
		DIRECT_WRITE_BUFFER.clear();

		if(con.hasPendingWriteBuffer()) // если осталось что-то с прошлого раза
			con.movePendingWriteBufferTo(DIRECT_WRITE_BUFFER);

		if(DIRECT_WRITE_BUFFER.hasRemaining() && !con.hasPendingWriteBuffer())
		{
			int i;
			Queue<SendablePacket<T>> sendQueue = con.getSendQueue();
			SendablePacket<T> sp;

			for(i = 0; i < ConfigValue.MAX_SEND_PER_PASS; i++)
			{
				con.lock();
				try
				{
					if((sp = sendQueue.poll()) == null)
						break;
				}
				finally
				{
					con.unlock();
				}

				try
				{
					if(getAcceptFilter() != null)
						getAcceptFilter().incSendablePacket(sp, con.getClient());

					_stats.increaseOutgoingPacketsCount();
					putPacketIntoWriteBuffer(sp, true); // записываем пакет в WRITE_BUFFER
					_stats.addPacket(sp);
					WRITE_BUFFER.flip();
					if(DIRECT_WRITE_BUFFER.remaining() >= WRITE_BUFFER.limit())
						DIRECT_WRITE_BUFFER.put(WRITE_BUFFER);
					else
						// если не осталось места в DIRECT_WRITE_BUFFER для WRITE_BUFFER то мы его запишев в следующий раз
					{
						con.createWriteBuffer(WRITE_BUFFER);
						break;
					}
				}
				catch(Exception e)
				{
					_log.info("Error in " + getName()+ e);
					break;
				}
			}
		}

		WRITE_BUFFER.clear();
		WRITE_CLIENT = null;
	}

	protected final void putPacketIntoWriteBuffer(SendablePacket<T> sp, boolean encrypt)
	{
		WRITE_BUFFER.clear();

		// reserve space for the size
		int headerPos = WRITE_BUFFER.position();
		WRITE_BUFFER.position(headerPos + ConfigValue.HEADER_SIZE);

		// write content to buffer
		sp.write();

		// size (incl header)
		int dataSize = WRITE_BUFFER.position() - headerPos - ConfigValue.HEADER_SIZE;
		if(dataSize == 0)
		{
			WRITE_BUFFER.position(headerPos);
			return;
		}
		WRITE_BUFFER.position(headerPos + ConfigValue.HEADER_SIZE);

		if(encrypt)
		{
			WRITE_CLIENT.encrypt(WRITE_BUFFER, dataSize);
			// recalculate size after encryption
			dataSize = WRITE_BUFFER.position() - headerPos - ConfigValue.HEADER_SIZE;
		}

		// prepend header
		WRITE_BUFFER.position(headerPos);
		WRITE_BUFFER.putShort((short) (ConfigValue.HEADER_SIZE + dataSize));
		WRITE_BUFFER.position(headerPos + ConfigValue.HEADER_SIZE + dataSize);
	}

	protected Selector getSelector()
	{
		return _selector;
	}

	protected IMMOExecutor<T> getExecutor()
	{
		return _executor;
	}

	protected IPacketHandler<T> getPacketHandler()
	{
		return _packetHandler;
	}

	protected IClientFactory<T> getClientFactory()
	{
		return _clientFactory;
	}

	public IAcceptFilter getAcceptFilter()
	{
		return _acceptFilter;
	}

	protected void closeConnectionImpl(MMOConnection<T> con, int result)
	{
		final String ip = con.getSocket().getInetAddress().getHostAddress();
		try
		{
			if(ConfigValue.DubugTraceCloseConnection)
				Log.logTrace("["+ip+"]["+result+"]", "selector_thread", "close_impl");
			// notify connection
			con.onDisconnection();
		}
		finally
		{
			try
			{
				// close socket and the SocketChannel
				con.close();
			}
			catch(IOException e)
			{
				// ignore, we are closing anyway
			}
			finally
			{
				try
				{
					// очистить буферы
					con.releaseBuffers();
					// очистим очереди
					con.clearQueues();
					// обнуляем соединение у клиента
					con.getClient().setConnection(null);
					// обнуляем соединение у ключа
					con.getSelectionKey().attach(null);
					// отменяем ключ
					con.getSelectionKey().cancel();
				}
				finally
				{
					if(getAcceptFilter() != null && ConfigValue.GameServerProtectEnablePacketCheck)
						getAcceptFilter().removeConnect(con.getClient());
					_connections.remove(con);
					_stats.decreaseOpenedConnections(ip);
				}
			}
		}
	}

	public void shutdown()
	{
		_shutdown = true;
	}

	public boolean isShuttingDown()
	{
		return _shutdown;
	}

	protected void closeAllChannels()
	{
		Set<SelectionKey> keys = getSelector().keys();
		for(SelectionKey key : keys)
			try
		{
				key.channel().close();
		}
		catch(IOException e)
		{
			// ignore
		}
	}

	protected void closeSelectorThread()
	{
		closeAllChannels();

		try
		{
			getSelector().close();
		}
		catch(IOException e)
		{
			// Ignore
		}
	}
	//----
	public static void getStartAntiFlood()
	{
		l2open.gameserver.GameTimeController.getInstance();
	}
	private static boolean enableAntiflood = false;
	public static void setAntiFlood(boolean _enableAntiflood)
	{
		enableAntiflood = _enableAntiflood;
	}
	public static boolean getAntiFlood()
	{
		return enableAntiflood;
	}
	public static void setAntiFloodSocketsConf(int MaxUnhandledSocketsPerIP, int UnhandledSocketsMinTTL)
	{}
	public static void setGlobalReadLock(boolean enable)
	{}
	public void setAcceptFilter(IAcceptFilter acceptFilter)
	{}

	public static CharSequence getStats2()
	{
		final StringBuilder list = new StringBuilder();
		list.append("=================================================\n");
		//list.append("pakcet count: .... ").append(_stats._p1.size()).append(" p/s\n");
		//list.append("pakcet count: .... ").append(_stats._p2.size()).append(" p/ms\n");
		list.append("pakcet count: .... ").append(_stats._p_1.get()).append(" p/s\n");
		list.append("pakcet count: .... ").append(_stats._p_2.get()).append(" p/ms\n");
		list.append("=================================================\n");
		return list;
	}

	public static CharSequence getStats()
	{
		final StringBuilder list = new StringBuilder();
		//list.append("selectorThreadCount: .... ").append(all_selectors.size()).append("\n");
		list.append("=================================================\n");
		list.append("getTotalConnections: .... ").append(_stats.getTotalConnections()).append("\n");
		list.append("getCurrentConnections: .. ").append(_stats.getCurrentConnections()).append("\n");
		list.append("getMaximumConnections: .. ").append(_stats.getMaximumConnections()).append("\n");
		list.append("getIncomingBytesTotal: .. ").append(_stats.getIncomingBytesTotal()).append("\n");
		list.append("getOutgoingBytesTotal: .. ").append(_stats.getOutgoingBytesTotal()).append("\n");
		list.append("getIncomingPacketsTotal:  ").append(_stats.getIncomingPacketsTotal()).append("\n");
		list.append("getOutgoingPacketsTotal:  ").append(_stats.getOutgoingPacketsTotal()).append("\n");
		list.append("getMaxBytesPerRead: ..... ").append(_stats.getMaxBytesPerRead()).append("\n");
		list.append("getMaxBytesPerWrite: .... ").append(_stats.getMaxBytesPerWrite()).append("\n");
		list.append("=================================================\n");
		return list;
	}
}