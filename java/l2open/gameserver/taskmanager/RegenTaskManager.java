package l2open.gameserver.taskmanager;

import l2open.common.*;

public class RegenTaskManager extends SteppingRunnable
{
	private static final RegenTaskManager _instance = new RegenTaskManager();
	public static final RegenTaskManager getInstance()
	{
		return _instance;
	}

	private RegenTaskManager()
	{
		super(333L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 333L, 333L);
		//Очистка каждые 10 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				RegenTaskManager.this.purge();
			}
		}, 10000L, 10000L);
	}
}