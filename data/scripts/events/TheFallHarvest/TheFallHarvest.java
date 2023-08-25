package events.TheFallHarvest;

import java.io.File;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;

public class TheFallHarvest extends Functions implements ScriptFile
{
	private static int EVENT_MANAGER_ID = 31255;
	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

	private static boolean _active = false;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: The Fall Harvest [state: activated]");
		}
		else
			_log.info("Loaded Event: The Fall Harvest [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("TheFallHarvest");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("TheFallHarvest", true))
		{
			spawnEventManagers();
			_log.info("Event 'The Fall Harvest' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TheFallHarvest.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'The Fall Harvest' already started.");

		_active = true;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Останавливает эвент
	*/
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive("TheFallHarvest", false))
		{
			unSpawnEventManagers();
			_log.info("Event 'The Fall Harvest' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TheFallHarvest.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'The Fall Harvest' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 81921, 148921, -3467, 16384 }, { 146405, 28360, -2269, 49648 },
				{ 19319, 144919, -3103, 31135 }, { -82805, 149890, -3129, 33202 }, { -12347, 122549, -3104, 32603 },
				{ 110642, 220165, -3655, 61898 }, { 116619, 75463, -2721, 20881 }, { 85513, 16014, -3668, 23681 },
				{ 81999, 53793, -1496, 61621 }, { 148159, -55484, -2734, 44315 }, { 44185, -48502, -797, 27479 },
				{ 86899, -143229, -1293, 22021 } };

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	public void onReload()
	{
		unSpawnEventManagers();
	}

	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	public static void OnDie(L2Character cha, L2Character killer)
	{
		if(_active && SimpleCheckDrop(cha, killer) && Rnd.get(1000) <= ConfigValue.TFH_POLLEN_CHANCE * killer.getPlayer().getRateItems() * RateService.getRateDropItems(killer.getPlayer()) * ((L2NpcInstance) cha).getTemplate().rateHp)
			((L2NpcInstance) cha).dropItem(killer.getPlayer(), 6391, 1);
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.TheFallHarvest.AnnounceEventStarted", null);
	}
}