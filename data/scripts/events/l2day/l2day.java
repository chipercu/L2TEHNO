package events.l2day;

import java.util.HashMap;
import java.util.Map.Entry;

import l2open.gameserver.model.L2DropData;

@SuppressWarnings("unused")
public class l2day extends LettersCollection
{
	// Награды
	private static int BSOE = 3958;
	private static int BSOR = 3959;
	private static int GUIDANCE = 3926;
	private static int WHISPER = 3927;
	private static int FOCUS = 3928;
	private static int ACUMEN = 3929;
	private static int HASTE = 3930;
	private static int AGILITY = 3931;
	private static int EMPOWER = 3932;
	private static int MIGHT = 3933;
	private static int WINDWALK = 3934;
	private static int SHIELD = 3935;

	private static int ENCH_WPN_D = 955;
	private static int ENCH_WPN_C = 951;
	private static int ENCH_WPN_B = 947;
	private static int ENCH_WPN_A = 729;

	private static int RABBIT_EARS = 8947;
	private static int FEATHERED_HAT = 8950;
	private static int FAIRY_ANTENNAE = 8949;
	private static int ARTISANS_GOOGLES = 8951;
	private static int LITTLE_ANGEL_WING = 8948;

	private static int RING_OF_ANT_QUIEEN = 6660;
	private static int EARRING_OF_ORFEN = 6661;
	private static int RING_OF_CORE = 6662;
	private static int FRINTEZZA_NECKLACE = 8191;

