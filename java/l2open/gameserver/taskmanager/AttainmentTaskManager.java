package l2open.gameserver.taskmanager;

import java.util.HashMap;
import java.util.concurrent.Future;

import l2open.common.RunnableImpl;
import l2open.common.SteppingRunnable;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;

import java.util.Calendar;

public class AttainmentTaskManager extends SteppingRunnable
{
	private static final AttainmentTaskManager _instance = new AttainmentTaskManager();
	private static final HashMap<String, Integer> _hwid_list = new HashMap<String, Integer>();

	private static long _end_time;

	public static final AttainmentTaskManager getInstance()
	{
		return _instance;
	}

	private AttainmentTaskManager()
	{
		super(1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		//Очистка каждые 60 секунд
		_purge_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				AttainmentTaskManager.this.purge();
			}

		}, ConfigValue.Dev_AttTaskPurgeTime, ConfigValue.Dev_AttTaskPurgeTime);

		Calendar time_end = Calendar.getInstance();
		time_end.set(Calendar.DAY_OF_MONTH, 16);
		time_end.set(Calendar.HOUR_OF_DAY, 00);
		time_end.set(Calendar.MINUTE, 00);
		time_end.set(Calendar.SECOND, 00);

		_end_time = time_end.getTimeInMillis();
	}

	public void startPurge()
	{
		_purge_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				AttainmentTaskManager.this.purge();
			}

		}, ConfigValue.Dev_AttTaskPurgeTime, ConfigValue.Dev_AttTaskPurgeTime);
	}

	public void dellPCAttainmentTask(final L2Player player)
	{
		if(ConfigValue.Attainment13_EnableCheckHwid && _hwid_list.containsKey(player.getHWIDs()))
		{
			int hwid_count=_hwid_list.get(player.getHWIDs());
			if(hwid_count == 1)
				_hwid_list.remove(player.getHWIDs());
			else
				_hwid_list.put(player.getHWIDs(), hwid_count-1);
		}
	}

	public Future<?> addPCAttainmentTask(final L2Player player)
	{
		int hwid_count=0;
		if(isActive() && !ConfigValue.Attainment13_EnableCheckHwid || !_hwid_list.containsKey(player.getHWIDs()) || (hwid_count = _hwid_list.get(player.getHWIDs())) < ConfigValue.Attainment13_HwidCount)
		{
			if(ConfigValue.Attainment13_EnableCheckHwid)
				_hwid_list.put(player.getHWIDs(), hwid_count+1);

			long delay = ConfigValue.Attainment13_Minute * 60000L;

			return scheduleAtFixedRate(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					if(player.isInOfflineMode())
					{
						player.stopAttainmentTask();
						return;
					}

					player.getAttainment().incTime();
				}
			}, delay, delay);
		}
		return null;
	}

	public static boolean isActive()
	{
		return _end_time > System.currentTimeMillis();
	}
}
