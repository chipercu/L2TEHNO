package l2open.gameserver.taskmanager;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.GArray;
import l2open.util.Util;

public class SpawnTaskManager
{
	private SpawnTask[] _spawnTasks = new SpawnTask[500];
	private int _spawnTasksSize = 0;
	private final Object spawnTasks_lock = new Object();

	private static SpawnTaskManager _instance;

	public SpawnTaskManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnScheduler(), 2000, 2000);
	}

	public static SpawnTaskManager getInstance()
	{
		if(_instance == null)
			_instance = new SpawnTaskManager();

		return _instance;
	}

	public void addSpawnTask(L2NpcInstance actor, long interval)
	{
		removeObject(actor);
		addObject(new SpawnTask(actor, System.currentTimeMillis() + interval));
	}

	public void cancelSpawnTask(L2NpcInstance actor)
	{
		removeObject(actor);
	}

	public class SpawnScheduler extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(_spawnTasksSize > 0)
				try
				{
					GArray<L2NpcInstance> works = new GArray<L2NpcInstance>();

					synchronized (spawnTasks_lock)
					{
						long current = System.currentTimeMillis();
						int size = _spawnTasksSize;

						for(int i = size - 1; i >= 0; i--)
							try
							{
								SpawnTask container = _spawnTasks[i];

								if(container != null && container.endtime > 0 && current > container.endtime)
								{
									L2NpcInstance actor = container.getActor();
									if(actor != null && actor.getSpawn() != null)
										works.add(actor);

									container.endtime = -1;
								}

								if(container == null || container.getActor() == null || container.endtime < 0)
								{
									if(i == _spawnTasksSize - 1)
										_spawnTasks[i] = null;
									else
									{
										_spawnTasks[i] = _spawnTasks[_spawnTasksSize - 1];
										_spawnTasks[_spawnTasksSize - 1] = null;
									}

									if(_spawnTasksSize > 0)
										_spawnTasksSize--;
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
					}

					for(L2NpcInstance work : works)
					{
						L2Spawn spawn = work.getSpawn();
						if(spawn == null)
							continue;
						spawn.decreaseScheduledCount();
						if(spawn.isDoRespawn())
							spawn.respawnNpc(work);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer("============= SpawnTask Manager Report ============\n\r");
		sb.append("Tasks count: ").append(_spawnTasksSize).append("\n\r");
		sb.append("Tasks dump:\n\r");

		long current = System.currentTimeMillis();
		for(SpawnTask container : _spawnTasks)
		{
			sb.append("Class/Name: ").append(container.getClass().getSimpleName()).append('/').append(container.getActor());
			sb.append(" spawn timer: ").append(Util.formatTime(container.endtime - current)).append("\n\r");
		}

		return sb.toString();
	}

	private class SpawnTask
	{
		private final L2NpcInstance _cha;
		public long endtime;

		SpawnTask(L2NpcInstance cha, long delay)
		{
			_cha = cha;
			endtime = delay;
		}

		public L2NpcInstance getActor()
		{
			return _cha;
		}
	}

	private void addObject(SpawnTask decay)
	{
		synchronized (spawnTasks_lock)
		{
			if(_spawnTasksSize >= _spawnTasks.length)
			{
				SpawnTask[] temp = new SpawnTask[_spawnTasks.length * 2];
				for(int i = 0; i < _spawnTasksSize; i++)
					temp[i] = _spawnTasks[i];
				_spawnTasks = temp;
			}

			_spawnTasks[_spawnTasksSize] = decay;
			_spawnTasksSize++;
		}
	}

	private void removeObject(L2NpcInstance actor)
	{
		synchronized (spawnTasks_lock)
		{
			if(_spawnTasksSize > 1)
			{
				int k = -1;
				for(int i = 0; i < _spawnTasksSize; i++)
					if(_spawnTasks[i].getActor() == actor)
						k = i;
				if(k > -1)
				{
					_spawnTasks[k] = _spawnTasks[_spawnTasksSize - 1];
					_spawnTasks[_spawnTasksSize - 1] = null;
					_spawnTasksSize--;
				}
			}
			else if(_spawnTasksSize == 1 && _spawnTasks[0].getActor() == actor)
			{
				_spawnTasks[0] = null;
				_spawnTasksSize = 0;
			}
		}
	}
}