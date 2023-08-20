package events.TrickOfTrans;

import l2p.Config;
import l2p.extensions.scripts.Functions;
import l2p.extensions.scripts.ScriptFile;
import l2p.gameserver.Announcements;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Spawn;
import l2p.util.Files;
import l2p.util.GArray;
import l2p.util.Rnd;

/**
 * Development by L2Phoenix
 * Trick Of Transmutation Event
 */
public class TrickOfTrans extends Functions implements ScriptFile
{

	// Эвент Менеджеры
	private static final int EVENT_MANAGER_ID = 32132; // Alchemist\'s Servitor
	private static final int CHESTS_ID = 13036; // Alchemist\'s Chest

	// Рецепты
	private static final int RED_PSTC = 9162; // Red Philosopher''s Stone Transmutation Circle
	private static final int BLUE_PSTC = 9163; // Blue Philosopher''s Stone Transmutation Circle
	private static final int ORANGE_PSTC = 9164; // Orange Philosopher''s Stone Transmutation Circle
	private static final int BLACK_PSTC = 9165; // Black Philosopher''s Stone Transmutation Circle
	private static final int WHITE_PSTC = 9166; // White Philosopher''s Stone Transmutation Circle
	private static final int GREEN_PSTC = 9167; // Green Philosopher''s Stone Transmutation Circle

	// Награды
	private static final int RED_PSTC_R = 9171; // Red Philosopher''s Stone
	private static final int BLUE_PSTC_R = 9172; // Blue Philosopher''s Stone
	private static final int ORANGE_PSTC_R = 9173; // Orange Philosopher''s Stone
	private static final int BLACK_PSTC_R = 9174; // Black Philosopher''s Stone
	private static final int WHITE_PSTC_R = 9175; // White Philosopher''s Stone
	private static final int GREEN_PSTC_R = 9176; // Green Philosopher''s Stone

	// Ключ
	private static final int A_CHEST_KEY = 9205; // Alchemist''s Chest Key

	// Ингридиенты
	private static final int PhilosophersStoneOre = 9168; // Philosopher''s Stone Ore
	private static final int PhilosophersStoneOreMax = 17; // Максимальное Кол-во
	private static final int PhilosophersStoneConversionFormula = 9169; // Philosopher''s Stone Conversion Formula
	private static final int MagicReagents = 9170; // Magic Reagents
	private static final int MagicReagentsMax = 30; // Максимальное Кол-во
	
