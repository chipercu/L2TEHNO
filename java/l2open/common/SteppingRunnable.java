package l2open.common;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.mutable.MutableLong;

import l2open.config.ConfigValue;
import l2open.common.collections.LazyArrayList;
import l2open.gameserver.model.L2Object;

/**
 * Менеджер очереди задач с кратным запланированным временем выполнения.
 * 
 * @author G1ta0
 */
public abstract class SteppingRunnable implements Runnable
{
	public static final Logger _log = Logger.getLogger(SteppingRunnable.class.getName());

	protected final long tickPerStepInMillis;
	public final List<SteppingScheduledFuture<?>> queue = new CopyOnWriteArrayList<SteppingScheduledFuture<?>>();
	public final AtomicBoolean isRunning = new AtomicBoolean();
	public final AtomicLong set_id = new AtomicLong(0);
	public Future<?> _purge_task;

	public SteppingRunnable(long tickPerStepInMillis)
	{
		this.tickPerStepInMillis = tickPerStepInMillis;
	}

	public class SteppingScheduledFuture<V> implements RunnableScheduledFuture<V>
	{
		public final Runnable r;
		public String text = "-";
		public final long stepping;
		public final boolean isPeriodic;

		public long step;
		public boolean isCancelled;

		public L2Object object;
		public Thread thread;
		public final AtomicLong isRunning = new AtomicLong(0); // служит для подсчета времени выполнения.

		public SteppingScheduledFuture(L2Object o, Runnable r, long initial, long stepping, boolean isPeriodic)
		{
			text = r.getClass().getName()+"[cur_id:"+set_id.getAndIncrement()+"]";
			object = o;
			this.r = r;
			this.step = initial;
			this.stepping = stepping;
			this.isPeriodic = isPeriodic;
		}

		@Override
		public void run()
		{
			if(--step == 0)
				try
				{
					thread = Thread.currentThread(); // сохраняем ссылку на поток, который запускает задачу...нужно только для отладки в случае каких проблем...
					isRunning.set(System.nanoTime()); // задаем время выполнения
					r.run();
				}
				catch(Exception e)
				{
					if(ConfigValue.RunnableLog)
						_log.log(Level.SEVERE, "Exception in a Runnable execution: =" + text + "= ", e);
				}
				finally
				{
					if(isPeriodic)
						step = stepping;
					isRunning.set(0);
				}
		}

		public Runnable getRunnable()
		{
			return r;
		}

		@Override
		public boolean isDone()
		{
			return isCancelled || !isPeriodic && step == 0;
		}

		@Override
		public boolean isCancelled()
		{
			return isCancelled;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning)
		{
			return isCancelled = true;
		}

		@Override
		public V get() throws InterruptedException, ExecutionException
		{
			return null;
		}

		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
		{
			return null;
		}

		@Override
		public long getDelay(TimeUnit unit)
		{
			return unit.convert(step * tickPerStepInMillis, TimeUnit.MILLISECONDS);
		}

		@Override
		public int compareTo(Delayed o)
		{
			return 0;
		}

		@Override
		public boolean isPeriodic()
		{
			return isPeriodic;
		}

		public Thread getThread()
		{
			return thread;
		}

		public String toString()
		{
			return text;
		}
	}

	/**
	 * Запланировать выполнение задачи через промежуток времени
	 * @param r задача для выполнения
	 * @param delay задержка в миллисекундах
	 * @return SteppingScheduledFuture управляющий объект, отвечающий за выполенение задачи
	 */
	public SteppingScheduledFuture<?> schedule(Runnable r, long delay)
	{
		return schedule(null, r, delay, delay, false);
	}

	/**
	 * Запланировать выполнение задачи через равные промежутки времени, с начальной задержкой
	 * @param r задача для выполнения
	 * @param initial начальная задержка в миллисекундах
	 * @param delay период выполенения в силлисекундах
	 * @return SteppingScheduledFuture управляющий объект, отвечающий за выполенение задачи
	 */
	public SteppingScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay)
	{
		return schedule(null, r, initial, delay, true);
	}

	public SteppingScheduledFuture<?> scheduleAtFixedRate(L2Object object, Runnable r, long initial, long delay)
	{
		return schedule(object, r, initial, delay, true);
	}

	public SteppingScheduledFuture<?> schedule(Runnable r, long initial, long delay, boolean isPeriodic)
	{
		return schedule(null, r, initial, delay, isPeriodic);
	}
	public SteppingScheduledFuture<?> schedule(L2Object object, Runnable r, long initial, long delay, boolean isPeriodic)
	{
		SteppingScheduledFuture<?> sr;

		long initialStepping = getStepping(initial);
		long stepping = getStepping(delay);

		queue.add(sr = new SteppingScheduledFuture<Boolean>(object, r, initialStepping, stepping, isPeriodic));

		return sr;
	}

	/**
	 * Выбираем "степпинг" для работы задачи:
	 * если delay меньше шага выполнения, результат будет равен 1
	 * если delay больше шага выполнения, результат будет результатом округления от деления delay / step
	 */
	public long getStepping(long delay)
	{
		delay = Math.max(0, delay);
		return delay % tickPerStepInMillis > tickPerStepInMillis / 2 ? delay / tickPerStepInMillis + 1 : delay < tickPerStepInMillis ? 1 : delay / tickPerStepInMillis;
	}

	@Override
	public void run()
	{
		if (!isRunning.compareAndSet(false, true))
		{
			_log.warning("Slow running queue, managed by " + this + ", queue size : " + queue.size() + "!");
			return;
		}

		try
		{
			if (queue.isEmpty())
				return;

			for(SteppingScheduledFuture<?> sr: queue)
				if(!sr.isDone())
				{
					if(sr.getRunnable() != null)
						sr.run();
					else
						_log.warning("~Error-1~ This " + this + " running == Null");
				}
		}
		finally
		{
			isRunning.set(false);
		}
	}

	/**
	 * Очистить очередь от выполенных и отмененных задач.
	 */
	public void purge()
	{
		LazyArrayList<SteppingScheduledFuture<?>> purge = LazyArrayList.newInstance();

		for(SteppingScheduledFuture<?> sr: queue)
		{
			if(sr.isDone())
				purge.add(sr);
			if(sr.getRunnable() == null)
				_log.warning("~Error-2~ This " + this + " running == Null clyned!!!");
		}

		queue.removeAll(purge);

		LazyArrayList.recycle(purge);
	}

	public CharSequence getStats()
	{
		StringBuilder list = new StringBuilder();

		Map<String, MutableLong> stats = new TreeMap<String, MutableLong>();
		int total = 0;
		int done = 0;
		int nulls = 0;

		for(SteppingScheduledFuture<?> sr: queue)
		{
			if (sr.isDone())
			{
				done++;
				continue;
			}
			if (sr.getRunnable() == null)
			{
				nulls++;
				continue;
			}

			total++;
			MutableLong count = stats.get(sr.r.getClass().getName());
			if (count == null)
				stats.put(sr.r.getClass().getName(), count = new MutableLong(1L));
			else
				count.increment();
		}

		for(Map.Entry<String, MutableLong> e : stats.entrySet())
			list.append("\t").append(e.getKey()).append(" : ").append(e.getValue().longValue()).append("\n");

		list.append("Scheduled: ....... ").append(total).append("\n");
		list.append("Done/Cancelled: .. ").append(done).append("\n");
		list.append("Runnable null: ... ").append(nulls).append("\n");

		return list;
	}

	public void stopPurge()
	{
		if(_purge_task != null)
			_purge_task.cancel(true);
	}

	public void startPurge()
	{}
}
