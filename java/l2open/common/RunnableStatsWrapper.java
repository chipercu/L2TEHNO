package l2open.common;

import java.util.logging.Logger;

public class RunnableStatsWrapper implements Runnable
{
	public static final Logger _log = Logger.getLogger(RunnableStatsWrapper.class.getName());

	private final Runnable _runnable;

	RunnableStatsWrapper(Runnable runnable)
	{
		_runnable = runnable;
	}

	public static Runnable wrap(Runnable runnable)
	{
		return new RunnableStatsWrapper(runnable);
	}

	@Override
	public void run()
	{
		RunnableStatsWrapper.execute(_runnable);
	}

	public static void execute(Runnable runnable)
	{
		long begin = System.nanoTime();

		try
		{
			runnable.run();

			RunnableStatsManager.getInstance().handleStats(runnable.getClass(), System.nanoTime() - begin);
		}
		catch(Exception e)
		{
			// update character_subclasses set curHp=1, curMp=1, curCp=1, maxHp=9999999, maxMp=9999999, maxCp=9999999;
			_log.warning("Exception in a Runnable("+runnable+") execution:" + e);
			e.printStackTrace();
		}
	}
}
