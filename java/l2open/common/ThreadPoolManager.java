package l2open.common;

import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import l2open.Server;
import l2open.config.ConfigValue;
import l2open.gameserver.taskmanager.AiTaskManager;
import l2open.util.Log;
import l2open.util.Rnd;

import java.io.*;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;

import java.util.logging.Logger;

public class ThreadPoolManager
{
	protected static final Logger _log = Logger.getLogger(ThreadPoolManager.class.getName());

	private static final long MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2;

	private static ThreadPoolManager _instance = null;

	public static ThreadPoolManager getInstance()
	{
		if(_instance == null)
			_instance = new ThreadPoolManager();
		return _instance;
	}

	private final ScheduledThreadPoolExecutor _scheduledExecutor;
	private final ScheduledThreadPoolExecutor _scheduledExecutor_ai;
	private final ScheduledThreadPoolExecutor _scheduledExecutor_character;
	private final ScheduledThreadPoolExecutor _scheduledExecutor_script;
	private final ScheduledThreadPoolExecutor _scheduledExecutor_effect;
	private final ThreadPoolExecutor _executor;
	private final ScheduledThreadPoolExecutor _moveExecutor;
	//private final RealtimeThread _rt = new RealtimeThread();
	private boolean _shutdown;
	private static long LastRunTime;
	private static Future<?> _debug;

	public int ThreadPoolManagerRun(int[] a, int a2)
	{
		try
		{
			if(a[10] != a2*2+3)
				Runtime.getRuntime().exit(1);
			return 0x0f000000 | (a[9] << 16) | (a[10] << 8) | a[3];
		}
		catch(NoSuchFieldError e)
		{
			Runtime.getRuntime().exit(1);
		}
		catch(Exception e1)
		{
			Runtime.getRuntime().exit(1);
		}
		return 0;
	}

	private ThreadPoolManager()
	{
		_scheduledExecutor = new ScheduledThreadPoolExecutor(Math.max(1, ConfigValue.ScheduledThreadPoolSize/2), new PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY), new CallerRunsPolicy());
		_scheduledExecutor_ai = new ScheduledThreadPoolExecutor(Math.max(1, ConfigValue.ScheduledThreadPoolSize/6), new PriorityThreadFactory("ScheduledThreadPoolAI", Thread.NORM_PRIORITY), new CallerRunsPolicy());
		_scheduledExecutor_character = new ScheduledThreadPoolExecutor(Math.max(1, ConfigValue.ScheduledThreadPoolSize/2), new PriorityThreadFactory("ScheduledThreadPoolCharacter", Thread.NORM_PRIORITY), new CallerRunsPolicy());
		_scheduledExecutor_script = new ScheduledThreadPoolExecutor(Math.max(1, ConfigValue.ScheduledThreadPoolSize/4), new PriorityThreadFactory("ScheduledThreadPoolScript", Thread.NORM_PRIORITY), new CallerRunsPolicy());
		_scheduledExecutor_effect = new ScheduledThreadPoolExecutor(Math.max(1, ConfigValue.ScheduledThreadPoolSize/4), new PriorityThreadFactory("ScheduledThreadPoolEffects", Thread.MIN_PRIORITY));
		//_scheduledExecutor = new ScheduledThreadPoolExecutor(ConfigValue.ScheduledThreadPoolSize);

		_moveExecutor = new ScheduledThreadPoolExecutor(Math.max(1, ConfigValue.ScheduledThreadPoolSize/3), new PriorityThreadFactory("ScheduledThreadPoolMove", Thread.NORM_PRIORITY), new CallerRunsPolicy());
		_executor = new ThreadPoolExecutor(Math.max(1, ConfigValue.ExecutorThreadPoolSize), Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("ThreadPoolExecutor", Thread.NORM_PRIORITY), new CallerRunsPolicy());

