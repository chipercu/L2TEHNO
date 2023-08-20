package events.March8;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
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
import l2open.util.Util;

/**
 * Эвент к 8 марта: http://www.lineage2.com/archive/2009/01/the_valentine_e.html
 * 
 * @author SYS
 */
public class March8 extends Functions implements ScriptFile
{
	private static final String EVENT_NAME = "March8";
	private static final int RECIPE_PRICE = 50000; // 50.000 adena at x1 servers
	private static final int RECIPE_ID = 20191;
	private static final int EVENT_MANAGER_ID = 4301;
	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();
	private static final int[] DROP = { 20192, 20193, 20194 };
	private static boolean _active = false;

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { -14823, 123567, -3143, 8192 }, // Gludio
				{ -83159, 150914, -3155, 49152 }, // Gludin
				{ 18600, 145971, -3095, 40960 }, // Dion
				{ 82158, 148609, -3493, 60 }, // Giran
				{ 110992, 218753, -3568, 0 }, // Hiene
				{ 116339, 75424, -2738, 0 }, // Hunter Village
				{ 81140, 55218, -1551, 32768 }, // Oren
				{ 147148, 27401, -2231, 2300 }, // Aden
				{ 43532, -46807, -823, 31471 }, // Rune
				{ 87765, -141947, -1367, 6500 }, // Schuttgart
				{ 147154, -55527, -2807, 61300 } // Goddard
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive(EVENT_NAME);
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			_log.info("Event: March 8 started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.March8.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'March 8' already started.");

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
		if(SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			_log.info("Event: March 8 stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.March8.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'March 8' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Продает рецепт игроку
	*/
	public void buyrecipe(String[] var)
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible(true))
			return;

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		int recipe_count = 1;
		try
		{
			recipe_count = Integer.valueOf(var[0]);
		}
		catch(Exception E)
		{}

		long need_adena = (long) (RECIPE_PRICE * ConfigValue.March8PriceRate * recipe_count);
		if(player.getAdena() < need_adena)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(need_adena, true);
		Functions.addItem(player, RECIPE_ID, recipe_count);
	}

	/**
	 * Добавляет в диалоги эвент менеджеров строчку с байпасом для покупки сундука
	 */
	private static int[] recipes_counts = { 1, 5, 10 }; //TODO в конфиг

	public String DialogAppend_4301(Integer val)
	{
		if(val != 0)
			return "";

		String price;
		String append = "";
		for(int cnt : recipes_counts)
		{
			price = Util.formatAdena((long) (RECIPE_PRICE * ConfigValue.March8PriceRate * cnt));
			append += "<br><a action=\"bypass -h scripts_events.March8.March8:buyrecipe " + cnt + "\">";
			if(cnt == 1)
				append += new CustomMessage("scripts.events.March8.buyrecipe", getSelf()).addString(price);
			else
				append += new CustomMessage("scripts.events.March8.buyrecipes", getSelf()).addNumber(cnt).addString(price);
			append += "</a><br>";
		}

		return append;
	}

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: March 8 [state: activated]");
		}
		else
			_log.info("Loaded Event: March 8 [state: deactivated]");
	}

	public void onReload()
	{
		unSpawnEventManagers();
	}

	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.March8.AnnounceEventStarted", null);
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	public static void OnDie(L2Character cha, L2Character killer)
	{
		if(_active && SimpleCheckDrop(cha, killer) && Rnd.get(1000) <= ConfigValue.March8DropChance * killer.getPlayer().getRateItems() * RateService.getRateDropItems(killer.getPlayer()) * ((L2NpcInstance) cha).getTemplate().rateHp)
			((L2NpcInstance) cha).dropItem(killer.getPlayer(), DROP[Rnd.get(DROP.length)], 1);
	}
}