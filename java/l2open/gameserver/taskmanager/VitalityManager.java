package l2open.gameserver.taskmanager;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;

import java.util.concurrent.ConcurrentLinkedQueue;

public class VitalityManager
{
	private PlayerContainer[] _tasks = new PlayerContainer[2];
	private int _currentCell = 0;

	private static VitalityManager _instance;

	private VitalityManager()
	{
		if(ConfigValue.AltVitalityEnabled)
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new VitalityScheduler(), 120000, 120000);
	}

	public static VitalityManager getInstance()
	{
		if(_instance == null)
			_instance = new VitalityManager();

		return _instance;
	}

	public PlayerContainer addRegenTask(L2Player p)
	{
		// Вообще-то этот кошмар можно нахрен убрать, оставив только одну ячейку для следующего тика, но раз работает...
		int cell = _currentCell + 1;
		if(_tasks.length <= cell)
			cell -= _tasks.length;
		if(_tasks[cell] == null)
			_tasks[cell] = new PlayerContainer();
		_tasks[cell].addPlayer(p);
		return _tasks[cell];
	}

	private class VitalityScheduler extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				PlayerContainer currentContainer = _tasks[_currentCell];
				if(currentContainer != null)
					for(L2Player player : currentContainer.getList())
						try
						{
							if(player != null && !player.isDeleting() && (player.isConnected() || player.isInOfflineMode()) && player.isInPeaceZone())
							{
								player.setVitality(player.getVitality() + 1); // одно очко в 2 минуты, поскольку у нас одно очко исторически вдвое больше чем на оффе
								addRegenTask(player);
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(_tasks[_currentCell] != null)
					_tasks[_currentCell].clear();
				_currentCell++;
				if(_currentCell >= _tasks.length)
					_currentCell = 0;
			}
		}
	}

	private class PlayerContainer
	{
		private ConcurrentLinkedQueue<L2Player> list = new ConcurrentLinkedQueue<L2Player>();

		public void addPlayer(L2Player e)
		{
			list.add(e);
		}

		public ConcurrentLinkedQueue<L2Player> getList()
		{
			return list;
		}

		public void clear()
		{
			synchronized (list)
			{
				list.clear();
			}
		}
	}
}