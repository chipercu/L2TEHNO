package l2open.gameserver.taskmanager;

import l2open.config.ConfigValue;
import l2open.common.*;
import l2open.util.Rnd;

import java.util.concurrent.Future;

/**
 * Менеджер задач AI, шаг выполенния задач 250 мс.
 * 
 * @author G1ta0
 */
public class AiTaskManager extends SteppingRunnable
{
	private final static long TICK = 250;

	public Future<?> _task;

	public static AiTaskManager _instances = new AiTaskManager();

	public final static AiTaskManager getInstance()
	{
		return _instances;
	}

	public AiTaskManager()
	{
		super(TICK);
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(1, TICK), TICK);
		//Очистка каждую минуту
		_purge_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				AiTaskManager.this.purge();
			}

		}, ConfigValue.Dev_AITaskPurgeTime, ConfigValue.Dev_AITaskPurgeTime);
	}

	public void startPurge()
	{
		_purge_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				AiTaskManager.this.purge();
			}

		}, ConfigValue.Dev_AITaskPurgeTime, ConfigValue.Dev_AITaskPurgeTime);
	}

	public void restart()
	{
		if(_task != null)
			_task.cancel(true);
		isRunning.set(false);
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, TICK, TICK);
	}

	public CharSequence getStats(int num)
	{
		return _instances.getStats();
	}
}
