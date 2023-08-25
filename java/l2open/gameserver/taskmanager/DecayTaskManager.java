package l2open.gameserver.taskmanager;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Character;
import l2open.util.GArray;
import l2open.util.Util;

public class DecayTaskManager
{
	private DecayTask[] _decayTasks = new DecayTask[500];
	private int _decayTasksSize = 0;
	private final Object decayTasks_lock = new Object();

	private static DecayTaskManager _instance;

	private DecayTaskManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new DecayScheduler(), 2000, 2000);
	}

	public static DecayTaskManager getInstance()
	{
		if(_instance == null)
			_instance = new DecayTaskManager();

		return _instance;
	}

	public void addDecayTask(L2Character actor)
	{
		if(actor.isFlying())
			addDecayTask(actor, 4500);
		else
			addDecayTask(actor, 8500);
	}

	public void addDecayTask(L2Character actor, long interval)
	{
		removeObject(actor);
		addObject(new DecayTask(actor, System.currentTimeMillis() + interval));
	}

	public void cancelDecayTask(L2Character actor)
	{
		removeObject(actor);
	}

	public class DecayScheduler extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(_decayTasksSize > 0)
				try
				{
					GArray<L2Character> works = new GArray<L2Character>();

					synchronized (decayTasks_lock)
					{
						long current = System.currentTimeMillis();
						int size = _decayTasksSize;

						for(int i = size - 1; i >= 0; i--)
							try
							{
								DecayTask container = _decayTasks[i];

								if(container != null && container.endtime > 0 && current > container.endtime)
								{
									L2Character actor = container.getActor();
									if(actor != null)
										works.add(actor);

									container.endtime = -1;
								}

								if(container == null || container.getActor() == null || container.endtime < 0)
								{
									if(i == _decayTasksSize - 1)
										_decayTasks[i] = null;
									else
									{
										_decayTasks[i] = _decayTasks[_decayTasksSize - 1];
										_decayTasks[_decayTasksSize - 1] = null;
									}

									if(_decayTasksSize > 0)
										_decayTasksSize--;
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
					}

					for(L2Character work : works)
						work.onDecay();
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
		StringBuffer sb = new StringBuffer("============= DecayTask Manager Report ============\n\r");
		sb.append("Tasks count: ").append(_decayTasksSize).append("\n\r");
		sb.append("Tasks dump:\n\r");

		long current = System.currentTimeMillis();
		for(DecayTask container : _decayTasks)
		{
			sb.append("Class/Name: ").append(container.getClass().getSimpleName()).append('/').append(container.getActor());
			sb.append(" decay timer: ").append(Util.formatTime(container.endtime - current)).append("\n\r");
		}

		return sb.toString();
	}

	private class DecayTask
	{
		private final L2Character actorStoreId;
		public long endtime;

		public DecayTask(L2Character cha, long delay)
		{
			actorStoreId = cha;
			endtime = delay;
		}

		public L2Character getActor()
		{
			return actorStoreId;
		}
	}

	private void addObject(DecayTask decay)
	{
		synchronized (decayTasks_lock)
		{
			if(_decayTasksSize >= _decayTasks.length)
			{
				DecayTask[] temp = new DecayTask[_decayTasks.length * 2];
				for(int i = 0; i < _decayTasksSize; i++)
					temp[i] = _decayTasks[i];
				_decayTasks = temp;
			}

			_decayTasks[_decayTasksSize] = decay;
			_decayTasksSize++;
		}
	}

	private void removeObject(L2Character actor)
	{
		synchronized (decayTasks_lock)
		{
			if(_decayTasksSize > 1)
			{
				int k = -1;
				for(int i = 0; i < _decayTasksSize; i++)
					if(_decayTasks[i].getActor() == actor)
						k = i;
				if(k > -1)
				{
					_decayTasks[k] = _decayTasks[_decayTasksSize - 1];
					_decayTasks[_decayTasksSize - 1] = null;
					_decayTasksSize--;
				}
			}
			else if(_decayTasksSize == 1 && _decayTasks[0].getActor() == actor)
			{
				_decayTasks[0] = null;
				_decayTasksSize = 0;
			}
		}
	}
}