package l2open.gameserver.taskmanager.tasks;

import l2open.database.mysql;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.taskmanager.Task;
import l2open.gameserver.taskmanager.TaskManager;
import l2open.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2open.gameserver.taskmanager.TaskTypes;

import java.util.logging.Logger;

public class TaskRecom extends Task
{
	private static final Logger _log = Logger.getLogger(TaskRecom.class.getName());
	private static final String NAME = "sp_recommendations";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		_log.info("Recommendation Global Task: launched.");
		for(L2Player player : L2ObjectsStorage.getPlayers())
			player.getRecommendation().restartRecom();
		mysql.set("UPDATE `characters` SET `rec_timeleft`=3600");
		_log.info("Recommendation Global Task: completed.");
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "6:30:00", "");
	}
}