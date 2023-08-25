package l2open.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class DummyDeadlock extends Thread
{
	public DummyDeadlock()
	{
		super();
		setName(getClass().getSimpleName());
	}

	/**
	 * Дедлок посредством ReentrantLock
	 */
	public static class ReentrantDeadlock extends DummyDeadlock
	{
		private final Lock lock1 = new ReentrantLock(), lock2 = new ReentrantLock();

		@Override
		public void a()
		{
			lock1.lock();
			try
			{
				lock2.lock();
				try
				{
					// dummy
				}
				finally
				{
					lock2.unlock();
				}
			}
			finally
			{
				lock1.unlock();
			}
		}

		@Override
		public void b()
		{
			lock2.lock();
			try
			{
				a();
			}
			finally
			{
				lock2.unlock();
			}
		}
	}

	/**
	 * Дедлок посредством synchronized
	 */
	public static class SynchronizedDeadlock extends DummyDeadlock
	{
		private final Object lock = new Object();

		@Override
		public synchronized void a()
		{
			synchronized (lock)
			{
				// dummy
			}
		}

		@Override
		public void b()
		{
			synchronized (lock)
			{
				a();
			}
		}
	}

	@Override
	public void run()
	{
		new SecondThread().start();

		while(true)
			try
			{
				b();
				Thread.sleep(1);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	class SecondThread extends Thread
	{
		public SecondThread()
		{
			super(DummyDeadlock.this.getClass().getSimpleName() + ":SecondThread");
		}

		@Override
		public void run()
		{
			while(true)
				try
				{
					a();
					Thread.sleep(1);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	abstract void a();

	abstract void b();
}