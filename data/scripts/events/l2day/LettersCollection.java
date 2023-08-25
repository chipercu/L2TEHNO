package events.l2day;

import java.util.HashMap;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Rnd;

public abstract class LettersCollection extends Functions implements ScriptFile
{
	// Переменные, определять
	protected static boolean _active;
	protected static String _name;
	protected static int[][] letters;
	protected static int EVENT_MANAGERS[][] = null;
	protected static String _msgStarted;
	protected static String _msgEnded;

	// Буквы, статика
	protected static int A = 3875;
	protected static int C = 3876;
	protected static int E = 3877;
	protected static int F = 3878;
	protected static int G = 3879;
	protected static int H = 3880;
	protected static int I = 3881;
	protected static int L = 3882;
	protected static int N = 3883;
	protected static int O = 3884;
	protected static int R = 3885;
	protected static int S = 3886;
	protected static int T = 3887;
	protected static int II = 3888;
	protected static int Y = 13417;
	protected static int _5 = 13418;

	protected static int EVENT_MANAGER_ID = 31230;

	// Контейнеры, не трогать
	protected static HashMap<String, Integer[][]> _words = new HashMap<String, Integer[][]>();
	protected static HashMap<String, L2DropData[]> _rewards = new HashMap<String, L2DropData[]>();
	protected static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: " + _name + " [state: activated]");
		}
		else
			_log.info("Loaded Event: " + _name + " [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 */
	protected static boolean isActive()
	{
		return IsActive(_name);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	protected void spawnEventManagers()
	{
		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	protected void unSpawnEventManagers()
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
			int[] letter = letters[Rnd.get(letters.length)];
			if(Rnd.chance(letter[1] * ConfigValue.L2DAY_LETTER_CHANCE * ((L2NpcTemplate) cha.getTemplate()).rateHp))
				((L2NpcInstance) cha).dropItem(killer.getPlayer(), letter[0], 1);
		}
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive(_name, true))
		{
			spawnEventManagers();
			_log.info("Event '" + _name + "' started.");
			Announcements.getInstance().announceByCustomMessage(_msgStarted, null);
		}
		else
			player.sendMessage("Event '" + _name + "' already started.");

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
		if(SetActive(_name, false))
		{
			unSpawnEventManagers();
			_log.info("Event '" + _name + "' stopped.");
			Announcements.getInstance().announceByCustomMessage(_msgEnded, null);
		}
		else
			player.sendMessage("Event '" + _name + "' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Обмен эвентовых вещей, где var[0] - слово.
	*/
	public void exchange(String[] var)
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible(true))
			return;

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		Integer[][] mss = _words.get(var[0]);

		for(Integer[] l : mss)
			if(getItemCount(player, l[0]) < l[1])
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}

		for(Integer[] l : mss)
			removeItem(player, l[0], l[1]);

		L2DropData[] rewards = _rewards.get(var[0]);
		int sum = 0;
		for(L2DropData r : rewards)
			sum += r.getChance();
		int random = Rnd.get(sum);
		sum = 0;
		for(L2DropData r : rewards)
		{
			sum += r.getChance();
			if(sum > random)
			{
				addItem(player, r.getItemId(), Rnd.get(r.getMinDrop(), r.getMaxDrop()));
				return;
			}
		}
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, _msgStarted, null);
	}

	public String DialogAppend_31230(Integer val)
	{
		if(!_active)
			return "";

		StringBuilder append = new StringBuilder("<br><br>");
		for(String word : _words.keySet())
			append.append("[scripts_").append(getClass().getName()).append(":exchange ").append(word).append("|").append(word).append("]<br1>");

		return append.toString();
	}
}