	private static GArray<L2Spawn> _em_spawns = new GArray<L2Spawn>();
	private static GArray<L2Spawn> _ch_spawns = new GArray<L2Spawn>();
	private static boolean _active = false;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			System.out.println("Loaded Event: Trick of Trnasmutation [state: activated]");
		}
		else
			System.out.println("Loaded Event: Trick of Trnasmutation [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 */
	private static boolean isActive()
	{
		return IsActive("trickoftrans");
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive("trickoftrans", true))
		{
			spawnEventManagers();
			System.out.println("Event: 'Trick of Transmutation' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event: 'Trick of Transmutation' already started.");

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
		if(SetActive("trickoftrans", false))
		{
			unSpawnEventManagers();
			System.out.println("Event: 'Trick of Transmutation' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event: 'Trick of Transmutation' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Анонсируется при заходе игроком в мир
	 */
	public static void OnPlayerEnter(final L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.TrickOfTrans.AnnounceEventStarted", null);
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
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		// Эвент Менеджер
		final int EVENT_MANAGERS[][] = { { 147992, 28616, -2295, 0 }, // Aden
			{ 81919, 148290, -3472, 51432 }, // Giran
			{ 18293, 145208, -3081, 6470 }, // Dion
			{ -14694, 122699, -3122, 0 }, // Gludio
			{ -81634, 150275, -3155, 15863 } // Gludin
		};

		// Сундуки
		final int CHESTS[][] = { {148081, 28614, -2274, 2059 }, // Aden
			{ 147918, 28615, -2295, 31471 }, // Aden
			{ 147998, 28534, -2274, 49152 }, // Aden
			{ 148053, 28550, -2274, 55621 }, // Aden
			{ 147945, 28563, -2274, 40159 }, // Aden
			{ 82012, 148286, -3472, 61567 }, // Giran
			{ 81822, 148287, -3493, 29413 }, // Giran
			{ 81917, 148207, -3493, 49152 }, // Giran
			{ 81978, 148228, -3472, 53988 }, // Giran
			{ 81851, 148238, -3472, 40960 }, // Giran
			{ 18343, 145253, -3096, 7449 }, // Dion
			{ 18284, 145274, -3090, 19740 }, // Dion
			{ 18351, 145186, -3089, 61312 }, // Dion
			{ 18228, 145265, -3079, 21674 }, // Dion
			{ 18317, 145140, -3078, 55285 }, // Dion
			{ -14584, 122694, -3122, 65082 }, // Gludio
			{ -14610, 122756, -3143, 13029 }, // Gludio
			{ -14628, 122627, -3122, 50632 }, // Gludio
			{ -14697, 122607, -3143, 48408 }, // Gludio
			{ -14686, 122787, -3122, 12416 }, // Gludio
			{ -81745, 150275, -3134, 32768 }, // Gludin
			{ -81520, 150275, -3134, 0 }, // Gludin
			{ -81628, 150379, -3134, 16025 }, // Gludin
			{ -81696, 150347, -3155, 22854 }, // Gludin
			{ -81559, 150332, -3134, 3356 }, // Gludin
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _em_spawns);
		SpawnNPCs(CHESTS_ID, CHESTS, _ch_spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_em_spawns);
		deSpawnNPCs(_ch_spawns);
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	public static void OnDie(final L2Character cha, final L2Character killer)
	{
		if(_active && SimpleCheckDrop(cha, killer) && Rnd.get(1000) <= ConfigValue.TRICK_OF_TRANS_CHANCE * killer.getPlayer().getRateItems() * Config.RATE_DROP_ITEMS * ((L2NpcInstance) cha).getTemplate().rateHp)
			((L2NpcInstance) cha).dropItem(killer.getPlayer(), A_CHEST_KEY, 1);
	}

	public void accept()
	{
		L2Player player = (L2Player) getSelf();

		if(!player.isQuestContinuationPossible())
			return;

		if(!player.findRecipe(RED_PSTC_R))
			addItem(player, RED_PSTC, 1);
		if(!player.findRecipe(BLACK_PSTC_R))
			addItem(player, BLACK_PSTC, 1);
		if(!player.findRecipe(BLUE_PSTC_R))
			addItem(player, BLUE_PSTC, 1);
		if(!player.findRecipe(GREEN_PSTC_R))
			addItem(player, GREEN_PSTC, 1);
		if(!player.findRecipe(ORANGE_PSTC_R))
			addItem(player, ORANGE_PSTC, 1);
		if(!player.findRecipe(WHITE_PSTC_R))
			addItem(player, WHITE_PSTC, 1);

		if(player.getVar("lang@") == null || player.getVar("lang@").equals("en"))
			show(Files.read("data/scripts/events/TrickOfTrans/TrickOfTrans_01.htm", player), player);
		else
			show(Files.read("data/scripts/events/TrickOfTrans/TrickOfTrans_01_ru.htm", player), player);
	}

	public void open()
	{
		L2Player player = (L2Player) getSelf();

		if(getItemCount(player, A_CHEST_KEY) > 0)
		{
			removeItem(player, A_CHEST_KEY, 1);
			addItem(player, PhilosophersStoneOre, Rnd.get(1, PhilosophersStoneOreMax));
			addItem(player, MagicReagents, Rnd.get(1, MagicReagentsMax));
			if(Rnd.chance(80))
				addItem(player, PhilosophersStoneConversionFormula, 1);

			if(player.getVar("lang@") == null || player.getVar("lang@").equals("en"))
				show(Files.read("data/scripts/events/TrickOfTrans/TrickOfTrans_02.htm", player), player);
			else
				show(Files.read("data/scripts/events/TrickOfTrans/TrickOfTrans_02_ru.htm", player), player);
		}
		else if(player.getVar("lang@") == null || player.getVar("lang@").equals("en"))
			show(Files.read("data/scripts/events/TrickOfTrans/TrickOfTrans_03.htm", player), player);
		else
			show(Files.read("data/scripts/events/TrickOfTrans/TrickOfTrans_03_ru.htm", player), player);
	}
}