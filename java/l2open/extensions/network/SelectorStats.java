package l2open.extensions.network;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class SelectorStats
{
	private final AtomicLong _connectionsTotal = new AtomicLong();
	private final AtomicLong _connectionsCurrent = new AtomicLong();
	private final AtomicLong _connectionsMax = new AtomicLong();
	private final AtomicLong _incomingBytesTotal = new AtomicLong();
	private final AtomicLong _outgoingBytesTotal = new AtomicLong();
	private final AtomicLong _incomingPacketsTotal = new AtomicLong();
	private final AtomicLong _outgoingPacketsTotal = new AtomicLong();
	private final AtomicLong _bytesMaxPerRead = new AtomicLong();
	private final AtomicLong _bytesMaxPerWrite = new AtomicLong();
	public final ConcurrentHashMap<String, Integer> _online = new ConcurrentHashMap<String, Integer>();

	public static final AtomicLong _p_1 = new AtomicLong();
	public static final AtomicLong _p_2 = new AtomicLong();

	public static List<Integer> _p1;
	public static List<Integer> _p2;

	public static int _p11=0;
	public static int _p22=0;

	static
	{
		_p1 = new ArrayList<Integer>();
		_p2 = new ArrayList<Integer>();
		/*ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				_p_1.set(0);
				//_p11 = _p1.size();
				//_p1.clear();
			}
		}, 1000, 1000);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				_p_2.set(0);
				//_p22 = _p2.size();
				//_p2.clear();
			}
		}, 1, 1);*/
	}

	public void addPacket(SendablePacket<?> sp)
	{
		/*if(ConfigValue.PacketStatsType == 1)
		{
			_p_1.incrementAndGet();
			_p_2.incrementAndGet();
		}*/
		//else if(ConfigValue.PacketStatsType == 1)
		//{
		//_p11=_p11+1;
		//_p22=_p22+1;
		//long time = System.currentTimeMillis();
		//_p1.add(0);
		//_p2.add(0);
		//}
	}

	public void increaseOpenedConnections(String ip)
	{
		if(_connectionsCurrent.incrementAndGet() > _connectionsMax.get())
			_connectionsMax.incrementAndGet();
		_connectionsTotal.incrementAndGet();

		if(_online.containsKey(ip))
		{
			int count = _online.get(ip);
			_online.put(ip, count+1);
		}
		else
		{
			_online.put(ip, 1);
		}
	}

	public void decreaseOpenedConnections(String ip)
	{
		_connectionsCurrent.decrementAndGet();
		if(_online.containsKey(ip))
		{
			int count = _online.get(ip);
			_online.put(ip, Math.max(0, count-1));
		}
		else
		{
			_online.put(ip, 0);
		}
	}

	public void increaseIncomingBytes(int size)
	{
		if (size > _bytesMaxPerRead.get())
			_bytesMaxPerRead.set(size);
		_incomingBytesTotal.addAndGet(size);
	}

	public void increaseOutgoingBytes(int size)
	{
		if (size > _bytesMaxPerWrite.get())
			_bytesMaxPerWrite.set(size);
		_outgoingBytesTotal.addAndGet(size);
	}

	public void increaseIncomingPacketsCount()
	{
		_incomingPacketsTotal.incrementAndGet();
	}

	public void increaseOutgoingPacketsCount()
	{
		_outgoingPacketsTotal.incrementAndGet();
	}

	public long getTotalConnections()
	{
		return _connectionsTotal.get();
	}

	public long getCurrentConnections()
	{
		return _connectionsCurrent.get();
	}

	public long getMaximumConnections()
	{
		return _connectionsMax.get();
	}

	public long getIncomingBytesTotal()
	{
		return _incomingBytesTotal.get();
	}

	public long getOutgoingBytesTotal()
	{
		return _outgoingBytesTotal.get();
	}

	public long getIncomingPacketsTotal()
	{
		return _incomingPacketsTotal.get();
	}

	public long getOutgoingPacketsTotal()
	{
		return _outgoingPacketsTotal.get();
	}

	public long getMaxBytesPerRead()
	{
		return _bytesMaxPerRead.get();
	}

	public long getMaxBytesPerWrite()
	{
		return _bytesMaxPerWrite.get();
	}

	public ConcurrentHashMap<String, Integer> getIpOnline()
	{
		return _online;
	}
}
