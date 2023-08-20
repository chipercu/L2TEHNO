package events.RabbitsToRiches;

import java.io.File;
import java.util.Calendar;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * @author Diagod
 **/
public class RabbitsToRiches extends Functions implements ScriptFile
{
	private static int EVENT_MANAGER = 32365; // Snow
	private static int event_search_thing = 13097;

	private static int TREASURE_SACK_PIECE = 10272;

	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();
	private static boolean _active = false;

	/**
	 * Спавнит эвент менеджера
	 */
	private void spawnEventManagers()
	{
		int EVENT_MANAGERS1[][] = 
		{
				{-59189,-56896,-2032,0},
				{-59959,-57340,-2039,-21680},
				{-59960,-56426,-2039,21416},
				{-12992,122818,-3117,0},
				{-13964,121947,-2988,32768},
				{-14823,123752,-3117,8192},
				{-80762,151118,-3043,28672},
				{-84046,150193,-3129,4096},
				{-82675,151652,-3129,46943},
				{18178,145149,-3054,7400},
				{19185,144377,-3097,32768},
				{19508,145753,-3086,47999},
				{83358,149223,-3400,32768}, // gatekeeper
				{82277,148598,-3467,0},
				{81621,148725,-3467,32768},
				{81680,145656,-3533,32768},
				{79806,55570,-1560,0},
				{83328,55824,-1525,32768},
				{80986,54504,-1525,32768},
				{117498,76630,-2695,38000},
				{115933,76482,-2711,58999},
				{119536,76988,-2275,40960},
				{147120,27312,-2192,40960},
				{147959,25695,-2000,16384},
				{111585,221011,-3544,16384},  // gatekeeper
				{107922,218094,-3675,0},
				{114920,220020,-3632,32768},
				{47151,49436,-3059,32000},
				{44122,50784,-3059,57344},
				{147888,-58048,-2979,49000},
				{147285,-56461,-2776,11500},
				{44150,-48708,-800,32999},
				{44280,-47664,-792,49167},
				{87792,-142240,-1343,44000},
				{87557,-140657,-1542,20476},
				{-84566,242972,-3730,33999},
				{-85938,243228,-3729,59999},
				{11286,15675,-4584,29712},
				{11400,17767,-4574,63847},
				{17396,170259,-3507,30000},
				{-44324,-113720,-224,-1952},
				{-44662,-115368,-240,22500},
				{114746,-178730,-821,0},
				{115697,-182324,-1449,0},
				{-119193,47000,367,49151},
				{-117440,45059,360,16384}
		};

		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[0], "isThemePark", "0"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[1], "isThemePark", "0"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[2], "isThemePark", "0"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[3], "isThemePark", "1"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[4], "isThemePark", "1"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[5], "isThemePark", "1"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[6], "isThemePark", "2"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[7], "isThemePark", "2"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[8], "isThemePark", "2"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[9], "isThemePark", "3"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[10], "isThemePark", "3"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[11], "isThemePark", "3"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[12], "isThemePark", "4"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[13], "isThemePark", "4"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[14], "isThemePark", "4"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[15], "isThemePark", "4"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[16], "isThemePark", "5"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[17], "isThemePark", "5"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[18], "isThemePark", "5"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[19], "isThemePark", "6"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[20], "isThemePark", "6"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[21], "isThemePark", "6"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[22], "isThemePark", "7"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[23], "isThemePark", "7"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[24], "isThemePark", "8"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[25], "isThemePark", "8"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[26], "isThemePark", "8"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[27], "isThemePark", "9"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[28], "isThemePark", "9"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[29], "isThemePark", "10"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[30], "isThemePark", "10"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[31], "isThemePark", "11"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[32], "isThemePark", "11"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[33], "isThemePark", "12"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[34], "isThemePark", "12"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[35], "isThemePark", "13"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[36], "isThemePark", "13"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[37], "isThemePark", "14"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[38], "isThemePark", "14"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[39], "isThemePark", "15"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[40], "isThemePark", "16"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[41], "isThemePark", "16"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[42], "isThemePark", "17"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[43], "isThemePark", "17"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[44], "isThemePark", "18"));
		_spawns.add(spawnParam(EVENT_MANAGER, EVENT_MANAGERS1[45], "isThemePark", "18"));

		SpawnNPCs(event_search_thing, 9172, "", 30, 400, _spawns);
		SpawnNPCs(event_search_thing, 9173, "", 30, 400, _spawns);
		SpawnNPCs(event_search_thing, 9174, "", 30, 1000, _spawns);
		SpawnNPCs(event_search_thing, 9175, "", 30, 1000, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджера
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	/**
	 * Читает статус эвента из базы.
	 * 
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("RabbitsToRiches");
	}

	/**
	 * Start Event
	 */
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("RabbitsToRiches", true))
		{
			spawnEventManagers();
			_log.info("Event: 'L2 Rabbits To Riches Event' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.RabbitsToRiches.RabbitsToRiches.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event: 'L2 Rabbits To Riches Event' already started.");

		_active = true;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Stop Event
	 */
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("RabbitsToRiches", false))
		{
			unSpawnEventManagers();
			_log.info("Event: 'Rabbits To Riches Event' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.RabbitsToRiches.RabbitsToRiches.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event: 'L2 Rabbits To Riches Event' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: L2 Rabbits To Riches [state: activated]");
			if(ConfigValue.TREASURE_SACK_CHANCE > 0.8)
				_log.info("Event L2 Rabbits To Riches: << W A R N I N G >> RATES IS TO HIGH!!!");

			final Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 22);
			c.set(Calendar.MINUTE, 00);
			c.set(Calendar.SECOND, 00);
			c.set(Calendar.MILLISECOND, 00);
			long time_to_reload = c.getTimeInMillis()-System.currentTimeMillis();
			if(time_to_reload > 3600000)
				ThreadPoolManager.getInstance().schedule(new Runnable()
				{
					public void run()
					{
						unSpawnEventManagers();
						spawnEventManagers();
					}
				}, time_to_reload);
		}
		else
			_log.info("Loaded Event: L2 Rabbits To Riches Event [state: deactivated]");
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
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.RabbitsToRiches.RabbitsToRiches.AnnounceEventStarted", null);
	}
}