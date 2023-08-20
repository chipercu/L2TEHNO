package l2open.gameserver.taskmanager;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Rnd;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoSaveManager
{
	private PlayerContainer[] _tasks = new PlayerContainer[3200];
	private int _currentCell = 0;

	private static AutoSaveManager _instance;

	private AutoSaveManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveScheduler(), 1000, 1000);
	}

	public static AutoSaveManager getInstance()
	{
		if(_instance == null)
			_instance = new AutoSaveManager();

		return _instance;
	}

	public void addPlayerTask(L2Player p)
	{
		int cell = _currentCell + Rnd.get(800, 1600);
		if(_tasks.length <= cell)
			cell -= _tasks.length;
		if(_tasks[cell] == null)
			_tasks[cell] = new PlayerContainer();
		_tasks[cell].addPlayer(p);
	}

	private class SaveScheduler extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				PlayerContainer currentContainer = _tasks[_currentCell];
				if(currentContainer != null)
					for(L2Player p : currentContainer.getList())
						try
						{
							if(p == null || !p.isConnected() || p.isLogoutStarted() || p.getNetConnection() == null)
								continue;
							if(ConfigValue.Autosave)
								PlayerData.getInstance().store(p, true);
							addPlayerTask(p);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
			}
			catch(Throwable e)
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