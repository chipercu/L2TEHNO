package l2open.gameserver.taskmanager;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BreakWarnManager
{
	private PlayerContainer[] _tasks = new PlayerContainer[360];
	private int _currentCell = 0;

	private static BreakWarnManager _instance;

	private BreakWarnManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new DispelScheduler(), 60000, 60000);
	}

	public static BreakWarnManager getInstance()
	{
		if(_instance == null)
			_instance = new BreakWarnManager();

		return _instance;
	}

	public PlayerContainer addWarnTask(L2Player p)
	{
		int cell = _currentCell + (p.getTimeOnline() == 0 ? 120 : 60); // 120
		if(_tasks.length <= cell)
			cell -= _tasks.length;
		if(_tasks[cell] == null)
			_tasks[cell] = new PlayerContainer();
		_tasks[cell].addPlayer(p);
		return _tasks[cell];
	}

	private class DispelScheduler extends l2open.common.RunnableImpl
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
							if(player == null || !player.isConnected() || player.isDeleting())
								continue;
							int time = player.getTimeOnline();
							time += player.getTimeOnline() == 0 ? 2 : 1;
							player.setTimeOnline(time);
							SystemMessage sm = new SystemMessage(SystemMessage.YOU_HAVE_BEEN_PLAYING_FOR_S1_HOUR_PLEASE_CONSIDER_TAKING_A_BREAK).addNumber(time);
							player.sendPacket(sm);
							addWarnTask(player);
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