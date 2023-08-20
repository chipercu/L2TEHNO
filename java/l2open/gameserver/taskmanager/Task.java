package l2open.gameserver.taskmanager;

import l2open.gameserver.taskmanager.TaskManager.ExecutedTask;

import java.util.concurrent.ScheduledFuture;

public abstract class Task
{
	public abstract void initializate();

	public ScheduledFuture<?> launchSpecial(ExecutedTask instance)
	{
		return null;
	}

	public abstract String getName();

	public abstract void onTimeElapsed(ExecutedTask task);

	public void onDestroy()
	{}
}