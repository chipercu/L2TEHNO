package events.Christmas;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;

public class Christmas extends Functions implements ScriptFile
{
	private static int EVENT_MANAGER_ID = 31863;
	private static int CTREE_ID = 13006;

	private static int[][] _dropdata = {
	// Item, chance
			{ 5556, 20 }, //Star Ornament 2%
			{ 5557, 20 }, //Bead Ornament 2%
			{ 5558, 50 }, //Fir Tree Branch 5%
			{ 5559, 5 }, //Flower Pot 0.5%
	/*
	// Музыкальные кристаллы 0.2%
	{ 5562, 2 },
	{ 5563, 2 },
	{ 5564, 2 },
	{ 5565, 2 },
	{ 5566, 2 },
	{ 5583, 2 },
	{ 5584, 2 },
	{ 5585, 2 },
	{ 5586, 2 },
	{ 5587, 2 },
	{ 4411, 2 },
	{ 4412, 2 },
	{ 4413, 2 },
	{ 4414, 2 },
	{ 4415, 2 },
	{ 4416, 2 },
	{ 4417, 2 },
	{ 5010, 2 },
	{ 7061, 2 },
	{ 7062, 2 },
	{ 6903, 2 },
	{ 8555, 2 } 
	*/
	};

	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

	private static boolean _active = false;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Christmas [state: activated]");
		}
		else
			_log.info("Loaded Event: Christmas [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("Christmas");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("Christmas", true))
		{
			spawnEventManagers();
			_log.info("Event 'Christmas' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.Christmas.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Christmas' already started.");

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
		if(SetActive("Christmas", false))
		{
			unSpawnEventManagers();
			_log.info("Event 'Christmas' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.Christmas.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Christmas' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Спавнит эвент менеджеров и рядом ёлки
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 81921, 148921, -3467, 16384 }, { 146405, 28360, -2269, 49648 },
				{ 19319, 144919, -3103, 31135 }, { -82805, 149890, -3129, 16384 }, { -12347, 122549, -3104, 16384 },
				{ 110642, 220165, -3655, 61898 }, { 116619, 75463, -2721, 20881 }, { 85513, 16014, -3668, 23681 },
				{ 81999, 53793, -1496, 61621 }, { 148159, -55484, -2734, 44315 }, { 44185, -48502, -797, 27479 },
				{ 86899, -143229, -1293, 8192 } };

		final int CTREES[][] = { { 81961, 148921, -3467, 0 }, { 146445, 28360, -2269, 0 }, { 19319, 144959, -3103, 0 },
				{ -82845, 149890, -3129, 0 }, { -12387, 122549, -3104, 0 }, { 110602, 220165, -3655, 0 },
				{ 116659, 75463, -2721, 0 }, { 85553, 16014, -3668, 0 }, { 81999, 53743, -1496, 0 },
				{ 148199, -55484, -2734, 0 }, { 44185, -48542, -797, 0 }, { 86859, -143229, -1293, 0 } };

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
		SpawnNPCs(CTREE_ID, CTREES, _spawns);
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
		if(_active && SimpleCheckDrop(cha, killer))
		{
			int dropCounter = 0;
			for(int[] drop : _dropdata)
				if(Rnd.chance(drop[1] * killer.getPlayer().getRateItems() * RateService.getRateDropItems(killer.getPlayer()) * 0.1))
				{
					dropCounter++;
					((L2NpcInstance) cha).dropItem(killer.getPlayer(), drop[0], 1);

					// Из одного моба выпадет не более 3-х эвентовых предметов
					if(dropCounter > 2)
						break;
				}
		}
	}

	public void exchange(String[] var)
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible(true))
			return;

		if(player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(var[0].equalsIgnoreCase("0"))
		{
			if(getItemCount(player, 5556) >= 4 && getItemCount(player, 5557) >= 4 && getItemCount(player, 5558) >= 10 && getItemCount(player, 5559) >= 1)
			{
				removeItem(player, 5556, 4);
				removeItem(player, 5557, 4);
				removeItem(player, 5558, 10);
				removeItem(player, 5559, 1);
				addItem(player, 5560, 1); // Christmas Tree
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(var[0].equalsIgnoreCase("1"))
		{
			if(getItemCount(player, 5560) >= 10)
			{
				removeItem(player, 5560, 10);
				addItem(player, 5561, 1); // Special Christmas Tree
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(var[0].equalsIgnoreCase("2"))
		{
			if(getItemCount(player, 5560) >= 10)
			{
				removeItem(player, 5560, 10);
				addItem(player, 7836, 1); // Santa's Hat
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(var[0].equalsIgnoreCase("3"))
		{
			if(getItemCount(player, 5560) >= 10)
			{
				removeItem(player, 5560, 10);
				addItem(player, 8936, 1); // Santa's Antlers
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(var[0].equalsIgnoreCase("4"))
		{
			if(getItemCount(player, 5560) >= 20)
			{
				removeItem(player, 5560, 20);
				addItem(player, 10606, 1); // Agathion Seal Bracelet - Rudolph
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Christmas.AnnounceEventStarted", null);
	}
}