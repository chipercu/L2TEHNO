package events.heart;

import java.util.HashMap;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.PrintfFormat;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * Event Change of Heart
 * @author Drin
 * http://www.lineage2.com/archive/2007/02/change_of_heart.html
 */

public class heart extends Functions implements ScriptFile
{
	private static boolean _active = false;
	private static final GArray<L2Spawn> _spawns = new GArray<L2Spawn>();
	private static final HashMap<Integer, Integer> Guesses = new HashMap<Integer, Integer>();
	private static String links_en = "", links_ru = "";
	private static final String[][] variants = { { "Rock", "Камень" }, { "Scissors", "Ножницы" }, { "Paper", "Бумага" } };
	static
	{
		PrintfFormat fmt = new PrintfFormat("<br><a action=\"bypass -h scripts_events.heart.heart:play %d\">\"%s!\"</a>");
		for(int i = 0; i < variants.length; i++)
		{
			links_en += fmt.sprintf(new Object[] { i, variants[i][0] });
			links_ru += fmt.sprintf(new Object[] { i, variants[i][1] });
		}
	}

	private static final int EVENT_MANAGER_ID = 31227; //Buzz the Cat
	private static final int[] hearts = { 4209, 4210, 4211, 4212, 4213, 4214, 4215, 4216, 4217 };
	private static final int[] potions = { 1374, // Greater Haste Potion
			1375, // Greater Swift Attack Potion
			6036, // Greater Magic Haste Potion
			1539 // Greater Healing Potion
	};
	private static final int[] scrolls = { 3926, //	L2Day - Scroll of Guidance
			3927, //	L2Day - Scroll of Death Whisper
			3928, //	L2Day - Scroll of Focus
			3929, //	L2Day - Scroll of Greater Acumen
			3930, //	L2Day - Scroll of Haste
			3931, //	L2Day - Scroll of Agility
			3932, //	L2Day - Scroll of Mystic Empower
			3933, //	L2Day - Scroll of Might
			3934, //	L2Day - Scroll of Windwalk
			3935 //	L2Day - Scroll of Shield
	};

	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("heart", true))
		{
			spawnEventManagers();
			_log.info("Event 'Change of Heart' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Change of Heart' already started.");

		_active = true;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive("heart", false))
		{
			unSpawnEventManagers();
			_log.info("Event 'Change of Heart' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Change of Heart' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void letsplay()
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible(true))
			return;

		zeroGuesses(player);
		if(haveAllHearts(player))
			show(link(Files.read("data/scripts/events/heart/hearts_01.htm", player), isRus(player)), player);
		else
			show(Files.read("data/scripts/events/heart/hearts_00.htm", player), player);
	}

	public void play(String[] var)
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible(true) || var.length == 0)
			return;

		if(!haveAllHearts(player))
		{
			if(var[0].equalsIgnoreCase("Quit"))
				show(Files.read("data/scripts/events/heart/hearts_00b.htm", player), player);
			else
				show(Files.read("data/scripts/events/heart/hearts_00a.htm", player), player);
			return;
		}

		if(var[0].equalsIgnoreCase("Quit"))
		{
			int curr_guesses = getGuesses(player);
			takeHeartsSet(player);
			reward(player, curr_guesses);
			show(Files.read("data/scripts/events/heart/hearts_reward_" + curr_guesses + ".htm", player), player);
			zeroGuesses(player);
			return;
		}

		int var_cat = Rnd.get(variants.length);
		int var_player = 0;
		try
		{
			var_player = Integer.parseInt(var[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}

		if(var_player == var_cat)
		{
			show(fillvars(Files.read("data/scripts/events/heart/hearts_same.htm", player), var_player, var_cat, player), player);
			return;
		}

		if(playerWins(var_player, var_cat))
		{
			incGuesses(player);
			int curr_guesses = getGuesses(player);
			if(curr_guesses == 10)
			{
				takeHeartsSet(player);
				reward(player, curr_guesses);
				zeroGuesses(player);
			}
			show(fillvars(Files.read("data/scripts/events/heart/hearts_level_" + curr_guesses + ".htm", player), var_player, var_cat, player), player);
			return;
		}

		takeHeartsSet(player);
		reward(player, getGuesses(player) - 1);
		show(fillvars(Files.read("data/scripts/events/heart/hearts_loose.htm", player), var_player, var_cat, player), player);
		zeroGuesses(player);
	}

	private void reward(L2Player player, int guesses)
	{
		switch(guesses)
		{
			case -1:
			case 0:
				addItem(player, scrolls[Rnd.get(scrolls.length)], 1);
				break;
			case 1:
				addItem(player, potions[Rnd.get(potions.length)], 10);
				break;
			case 2:
				addItem(player, 1538, 1); // 1  Blessed Scroll of Escape
				break;
			case 3:
				addItem(player, 3936, 1); // 1  Blessed Scroll of Resurrection
				break;
			case 4:
				addItem(player, 951, 2); // 2  Scroll: Enchant Weapon (C)
				break;
			case 5:
				addItem(player, 948, 4); // 4  Scroll: Enchant Armor (B)
				break;
			case 6:
				addItem(player, 947, 1); // 1  Scroll: Enchant Weapon (B)
				break;
			case 7:
				addItem(player, 730, 3); // 3  Scroll: Enchant Armor (A)
				break;
			case 8:
				addItem(player, 729, 1); // 1  Scroll: Enchant Weapon (A)
				break;
			case 9:
				addItem(player, 960, 2); // 2  Scroll: Enchant Armor (S)
				break;
			case 10:
				addItem(player, 959, 1); // 1  Scroll: Enchant Weapon (S)
				break;
		}
	}

	private String fillvars(String s, int var_player, int var_cat, L2Player player)
	{
		boolean rus = isRus(player);
		return link(s.replaceFirst("Player", player.getName()).replaceFirst("%var_payer%", variants[var_player][rus ? 1 : 0]).replaceFirst("%var_cat%", variants[var_cat][rus ? 1 : 0]), rus);
	}

	private boolean isRus(L2Player player)
	{
		return player.isLangRus();
	}

	private String link(String s, boolean rus)
	{
		return s.replaceFirst("%links%", rus ? links_ru : links_en);
	}

	private boolean playerWins(int var_player, int var_cat)
	{
		if(var_player == 0) // Rock vs Scissors
			return var_cat == 1;
		if(var_player == 1) // Scissors vs Paper
			return var_cat == 2;
		if(var_player == 2) // Paper vs Rock
			return var_cat == 0;
		return false;
	}

	private int getGuesses(L2Player player)
	{
		return Guesses.containsKey(player.getObjectId()) ? Guesses.get(player.getObjectId()) : 0;
	}

	private void incGuesses(L2Player player)
	{
		int val = 1;
		if(Guesses.containsKey(player.getObjectId()))
			val = Guesses.remove(player.getObjectId()) + 1;
		Guesses.put(player.getObjectId(), val);
	}

	private void zeroGuesses(L2Player player)
	{
		if(Guesses.containsKey(player.getObjectId()))
			Guesses.remove(player.getObjectId());
	}

	private void takeHeartsSet(L2Player player)
	{
		for(int heart_id : hearts)
			removeItem(player, heart_id, 1);
	}

	private boolean haveAllHearts(L2Player player)
	{
		for(int heart_id : hearts)
			if(player.getInventory().getCountOf(heart_id) < 1)
				return false;
		return true;
	}

	public static void OnDie(L2Character cha, L2Character killer)
	{
		if(_active && SimpleCheckDrop(cha, killer))
			((L2NpcInstance) cha).dropItem(killer.getPlayer(), hearts[Rnd.get(hearts.length)], Util.rollDrop(1, 1, ConfigValue.EVENT_CHANGE_OF_HEART_CHANCE * killer.getPlayer().getRateItems() * ((L2MonsterInstance) cha).getTemplate().rateHp * 10000L, true, killer.getPlayer()));
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.ChangeofHeart.AnnounceEventStarted", null);
	}

	private static boolean isActive()
	{
		return IsActive("heart");
	}

	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 146936, 26654, -2208, 16384 }, // Aden
				{ 82168, 148842, -3464, 7806 }, // Giran
				{ 82204, 53259, -1488, 16384 }, // Oren
				{ 18924, 145782, -3088, 44034 }, // Dion
				{ 111794, 218967, -3536, 20780 }, // Heine
				{ -14539, 124066, -3112, 50874 }, // Gludio
				{ 147271, -55573, -2736, 60304 }, // Goddard
				{ 87801, -143150, -1296, 28800 }, // Shuttgard
				{ -80684, 149458, -3040, 16384 }, // Gludin
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Change of Heart [state: activated]");
		}
		else
			_log.info("Loaded Event: Change of Heart[state: deactivated]");
	}

	public void onReload()
	{
		unSpawnEventManagers();
	}

	public void onShutdown()
	{
		unSpawnEventManagers();
	}
}