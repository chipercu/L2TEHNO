package l2open.gameserver.taskmanager;

import java.util.concurrent.Future;

import l2open.config.ConfigValue;
import l2open.common.*;
import l2open.gameserver.model.L2Player;
import l2open.util.Rnd;

public class LazyPrecisionTaskManager extends SteppingRunnable
{
	private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

	public static final LazyPrecisionTaskManager getInstance()
	{
		return _instance;
	}

	private LazyPrecisionTaskManager()
	{
		super(1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		//Очистка каждые 60 секунд
		_purge_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				LazyPrecisionTaskManager.this.purge();
			}

		}, ConfigValue.Dev_LazyTaskPurgeTime, ConfigValue.Dev_LazyTaskPurgeTime);
	}

	public void startPurge()
	{
		_purge_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				LazyPrecisionTaskManager.this.purge();
			}

		}, ConfigValue.Dev_LazyTaskPurgeTime, ConfigValue.Dev_LazyTaskPurgeTime);
	}

	public Future<?> addPCCafePointsTask(final L2Player player)
	{
		long delay = ConfigValue.AltPcBangPointsDelay * 60000L;

		return scheduleAtFixedRate(new RunnableImpl(){

			@Override
			public void runImpl() throws Exception
			{
				if(player.isInOfflineMode() || player.getLevel() < ConfigValue.AltPcBangPointsMinLvl || !ConfigValue.AltPcBangPointsEnabled)
					return;

				player.addPcBangPoints((int)(ConfigValue.AltPcBangPointsBonus*player.getAltBonus()), ConfigValue.AltPcBangPointsDoubleChance > 0 && Rnd.chance(ConfigValue.AltPcBangPointsDoubleChance), 1);
			}

		}, delay, delay);
	}
}