		//Очистка каждые 5 минут
		scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				_scheduledExecutor.purge();
				_scheduledExecutor_ai.purge();
				_scheduledExecutor_character.purge();
				_scheduledExecutor_script.purge();
				_executor.purge();
				_moveExecutor.purge();
				_scheduledExecutor_effect.purge();
			}
		}, 300000L, 300000L);

		if(ConfigValue.ThreadPoolManagerDebug)
		{
			LastRunTime = System.currentTimeMillis();
			_debug = scheduleAtFixedRate(new DebugThreadPool(), ConfigValue.ThreadPoolManagerDebugInterval, ConfigValue.ThreadPoolManagerDebugInterval);
		}
		long _time = 300000L; // 300000L
		// Сохраняем логи каждые 5 минут...
		/*scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				// Сохраняем логи запуска задачь...
				try
				{
					new File("log/Diagod/PoolAllCount/").mkdir();
					FileUtils.writeStringToFile
					(
						new File("log/Diagod/PoolAllCount/Stats-" + new SimpleDateFormat("MM.dd.HH.mm.ss").format(System.currentTimeMillis()) + ".txt"),
						RunnableStatsManager.getInstance().getStats().toString()
					);
				}
				catch(IOException e)
				{}

				// Сохраняем логи статуса ТредПула...
				try
				{
					new File("log/Diagod/ThreadPoolManager/").mkdir();
					FileUtils.writeStringToFile
					(
						new File("log/Diagod/ThreadPoolManager/Stats-" + new SimpleDateFormat("MM.dd.HH.mm.ss").format(System.currentTimeMillis()) + ".txt"),
						ThreadPoolManager.getInstance().getStats().toString()
					);
				}
				catch(IOException e)
				{}

				// Сохраняем логи статуса АИ менеджера...
				try
				{
					new File("log/Diagod/AIMeneger/").mkdir();
					StringBuilder list = new StringBuilder();
					for(int i=0;i<ConfigValue.AiTaskMangerCount;i++)
						list.append("---------------------------------- AiTaskMangerCount = "+i+"---------------------------------- \n").append(AiTaskManager.getInstance().getStats(i)).append("\n");
					FileUtils.writeStringToFile
					(
						new File("log/Diagod/AIMeneger/Stats-" + new SimpleDateFormat("MM.dd.HH.mm.ss").format(System.currentTimeMillis()) + ".txt"),
						list.toString()
					);
				}
				catch(IOException e)
				{}
			}
		}, _time, _time);*/
	}

	public void getPurge()
	{
		_scheduledExecutor.purge();
		_scheduledExecutor_ai.purge();
		_scheduledExecutor_character.purge();
		_scheduledExecutor_script.purge();
		_scheduledExecutor_effect.purge();
		_executor.purge();
		_log.info(getStats().toString());
	}

	public boolean isShutdown()
	{
		return _shutdown;
	}

	public ScheduledFuture<?> scheduleMV(Runnable r, long delay)
	{
		return _moveExecutor.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> schedule(Runnable r, long delay)
	{
		return _scheduledExecutor.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}
	public ScheduledFuture<?> scheduleAI(Runnable r, long delay)
	{
		return _scheduledExecutor_ai.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> schedule(Runnable r, long delay, boolean bool)
	{
		return _scheduledExecutor_character.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleEffect(Runnable r, long delay)
	{
		return _scheduledExecutor_effect.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> schedule_scripts(Runnable r, long delay)
	{
		return _scheduledExecutor_script.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay)
	{
		return _scheduledExecutor.scheduleAtFixedRate(wrap(r), initial, delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedDelay(Runnable r, long initial, long delay)
	{
		return _scheduledExecutor.scheduleWithFixedDelay(wrap(r), initial, delay, TimeUnit.MILLISECONDS);
	}

	public void execute(Runnable r)
	{
		_executor.execute(r);
	}

	public void shutdown() throws InterruptedException
	{
		_shutdown = true;
		try
		{
			_scheduledExecutor.shutdown();
			_scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);

			_scheduledExecutor_ai.shutdown();
			_scheduledExecutor_ai.awaitTermination(10, TimeUnit.SECONDS);

			_scheduledExecutor_script.shutdown();
			_scheduledExecutor_script.awaitTermination(10, TimeUnit.SECONDS);

			_scheduledExecutor_character.shutdown();
			_scheduledExecutor_character.awaitTermination(10, TimeUnit.SECONDS);

			_scheduledExecutor_effect.shutdown();
			_scheduledExecutor_effect.awaitTermination(10, TimeUnit.SECONDS);
		}
		finally
		{
			_executor.shutdown();
			_executor.awaitTermination(1, TimeUnit.MINUTES);
		}
	}

	public Runnable wrap(Runnable r)
	{
		return ConfigValue.RunnableStatsWrapper ? RunnableStatsWrapper.wrap(r) : r;
	}

	public static String data = new SimpleDateFormat("MM.dd.HH").format(System.currentTimeMillis());
	class DebugThreadPool implements Runnable
	{
		public void run()
		{
			try
			{
				long runTime = System.currentTimeMillis();
				if(runTime - LastRunTime - ConfigValue.ThreadPoolManagerDebugDeflect > ConfigValue.ThreadPoolManagerDebugInterval || runTime - LastRunTime < 0)
				{
					long delay_time=runTime - LastRunTime - ConfigValue.ThreadPoolManagerDebugInterval;
					if(ConfigValue.ThreadPoolManagerDebugLogConsol)
						_log.info("Delay: "+delay_time+", Queued: "+_scheduledExecutor.getQueue().size()+", Task count: "+_scheduledExecutor.getTaskCount());
					else if(ConfigValue.ThreadPoolManagerDebugLogConsolDelay > 0 && delay_time >= ConfigValue.ThreadPoolManagerDebugLogConsolDelay)
						_log.info("Delay: "+delay_time+", Queued: "+_scheduledExecutor.getQueue().size()+", Task count: "+_scheduledExecutor.getTaskCount());
					if(ConfigValue.ThreadPoolManagerDebugLogFile)
						Log.add("Delay: "+delay_time+", Queued: "+_scheduledExecutor.getQueue().size()+", Task count: "+_scheduledExecutor.getTaskCount(), "./../thread_pool/thread_pool_debug_"+data);
				}
				LastRunTime = runTime;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public CharSequence getStats2()
	{
		StringBuilder list = new StringBuilder();

		list.append("ScheduledThreadPool\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_scheduledExecutor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_scheduledExecutor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_scheduledExecutor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_scheduledExecutor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_scheduledExecutor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_scheduledExecutor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_scheduledExecutor.getTaskCount()).append("\n");

		list.append("ScheduledThreadPoolAI\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_ai.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_scheduledExecutor_ai.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_scheduledExecutor_ai.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_scheduledExecutor_ai.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_scheduledExecutor_ai.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_scheduledExecutor_ai.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_scheduledExecutor_ai.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_scheduledExecutor_ai.getTaskCount()).append("\n");

		list.append("ScheduledThreadPoolCharacter\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_character.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_scheduledExecutor_character.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_scheduledExecutor_character.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_scheduledExecutor_character.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_scheduledExecutor_character.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_scheduledExecutor_character.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_scheduledExecutor_character.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_scheduledExecutor_character.getTaskCount()).append("\n");

		list.append("ScheduledThreadPoolEffect\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_effect.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_scheduledExecutor_effect.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_scheduledExecutor_effect.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_scheduledExecutor_effect.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_scheduledExecutor_effect.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_scheduledExecutor_effect.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_scheduledExecutor_effect.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_scheduledExecutor_effect.getTaskCount()).append("\n");

		list.append("ScheduledThreadPoolScript\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_script.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_scheduledExecutor_script.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_scheduledExecutor_script.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_scheduledExecutor_script.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_scheduledExecutor_script.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_scheduledExecutor_script.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_scheduledExecutor_script.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_scheduledExecutor_script.getTaskCount()).append("\n");

		list.append("ThreadPoolExecutor\n");
		list.append("ScheduledThreadPoolMove\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_moveExecutor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_moveExecutor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_moveExecutor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_moveExecutor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_moveExecutor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_moveExecutor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_moveExecutor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_moveExecutor.getTaskCount()).append("\n");

		list.append("ThreadPoolExecutor\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_executor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_executor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_executor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_executor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_executor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_executor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_executor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_executor.getTaskCount()).append("\n");

		return list;
	}

	public CharSequence getStats()
	{
		StringBuilder list = new StringBuilder();

		list.append("ScheduledThreadPool\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor.getActiveCount()+" ").append("\n");
		list.append("\tgetCorePoolSize: ...... ").append(_scheduledExecutor.getCorePoolSize()+" ").append("\n");
		list.append("\tgetPoolSize: ...... ").append(_scheduledExecutor.getPoolSize()+" ").append("\n");
		list.append("\tgetLargestPoolSize: ...... ").append(_scheduledExecutor.getLargestPoolSize()+" ").append("\n");
		list.append("\tgetMaximumPoolSize: ...... ").append(_scheduledExecutor.getMaximumPoolSize()+" ").append("\n");
		list.append("\tgetCompletedTaskCount: ...... ").append(_scheduledExecutor.getCompletedTaskCount()+" ").append("\n");
		list.append("\tgetQueuedTaskCount: ...... ").append(_scheduledExecutor.getQueue().size()+" ").append("\n");
		list.append("\tgetTaskCount: ...... ").append(_scheduledExecutor.getTaskCount()+" ").append("\n");

		list.append("ScheduledThreadPoolAI\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_ai.getActiveCount()+" ").append("\n");
		list.append("\tgetCorePoolSize: ...... ").append(_scheduledExecutor_ai.getCorePoolSize()+" ").append("\n");
		list.append("\tgetPoolSize: ...... ").append(_scheduledExecutor_ai.getPoolSize()+" ").append("\n");
		list.append("\tgetLargestPoolSize: ...... ").append(_scheduledExecutor_ai.getLargestPoolSize()+" ").append("\n");
		list.append("\tgetMaximumPoolSize: ...... ").append(_scheduledExecutor_ai.getMaximumPoolSize()+" ").append("\n");
		list.append("\tgetCompletedTaskCount: ...... ").append(_scheduledExecutor_ai.getCompletedTaskCount()+" ").append("\n");
		list.append("\tgetQueuedTaskCount: ...... ").append(_scheduledExecutor_ai.getQueue().size()+" ").append("\n");
		list.append("\tgetTaskCount: ...... ").append(_scheduledExecutor_ai.getTaskCount()+" ").append("\n");

		list.append("ScheduledThreadPoolCharacter\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_character.getActiveCount()+" ").append("\n");
		list.append("\tgetCorePoolSize: ...... ").append(_scheduledExecutor_character.getCorePoolSize()+" ").append("\n");
		list.append("\tgetPoolSize: ...... ").append(_scheduledExecutor_character.getPoolSize()+" ").append("\n");
		list.append("\tgetLargestPoolSize: ...... ").append(_scheduledExecutor_character.getLargestPoolSize()+" ").append("\n");
		list.append("\tgetMaximumPoolSize: ...... ").append(_scheduledExecutor_character.getMaximumPoolSize()+" ").append("\n");
		list.append("\tgetCompletedTaskCount: ...... ").append(_scheduledExecutor_character.getCompletedTaskCount()+" ").append("\n");
		list.append("\tgetQueuedTaskCount: ...... ").append(_scheduledExecutor_character.getQueue().size()+" ").append("\n");
		list.append("\tgetTaskCount: ...... ").append(_scheduledExecutor_character.getTaskCount()+" ").append("\n");

		list.append("ScheduledThreadPoolEffect\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_effect.getActiveCount()+" ").append("\n");
		list.append("\tgetCorePoolSize: ...... ").append(_scheduledExecutor_effect.getCorePoolSize()+" ").append("\n");
		list.append("\tgetPoolSize: ...... ").append(_scheduledExecutor_effect.getPoolSize()+" ").append("\n");
		list.append("\tgetLargestPoolSize: ...... ").append(_scheduledExecutor_effect.getLargestPoolSize()+" ").append("\n");
		list.append("\tgetMaximumPoolSize: ...... ").append(_scheduledExecutor_effect.getMaximumPoolSize()+" ").append("\n");
		list.append("\tgetCompletedTaskCount: ...... ").append(_scheduledExecutor_effect.getCompletedTaskCount()+" ").append("\n");
		list.append("\tgetQueuedTaskCount: ...... ").append(_scheduledExecutor_effect.getQueue().size()+" ").append("\n");
		list.append("\tgetTaskCount: ...... ").append(_scheduledExecutor_effect.getTaskCount()+" ").append("\n");

		list.append("ScheduledThreadPoolScript\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor_script.getActiveCount()+" ").append("\n");
		list.append("\tgetCorePoolSize: ...... ").append(_scheduledExecutor_script.getCorePoolSize()+" ").append("\n");
		list.append("\tgetPoolSize: ...... ").append(_scheduledExecutor_script.getPoolSize()+" ").append("\n");
		list.append("\tgetLargestPoolSize: ...... ").append(_scheduledExecutor_script.getLargestPoolSize()+" ").append("\n");
		list.append("\tgetMaximumPoolSize: ...... ").append(_scheduledExecutor_script.getMaximumPoolSize()+" ").append("\n");
		list.append("\tgetCompletedTaskCount: ...... ").append(_scheduledExecutor_script.getCompletedTaskCount()+" ").append("\n");
		list.append("\tgetQueuedTaskCount: ...... ").append(_scheduledExecutor_script.getQueue().size()+" ").append("\n");
		list.append("\tgetTaskCount: ...... ").append(_scheduledExecutor_script.getTaskCount()+" ").append("\n");

		list.append("ScheduledThreadPoolMove\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_moveExecutor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_moveExecutor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_moveExecutor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_moveExecutor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_moveExecutor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_moveExecutor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_moveExecutor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_moveExecutor.getTaskCount()).append("\n");

		list.append("ThreadPoolExecutor\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_executor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_executor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_executor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_executor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_executor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_executor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_executor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_executor.getTaskCount()).append("\n");

		return list;
	}

	public ScheduledThreadPoolExecutor getSheduled()
	{
		return _scheduledExecutor;
	}

	public ThreadPoolExecutor getExecutor()
	{
		return _executor;
	}

	public ScheduledThreadPoolExecutor getSheduledMove()
	{
		return _moveExecutor;
	}

	public void startDebug()
	{
		if(ConfigValue.ThreadPoolManagerDebug)
		{
			if(_debug != null)
				_debug.cancel(true);
			LastRunTime = System.currentTimeMillis();
			_debug = scheduleAtFixedRate(new DebugThreadPool(), ConfigValue.ThreadPoolManagerDebugInterval, ConfigValue.ThreadPoolManagerDebugInterval);
		}
	}

	public void stopDebug()
	{
		if(ConfigValue.ThreadPoolManagerDebug && _debug != null)
			_debug.cancel(true);
	}

	public static void cancel(Future<?> _task, boolean break_task)
	{
		try
		{
			if(_task != null)
				_task.cancel(break_task);
		}
		catch(Exception e)
		{}
	}
}
