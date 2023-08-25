package l2open.gameserver.tables;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.lang.ref.WeakReference;
import java.util.Map.Entry;

import gnu.trove.TIntObjectHashMap;

import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.KamalokaNightmare;
import l2open.util.GArray;
import l2open.util.Util;

public class ReflectionTable
{
	public static int SOD_REFLECTION_ID = 0;
	public static final int DEFAULT = 0;
	public static final int PARNASSUS = -1;
	public static final int GH = -2;
	public static final int JAIL = -3;
	public static final int SOI_HEART_OF_INFINITY_DEFENCE = 122;
	public static final int SOI_HALL_OF_EROSION_DEFENCE = 120;
	public static final int SOI_HALL_OF_EROSION_ATTACK = 119;
	public static final int SOI_HALL_OF_SUFFERING_SECTOR2 = 116;
	public static final int SOI_HALL_OF_SUFFERING_SECTOR1 = 115;
	public static final int DISCIPLES_NECROPOLIS = 112;

	private static ReflectionTable _instance;
	private static Reflection _default = new Reflection(0).setName("DEFAULT");
	private ConcurrentHashMap<Integer, WeakReference<Reflection>> _playerList = new ConcurrentHashMap<Integer, WeakReference<Reflection>>();

	private final TIntObjectHashMap<Reflection> _reflections = new TIntObjectHashMap<Reflection>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	public static ReflectionTable getInstance()
	{
		if(_instance == null)
			_instance = new ReflectionTable();
		return _instance;
	}

	private ConcurrentHashMap<Integer, Integer> _soloKamalokaList = new ConcurrentHashMap<Integer, Integer>();

	public Reflection get(int index, boolean CreateIfNonExist)
	{
		Reflection ret = get(index);
		if(CreateIfNonExist && ret == null)
		{
			ret = new Reflection(index);
			String name = "";
			switch(index)
			{
				case -1:
					name = "PARNASSUS";
					break;
				case -2:
					name = "GH";
					break;
				case -3:
					name = "JAIL";
					break;
			}
			ret.setName(name);
		}
		return ret;
	}

	public Reflection getDefault()
	{
		return _default;
	}

	public void addSoloKamaloka(Integer player, KamalokaNightmare r)
	{
		_soloKamalokaList.put(player, r.getId());
	}

	public void removeSoloKamaloka(Integer player)
	{
		_soloKamalokaList.remove(player);
	}

	public KamalokaNightmare findSoloKamaloka(Integer player)
	{
		Integer index = _soloKamalokaList.get(player);
		if(index == null)
			return null;
		Reflection found = get(index);
		if(found == null || !(found instanceof KamalokaNightmare) || ((KamalokaNightmare) found).getPlayerId() != player)
		{
			_soloKamalokaList.remove(player);
			return null;
		}
		return (KamalokaNightmare) get(index);
	}

	public void playerAddReflection(Integer id, Reflection ref)
	{
		_playerList.put(id, new WeakReference<Reflection>(ref));
	}

	public void playerRemoveReflection(Integer id)
	{
		_playerList.remove(id);
	}

	public Reflection playerFindReflection(Integer id)
	{
		WeakReference<Reflection> wr = _playerList.get(id);
		if(wr == null)
			return null;
		Reflection ref = wr.get();
		if(ref == null || ref.isCollapseStarted())
		{
			_playerList.remove(id);
			return null;
		}
		return ref;
	}

	public GArray<Integer> getPlayersEntered(Reflection ref)
	{
		GArray<Integer> player = new GArray<Integer>(9);
		for(Entry ent : _playerList.entrySet())
		{
			if(ent.getValue() != null && ((Reflection)((WeakReference)ent.getValue()).get()).equals(ref))
				player.add((Integer)ent.getKey());
		}
		return player.isEmpty() ? null : player;
	}

	public Reflection get(int id)
	{
		readLock.lock();
		try
		{
			return _reflections.get(id);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public Reflection addReflection(Reflection ref)
	{
		writeLock.lock();
		try
		{
			return _reflections.put(ref.getId(), ref);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public Reflection remove(Reflection ref)
	{
		writeLock.lock();
		try
		{
			return _reflections.remove(ref.getId());
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public Reflection[] getAll()
	{
		readLock.lock();
		try
		{
			return _reflections.getValues(new Reflection[_reflections.size()]);
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	public IBroadcastPacket canCreate()
	{
		if(getCountByIzId(_id) >= _maxChannels)
			return THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED;
		return null;
	}
	**/
	public int getCountByIzId(int instz_id)
	{
		readLock.lock();
		try
		{
			int count = 0;
			for(Reflection r : getAll())
				if(r.getInstancedZoneId() == instz_id)
					count++;
			return count;
		}
		finally
		{
			readLock.unlock();
		}
	}

	public int size()
	{
		return _reflections.size();
	}
}