	static
	{
		_name = "l2day";
		_msgStarted = "scripts.events.l2day.AnnounceEventStarted";
		_msgEnded = "scripts.events.l2day.AnnounceEventStoped";

		EVENT_MANAGERS = new int[][] { { 19541, 145419, -3103, 30419 }, { 147485, -59049, -2980, 9138 },
				{ 109947, 218176, -3543, 63079 }, { -81363, 151611, -3121, 42910 }, { 144741, 28846, -2453, 2059 },
				{ 44192, -48481, -796, 23331 }, { -13889, 122999, -3109, 40099 }, { 116278, 75498, -2713, 12022 },
				{ 82029, 55936, -1519, 58708 }, { 147142, 28555, -2261, 59402 }, { 82153, 148390, -3466, 57344 }, };

		_words.put("LineageII", new Integer[][] { { L, 1 }, { I, 1 }, { N, 1 }, { E, 2 }, { A, 1 }, { G, 1 }, { II, 1 } });
		_rewards.put("LineageII", new L2DropData[] {
				// L2Day Scrolls
				new L2DropData(GUIDANCE, 3, 3, 85000), new L2DropData(WHISPER, 3, 3, 85000),
				new L2DropData(FOCUS, 3, 3, 85000), new L2DropData(ACUMEN, 3, 3, 85000), new L2DropData(HASTE, 3, 3, 85000),
				new L2DropData(AGILITY, 3, 3, 85000),
				new L2DropData(EMPOWER, 3, 3, 85000),
				new L2DropData(MIGHT, 3, 3, 85000),
				new L2DropData(WINDWALK, 3, 3, 85000),
				new L2DropData(SHIELD, 3, 3, 85000),
				// Other
				new L2DropData(BSOE, 1, 1, 50000), new L2DropData(BSOR, 1, 1, 50000), new L2DropData(ENCH_WPN_C, 3, 3, 14000),
				new L2DropData(ENCH_WPN_B, 2, 2, 7000), new L2DropData(ENCH_WPN_A, 1, 1, 7000),
				new L2DropData(RABBIT_EARS, 1, 1, 5000), new L2DropData(FEATHERED_HAT, 1, 1, 5000),
				new L2DropData(FAIRY_ANTENNAE, 1, 1, 5000), new L2DropData(RING_OF_ANT_QUIEEN, 1, 1, 100),
				new L2DropData(RING_OF_CORE, 1, 1, 100), });

		_words.put("Throne", new Integer[][] { { T, 1 }, { H, 1 }, { R, 1 }, { O, 1 }, { N, 1 }, { E, 1 } });
		_rewards.put("Throne", new L2DropData[] {
				// L2Day Scrolls
				new L2DropData(GUIDANCE, 3, 3, 85000), new L2DropData(WHISPER, 3, 3, 85000),
				new L2DropData(FOCUS, 3, 3, 85000), new L2DropData(ACUMEN, 3, 3, 85000), new L2DropData(HASTE, 3, 3, 85000),
				new L2DropData(AGILITY, 3, 3, 85000), new L2DropData(EMPOWER, 3, 3, 85000),
				new L2DropData(MIGHT, 3, 3, 85000),
				new L2DropData(WINDWALK, 3, 3, 85000),
				new L2DropData(SHIELD, 3, 3, 85000),
				// Other
				new L2DropData(BSOE, 1, 1, 50000), new L2DropData(BSOR, 1, 1, 50000), new L2DropData(ENCH_WPN_D, 4, 4, 16000),
				new L2DropData(ENCH_WPN_C, 3, 3, 11000), new L2DropData(ENCH_WPN_B, 2, 2, 6000),
				new L2DropData(ARTISANS_GOOGLES, 1, 1, 6000), new L2DropData(LITTLE_ANGEL_WING, 1, 1, 5000),
				new L2DropData(RING_OF_ANT_QUIEEN, 1, 1, 100), new L2DropData(RING_OF_CORE, 1, 1, 100), });

		_words.put("NCSoft", new Integer[][] { { N, 1 }, { C, 1 }, { S, 1 }, { O, 1 }, { F, 1 }, { T, 1 } });
		_rewards.put("NCSoft", new L2DropData[] {
				// L2Day Scrolls
				new L2DropData(GUIDANCE, 3, 3, 85000), new L2DropData(WHISPER, 3, 3, 85000),
				new L2DropData(FOCUS, 3, 3, 85000), new L2DropData(ACUMEN, 3, 3, 85000), new L2DropData(HASTE, 3, 3, 85000),
				new L2DropData(AGILITY, 3, 3, 85000), new L2DropData(EMPOWER, 3, 3, 85000),
				new L2DropData(MIGHT, 3, 3, 85000),
				new L2DropData(WINDWALK, 3, 3, 85000),
				new L2DropData(SHIELD, 3, 3, 85000),
				// Other
				new L2DropData(BSOE, 1, 1, 50000), new L2DropData(BSOR, 1, 1, 50000), new L2DropData(ENCH_WPN_D, 4, 4, 16000),
				new L2DropData(ENCH_WPN_C, 3, 3, 11000), new L2DropData(ENCH_WPN_B, 2, 2, 6000),
				new L2DropData(ARTISANS_GOOGLES, 1, 1, 6000), new L2DropData(LITTLE_ANGEL_WING, 1, 1, 5000),
				new L2DropData(RING_OF_ANT_QUIEEN, 1, 1, 100), new L2DropData(RING_OF_CORE, 1, 1, 100), });

		/*
		_words.put("Asterios", new Integer[][] { { A, 1 }, { S, 2 }, { T, 1 }, { E, 1 }, { R, 1 }, { I, 1 }, { O, 1 } });
		_rewards.put("Asterios", new L2DropData[] {
		// L2Day Scrolls
				new L2DropData(GUIDANCE, 3, 3, 85000),
				new L2DropData(WHISPER, 3, 3, 85000),
				new L2DropData(FOCUS, 3, 3, 85000),
				new L2DropData(ACUMEN, 3, 3, 85000),
				new L2DropData(HASTE, 3, 3, 85000),
				new L2DropData(AGILITY, 3, 3, 85000),
				new L2DropData(EMPOWER, 3, 3, 85000),
				new L2DropData(MIGHT, 3, 3, 85000),
				new L2DropData(WINDWALK, 3, 3, 85000),
				new L2DropData(SHIELD, 3, 3, 85000),
				// Other
				new L2DropData(BSOE, 1, 1, 50000),
				new L2DropData(BSOR, 1, 1, 50000),
				new L2DropData(ENCH_WPN_C, 3, 3, 14000),
				new L2DropData(ENCH_WPN_B, 2, 2, 7000),
				new L2DropData(ENCH_WPN_A, 1, 1, 7000),
				new L2DropData(RABBIT_EARS, 1, 1, 5000),
				new L2DropData(FEATHERED_HAT, 1, 1, 5000),
				new L2DropData(FAIRY_ANTENNAE, 1, 1, 5000),
				new L2DropData(RING_OF_ANT_QUIEEN, 1, 1, 50),
				new L2DropData(RING_OF_CORE, 1, 1, 50),
				new L2DropData(EARRING_OF_ORFEN, 1, 1, 50),
				new L2DropData(FRINTEZZA_NECKLACE, 1, 1, 50), });
		*/

		// дальше трогать не рекомендуется
		final int DROP_MULT = 3; // Множитель шанса дропа
		// Балансируем дроплист на базе используемых слов
		HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
		for(Integer[][] ii : _words.values())
			for(Integer[] i : ii)
			{
				Integer curr = temp.get(i[0]);
				if(curr == null)
					temp.put(i[0], i[1]);
				else
					temp.put(i[0], curr + i[1]);
			}
		letters = new int[temp.size()][2];
		int i = 0;
		for(Entry<Integer, Integer> e : temp.entrySet())
			letters[i++] = new int[] { e.getKey(), e.getValue() * DROP_MULT };
	}
}