package l2open.gameserver.idfactory;

import l2open.common.ThreadPoolManager;
import l2open.util.PrimeFinder;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class BitSetIDFactory extends IdFactory
{
	private static Logger _log = Logger.getLogger(BitSetIDFactory.class.getName());

	private BitSet freeIds;
	private AtomicInteger freeIdCount;
	private AtomicInteger nextFreeId;

	public class BitSetCapacityCheck extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(reachingBitSetCapacity())
				increaseBitSetCapacity();
		}
	}

	protected BitSetIDFactory()
	{
		super();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
		initialize();
		_log.info("IDFactory: " + freeIds.size() + " id's available.");
	}

	public synchronized void initialize()
	{
		try
		{
			freeIds = new BitSet(PrimeFinder.nextPrime(100000));
			freeIds.clear();
			freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

			for(int usedObjectId : extractUsedObjectIDTable())
			{
				int objectID = usedObjectId - FIRST_OID;
				if(objectID < 0)
				{
					_log.warning("Object ID " + usedObjectId + " in DB is less than minimum ID of " + FIRST_OID);
					continue;
				}
				freeIds.set(usedObjectId - FIRST_OID);
				freeIdCount.decrementAndGet();
			}

			nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
			initialized = true;
		}
		catch(Exception e)
		{
			initialized = false;
			_log.severe("BitSet ID Factory could not be initialized correctly");
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void releaseId(int objectID)
	{
		if(objectID - FIRST_OID > -1)
		{
			freeIds.clear(objectID - FIRST_OID);
			freeIdCount.incrementAndGet();
			super.releaseId(objectID);
		}
		else
			_log.warning("BitSet ID Factory: release objectID " + objectID + " failed (< " + FIRST_OID + ")");
	}

	@Override
	public synchronized int getNextId()
	{
		int newID = nextFreeId.get();
		freeIds.set(newID);
		freeIdCount.decrementAndGet();

		int nextFree = freeIds.nextClearBit(newID);

		if(nextFree < 0)
			nextFree = freeIds.nextClearBit(0);
		if(nextFree < 0)
			if(freeIds.size() < FREE_OBJECT_ID_SIZE)
				increaseBitSetCapacity();
			else
				throw new NullPointerException("Ran out of valid Id's.");

		nextFreeId.set(nextFree);

		return newID + FIRST_OID;
	}

	@Override
	public synchronized int size()
	{
		return freeIdCount.get();
	}

	protected synchronized int usedIdCount()
	{
		return size() - FIRST_OID;
	}

	protected synchronized boolean reachingBitSetCapacity()
	{
		return PrimeFinder.nextPrime(usedIdCount() * 11 / 10) > freeIds.size();
	}

	protected synchronized void increaseBitSetCapacity()
	{
		BitSet newBitSet = new BitSet(PrimeFinder.nextPrime(usedIdCount() * 11 / 10));
		newBitSet.or(freeIds);
		freeIds = newBitSet;
	}

	public void _unload()
	{
		freeIds.clear();
		freeIds = null;
	}
}