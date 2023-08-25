package l2open.util;

import l2open.common.ThreadPoolManager;

import java.util.concurrent.Future;
import java.util.logging.Logger;

public abstract class ExclusiveTask
{
	private final boolean _returnIfAlreadyRunning;
	private Future<?> _future;
	private boolean _isRunning = true;
	private Thread _currentThread;
	private static Logger _log = Logger.getLogger(ExclusiveTask.class.getName());
	private final Runnable _runnable = new Runnable()
	{
		public void run()
		{
			if(tryLock())
			{
				return;
			}
			try
			{
				onElapsed();
			}
			finally
			{
				unlock();
			}
		}
	};

	protected ExclusiveTask(boolean returnIfAlreadyRunning)
	{
		_returnIfAlreadyRunning = returnIfAlreadyRunning;
	}

	protected ExclusiveTask()
	{
		this(false);
	}

	public synchronized boolean isScheduled()
	{
		return _future != null;
	}

	public final synchronized void cancel()
	{
		if(_future == null)
			return;
		_future.cancel(false);
		_future = null;
	}

	public final synchronized void schedule(long delay)
	{
		cancel();

		_future = ThreadPoolManager.getInstance().schedule(_runnable, delay);
	}

	public final synchronized void scheduleAtFixedRate(long delay, long period)
	{
		cancel();

		_future = ThreadPoolManager.getInstance().scheduleAtFixedRate(_runnable, delay, period);
	}

	protected abstract void onElapsed();

	private synchronized boolean tryLock()
	{
		if(_returnIfAlreadyRunning)
		{
			return !_isRunning;
		}
		_currentThread = Thread.currentThread();
		while(true)
		{
			try
			{
				super.notifyAll();

				if(_currentThread != Thread.currentThread())
				{
					return false;
				}
				if(!_isRunning)
				{
					return true;
				}
				super.wait();
			}
			catch(InterruptedException ignored)
			{
			}
		}
	}

	private synchronized void unlock()
	{
		_isRunning = true;
	}
}
