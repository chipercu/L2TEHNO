package l2open.gameserver.taskmanager;

import javolution.util.ReentrantLock;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Skill;
import l2open.util.GArray;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class SkillTaskManager
{
	private static final Logger _log = Logger.getLogger(SkillTaskManager.class.getName());

	private SkillContainer[] _dispelTasks = new SkillContainer[172800];
	private Stack<SkillContainer> _pool = new Stack<SkillContainer>();
	private ReentrantLock lock = new ReentrantLock();
	private int _currentDispelCell = 0;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> _task;

	private static SkillTaskManager _instance;

	private SkillTaskManager()
	{
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DispelScheduler(), 1000, 1000);
	}

	public static SkillTaskManager getInstance()
	{
		return _instance != null ? _instance : (_instance = new SkillTaskManager());
	}

	/**
	 * интервал в секундах!
	 */
	public SkillContainer addDispelTask(L2Skill e, int interval)
	{
		try
		{
			lock.lock();

			if(interval < 1)
				interval = 1;
			if(interval > _dispelTasks.length/* / 2*/)
			{
				_log.warning("ERROR: Skill " + e.getName() + " I " + interval);
				interval = _dispelTasks.length - 1;
			}
			int cell = _currentDispelCell + interval;
			if(_dispelTasks.length <= cell)
				cell -= _dispelTasks.length;
			if(_dispelTasks[cell] == null)
				if(!_pool.isEmpty())
					_dispelTasks[cell] = _pool.pop();
				else
					_dispelTasks[cell] = new SkillContainer();
			_dispelTasks[cell].addEffect(e);

			return _dispelTasks[cell];
		}
		finally
		{
			lock.unlock();
		}
	}

	private class DispelScheduler extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			GArray<L2Skill> works = new GArray<L2Skill>();

			lock.lock();
			try
			{
				if(_dispelTasks[_currentDispelCell] != null && !_dispelTasks[_currentDispelCell].getList().isEmpty())
					for(WeakReference<L2Skill> we : _dispelTasks[_currentDispelCell].getList())
						try
						{
							L2Skill sk = we.get();
							if(sk == null)
								continue;

							works.add(sk);
							
							// TODO: ???
							addDispelTask(sk, sk.getAbnormalTime() / 1000);
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
				if(_dispelTasks[_currentDispelCell] != null)
				{
					_dispelTasks[_currentDispelCell].clear();
					_pool.push(_dispelTasks[_currentDispelCell]);
					_dispelTasks[_currentDispelCell] = null;
				}
				_currentDispelCell++;
				if(_currentDispelCell >= _dispelTasks.length)
					_currentDispelCell = 0;

				lock.unlock();
			}

			for(L2Skill work : works)
				try
				{
					work.exitEffect();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	private class SkillContainer
	{
		private LinkedList<WeakReference<L2Skill>> list = new LinkedList<WeakReference<L2Skill>>();

		public void addEffect(L2Skill e)
		{
			list.add(new WeakReference<L2Skill>(e));
		}

		public LinkedList<WeakReference<L2Skill>> getList()
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