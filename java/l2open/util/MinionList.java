package l2open.util;

import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2MinionData;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.tables.NpcTable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MinionList
{
	/** List containing the current spawned minions for this L2MonsterInstance */
	private final Set<L2MinionInstance> _minionReferences;
	private final L2MonsterInstance _master;
	private final Lock lock;

	public MinionList(L2MonsterInstance master)
	{
		_minionReferences = new HashSet<L2MinionInstance>();
		_master = master;
		lock = new ReentrantLock();
	}

	public int countSpawnedMinions()
	{
		return _minionReferences.size();
	}

	public boolean hasMinions()
	{
		return _minionReferences.size() > 0;
	}

	public List<L2MinionInstance> getSpawnedMinions()
	{
		List<L2MinionInstance> result = new ArrayList<L2MinionInstance>(_minionReferences.size());
		lock.lock();
		try
		{
			for(L2MinionInstance m : _minionReferences)
				if(!m.isDead())
					result.add(m);
		}
		finally
		{
			lock.unlock();
		}
		return result;
	}

	public void addSpawnedMinion(L2MinionInstance minion)
	{
		lock.lock();
		try
		{
			_minionReferences.add(minion);
		}
		finally
		{
			lock.unlock();
		}
	}

	public void removeSpawnedMinion(L2MinionInstance minion)
	{
		lock.lock();
		try
		{
			_minionReferences.remove(minion);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 *  Спавнит всех недостающих миньонов
	 */
	public void maintainMinions()
	{
		GArray<L2MinionData> minions = _master.getTemplate().getMinionData();

		lock.lock();
		try
		{
			byte minionCount;
			int minionId;
			for(L2MinionData minion : minions)
			{
				minionCount = minion.getAmount();
				minionId = minion.getMinionId();

				for(L2MinionInstance m : _minionReferences)
					if(m.getNpcId() == minionId)
						minionCount--;

				for(int i = 0; i < minionCount; i++)
					spawnSingleMinion(minionId);
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 *	Удаляет тех миньонов, которые еще живы
	 */
	public void maintainLonelyMinions()
	{
		lock.lock();
		try
		{
			for(L2MinionInstance minion : getSpawnedMinions())
				if(!minion.isDead())
				{
					_minionReferences.remove(minion);
					minion.deleteMe();
				}
		}
		finally
		{
			lock.unlock();
		}
	}

	private void spawnSingleMinion(int minionid)
	{
		L2MinionInstance monster = new L2MinionInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(minionid), _master);
		monster.setReflection(_master.getReflection().getId());
		if(_master.getChampion() == 2)
			monster.setChampion(1);
		monster.onSpawn();
		monster.setHeading(_master.getHeading());
		addSpawnedMinion(monster);
		monster.spawnMe(_master.getMinionPosition());
	}

	/**
	 * Same as spawnSingleMinion, but synchronized.<BR><BR>
	 * @param minionid The I2NpcTemplate Identifier of the Minion to spawn
	 */
	public void spawnSingleMinionSync(int minionid)
	{
		lock.lock();
		try
		{
			spawnSingleMinion(minionid);
		}
		finally
		{
			lock.unlock();
		}
	}
}