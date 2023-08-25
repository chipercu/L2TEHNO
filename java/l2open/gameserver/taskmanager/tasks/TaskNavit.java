package l2open.gameserver.taskmanager.tasks;

import l2open.database.mysql;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.taskmanager.Task;
import l2open.gameserver.taskmanager.TaskManager;
import l2open.gameserver.taskmanager.TaskTypes;

import java.util.logging.Logger;

public class TaskNavit extends Task
{
	private static final Logger _log = Logger.getLogger(TaskNavit.class.getName());
	private static final String NAME = "clear_nevit";

	public String getName()
	{
		return NAME;
	}

	public void onTimeElapsed(TaskManager.ExecutedTask task)
	{
		_log.info("Navit Global Task: launched.");
		for (L2Player player : L2ObjectsStorage.getPlayers())
			player.getNevitBlessing().setBonusTime(0);
		mysql.set("UPDATE `characters` SET `hunt_bonus`=0");

		_log.info("Navit Global Task: completed.");
	}

	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "6:00:00", "");
	}
}