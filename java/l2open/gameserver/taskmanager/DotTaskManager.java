package l2open.gameserver.taskmanager;

import javolution.util.ReentrantLock;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Effect;
import l2open.util.GArray;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class DotTaskManager
{
	private static final Logger _log = Logger.getLogger(DotTaskManager.class.getName());

	private EffectContainer[] _dispelTasks = new EffectContainer[172800];
	private Stack<EffectContainer> _pool = new Stack<EffectContainer>();
	private ReentrantLock lock = new ReentrantLock();
	private int _currentDispelCell = 0;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> _task;

	private static DotTaskManager _instance;

	private DotTaskManager()
	{
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DispelScheduler(), 666, 666);
	}

	public static DotTaskManager getInstance()
	{
		return _instance != null ? _instance : (_instance = new DotTaskManager());
	}

	public EffectContainer addDispelTask(L2Effect e, int interval)
	{
		try
		{
			lock.lock();

			if(interval < 1)
				interval = 1;
			if(interval > _dispelTasks.length / 2)
			{
				_log.warning("ERROR: Effect(DOT) " + e.getSkill().getName() + " I " + interval);
				interval = _dispelTasks.length - 1;
			}
			int cell = _currentDispelCell + interval;
			if(_dispelTasks.length <= cell)
				cell -= _dispelTasks.length;
			if(_dispelTasks[cell] == null)
				if(!_pool.isEmpty())
					_dispelTasks[cell] = _pool.pop();
				else
					_dispelTasks[cell] = new EffectContainer();
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
			GArray<L2Effect> works = new GArray<L2Effect>();

			lock.lock();
			try
			{
				if(_dispelTasks[_currentDispelCell] != null && !_dispelTasks[_currentDispelCell].getList().isEmpty())
					for(WeakReference<L2Effect> we : _dispelTasks[_currentDispelCell].getList())
						try
						{
							L2Effect eff = we.get();
							if(eff == null || eff.isFinished())
								continue;

							works.add(eff);

							// TODO не баг, но так получается лишний цикл (пустой, т.к. сработает eff.isFinished())
							if(!eff.isEnded())
								addDispelTask(eff, eff._tick_time);
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

			for(L2Effect work : works)
				try
				{
					work.onActionTime();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	private class EffectContainer
	{
		private LinkedList<WeakReference<L2Effect>> list = new LinkedList<WeakReference<L2Effect>>();

		public void addEffect(L2Effect e)
		{
			list.add(new WeakReference<L2Effect>(e));
		}

		public LinkedList<WeakReference<L2Effect>> getList()
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