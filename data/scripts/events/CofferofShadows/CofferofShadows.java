package events.CofferofShadows;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Util;

// Эвент Coffer of Shadows
public class CofferofShadows extends Functions implements ScriptFile
{
	private static int COFFER_PRICE = 50000; // 50.000 adena at x1 servers
	private static int COFFER_ID = 8659;
	private static int EVENT_MANAGER_ID = 32091;
	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

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
		return IsActive("CofferofShadows");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("CofferofShadows", true))
		{
			spawnEventManagers();
			_log.info("Event: Coffer of Shadows started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Coffer of Shadows' already started.");

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
		if(SetActive("CofferofShadows", false))
		{
			unSpawnEventManagers();
			_log.info("Event: Coffer of Shadows stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Coffer of Shadows' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Продает 1 сундук игроку
	*/
	public void buycoffer(String[] var)
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible(true))
			return;

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		int coffer_count = 1;
		try
		{
			coffer_count = Integer.valueOf(var[0]);
		}
		catch(Exception E)
		{}

		long need_adena = (long) (COFFER_PRICE * ConfigValue.CofferOfShadowsPriceRate * coffer_count);
		if(player.getAdena() < need_adena)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(need_adena, true);
		Functions.addItem(player, COFFER_ID, coffer_count);
	}

	/**
	 * Добавляет в диалоги эвент менеджеров строчку с байпасом для покупки сундука
	 */
	private static int[] buycoffer_counts = { 1, 5, 10, 50 }; //TODO в конфиг

	public String DialogAppend_32091(Integer val)
	{
		if(val != 0)
			return "";

		String price;
		String append = "";
		for(int cnt : buycoffer_counts)
		{
			price = Util.formatAdena((long) (COFFER_PRICE * ConfigValue.CofferOfShadowsPriceRate * cnt));
			append += "<a action=\"bypass -h scripts_events.CofferofShadows.CofferofShadows:buycoffer " + cnt + "\">";
			if(cnt == 1)
				append += new CustomMessage("scripts.events.CofferofShadows.buycoffer", getSelf()).addString(price);
			else
				append += new CustomMessage("scripts.events.CofferofShadows.buycoffers", getSelf()).addNumber(cnt).addString(price);
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
			_log.info("Loaded Event: Coffer of Shadows [state: activated]");
		}
		else
			_log.info("Loaded Event: Coffer of Shadows [state: deactivated]");
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
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.CofferofShadows.AnnounceEventStarted", null);
	}
}