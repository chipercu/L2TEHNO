package quests._215_TrialOfPilgrim;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _215_TrialOfPilgrim extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int MARK_OF_PILGRIM_ID = 2721;
	private static final int BOOK_OF_SAGE_ID = 2722;
	private static final int VOUCHER_OF_TRIAL_ID = 2723;
	private static final int SPIRIT_OF_FLAME_ID = 2724;
	private static final int ESSENSE_OF_FLAME_ID = 2725;
	private static final int BOOK_OF_GERALD_ID = 2726;
	private static final int GREY_BADGE_ID = 2727;
	private static final int PICTURE_OF_NAHIR_ID = 2728;
	private static final int HAIR_OF_NAHIR_ID = 2729;
	private static final int STATUE_OF_EINHASAD_ID = 2730;
	private static final int BOOK_OF_DARKNESS_ID = 2731;
	private static final int DEBRIS_OF_WILLOW_ID = 2732;
	private static final int TAG_OF_RUMOR_ID = 2733;
	private static final int ADENA_ID = 57;
	private static final int RewardExp = 629125;
	private static final int RewardSP = 40803;
	private static final int RewardAdena = 114649;

	public _215_TrialOfPilgrim()
	{
		super(false);

		addStartNpc(30648);

		addTalkId(30036);
		addTalkId(30117);
		addTalkId(30362);
		addTalkId(30550);
		addTalkId(30571);
		addTalkId(30612);
		addTalkId(30648);
		addTalkId(30649);
		addTalkId(30650);
		addTalkId(30651);
		addTalkId(30652);

		addKillId(27116);
		addKillId(27117);
		addKillId(27118);

		addQuestItem(new int[] { BOOK_OF_SAGE_ID, VOUCHER_OF_TRIAL_ID, ESSENSE_OF_FLAME_ID, BOOK_OF_GERALD_ID,
				TAG_OF_RUMOR_ID, PICTURE_OF_NAHIR_ID, HAIR_OF_NAHIR_ID, BOOK_OF_DARKNESS_ID, DEBRIS_OF_WILLOW_ID,
				GREY_BADGE_ID, SPIRIT_OF_FLAME_ID, STATUE_OF_EINHASAD_ID, });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			htmltext = "hermit_santiago_q0215_04.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(VOUCHER_OF_TRIAL_ID, 1);
		}
		if(!st.getPlayer().getVarB("dd1"))
		{
			st.giveItems(7562, 64);
			st.getPlayer().setVar("dd1", "1");
		}
		else if(event.equals("30648_1"))
			htmltext = "hermit_santiago_q0215_05.htm";
		else if(event.equals("30648_2"))
			htmltext = "hermit_santiago_q0215_06.htm";
		else if(event.equals("30648_3"))
			htmltext = "hermit_santiago_q0215_07.htm";
		else if(event.equals("30648_4"))
			htmltext = "hermit_santiago_q0215_08.htm";
		else if(event.equals("30648_5"))
			htmltext = "hermit_santiago_q0215_05.htm";
		else if(event.equals("30649_1"))
		{
			htmltext = "ancestor_martankus_q0215_04.htm";
			st.giveItems(SPIRIT_OF_FLAME_ID, 1);
			st.takeItems(ESSENSE_OF_FLAME_ID, 1);
			st.set("cond", "5");
		}
		else if(event.equals("30650_1"))
		{
			if(st.getQuestItemsCount(ADENA_ID) >= 100000)
			{
				htmltext = "gerald_priest_of_earth_q0215_02.htm";
				st.giveItems(BOOK_OF_GERALD_ID, 1);
				st.takeItems(ADENA_ID, 100000);
				st.set("cond", "7");
			}
			else
				htmltext = "gerald_priest_of_earth_q0215_03.htm";
		}
		else if(event.equals("30650_2"))
			htmltext = "gerald_priest_of_earth_q0215_03.htm";
		else if(event.equals("30362_1"))
		{
			htmltext = "andellria_q0215_05.htm";
			st.takeItems(BOOK_OF_DARKNESS_ID, 1);
			st.set("cond", "16");
		}
		else if(event.equals("30362_2"))
		{
			htmltext = "andellria_q0215_04.htm";
			st.set("cond", "16");
		}
		else if(event.equals("30652_1"))
		{
			htmltext = "uruha_q0215_02.htm";
			st.giveItems(BOOK_OF_DARKNESS_ID, 1);
			st.takeItems(DEBRIS_OF_WILLOW_ID, 1);
			st.set("cond", "15");
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_PILGRIM_ID) > 0)
		{
			st.exitCurrentQuest(true);
			return "completed";
		}
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if(id == CREATED)
		{
			st.setState(STARTED);
			st.set("cond", "0");
			st.set("id", "0");
		}
		if(npcId == 30648 && st.getInt("cond") == 0)
		{
			if(st.getPlayer().getClassId().getId() == 0x0f || st.getPlayer().getClassId().getId() == 0x1d || st.getPlayer().getClassId().getId() == 0x2a || st.getPlayer().getClassId().getId() == 0x32)
			{
				if(st.getPlayer().getLevel() >= 35)
					htmltext = "hermit_santiago_q0215_03.htm";
				else
				{
					htmltext = "hermit_santiago_q0215_01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "hermit_santiago_q0215_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == 30648 && st.getInt("cond") == 1 && st.getQuestItemsCount(VOUCHER_OF_TRIAL_ID) > 0)
			htmltext = "hermit_santiago_q0215_09.htm";
		else if(npcId == 30648 && st.getInt("cond") == 17 && st.getQuestItemsCount(BOOK_OF_SAGE_ID) > 0)
		{
			htmltext = "hermit_santiago_q0215_10.htm";
			st.takeItems(BOOK_OF_SAGE_ID, -1);
			st.giveItems(MARK_OF_PILGRIM_ID, 1);
			if(!st.getPlayer().getVarB("prof2.1"))
			{
				st.addExpAndSp(RewardExp, RewardSP, true);
				st.giveItems(ADENA_ID, RewardAdena, ConfigValue.RateQuestsRewardOccupationChange);
				st.getPlayer().setVar("prof2.1", "1");
			}
			st.playSound(SOUND_FINISH);
			st.unset("cond");
			st.exitCurrentQuest(false);
		}
		else if(npcId == 30571 && st.getInt("cond") == 1 && st.getQuestItemsCount(VOUCHER_OF_TRIAL_ID) > 0)
		{
			htmltext = "seer_tanapi_q0215_01.htm";
			st.takeItems(VOUCHER_OF_TRIAL_ID, 1);
			st.set("cond", "2");
		}
		else if(npcId == 30571 && st.getInt("cond") == 2)
			htmltext = "seer_tanapi_q0215_02.htm";
		else if(npcId == 30571 && st.getInt("cond") == 5 && st.getQuestItemsCount(SPIRIT_OF_FLAME_ID) > 0)
			htmltext = "seer_tanapi_q0215_03.htm";
		else if(npcId == 30649 && st.getInt("cond") == 2)
		{
			htmltext = "ancestor_martankus_q0215_01.htm";
			st.set("cond", "3");
		}
		else if(npcId == 30649 && st.getInt("cond") == 3)
			htmltext = "ancestor_martankus_q0215_02.htm";
		else if(npcId == 30649 && st.getInt("cond") == 4 && st.getQuestItemsCount(ESSENSE_OF_FLAME_ID) > 0)
			htmltext = "ancestor_martankus_q0215_03.htm";
		else if(npcId == 30550 && st.getInt("cond") == 5 && st.getQuestItemsCount(SPIRIT_OF_FLAME_ID) > 0)
		{
			htmltext = "gauri_twinklerock_q0215_01.htm";
			st.giveItems(TAG_OF_RUMOR_ID, 1);
			st.set("cond", "6");
		}
		else if(npcId == 30550 && st.getInt("cond") == 6)
			htmltext = "gauri_twinklerock_q0215_02.htm";
		else if(npcId == 30650 && st.getInt("cond") == 6 && st.getQuestItemsCount(TAG_OF_RUMOR_ID) > 0)
			htmltext = "gerald_priest_of_earth_q0215_01.htm";
		else if(npcId == 30650 && st.getInt("cond") >= 8 && st.getQuestItemsCount(GREY_BADGE_ID) > 0 && st.getQuestItemsCount(BOOK_OF_GERALD_ID) > 0)
		{
			htmltext = "gerald_priest_of_earth_q0215_04.htm";
			st.giveItems(ADENA_ID, 100000, false);
			st.takeItems(BOOK_OF_GERALD_ID, 1);
		}
		else if(npcId == 30651 && st.getInt("cond") == 6 && st.getQuestItemsCount(TAG_OF_RUMOR_ID) > 0)
		{
			htmltext = "wanderer_dorf_q0215_01.htm";
			st.giveItems(GREY_BADGE_ID, 1);
			st.takeItems(TAG_OF_RUMOR_ID, 1);
			st.set("cond", "8");
		}
		else if(npcId == 30651 && st.getInt("cond") == 7 && st.getQuestItemsCount(TAG_OF_RUMOR_ID) > 0)
		{
			htmltext = "wanderer_dorf_q0215_02.htm";
			st.giveItems(GREY_BADGE_ID, 1);
			st.takeItems(TAG_OF_RUMOR_ID, 1);
			st.set("cond", "8");
		}
		else if(npcId == 30651 && st.getInt("cond") == 8)
			htmltext = "wanderer_dorf_q0215_03.htm";
		else if(npcId == 30117 && st.getInt("cond") == 8)
		{
			htmltext = "primoz_q0215_01.htm";
			st.set("cond", "9");
		}
		else if(npcId == 30117 && st.getInt("cond") == 9)
			htmltext = "primoz_q0215_02.htm";
		else if(npcId == 30036 && st.getInt("cond") == 9)
		{
			htmltext = "potter_q0215_01.htm";
			st.giveItems(PICTURE_OF_NAHIR_ID, 1);
			st.set("cond", "10");
		}
		else if(npcId == 30036 && st.getInt("cond") == 10)
			htmltext = "potter_q0215_02.htm";
		else if(npcId == 30036 && st.getInt("cond") == 11)
		{
			htmltext = "potter_q0215_03.htm";
			st.giveItems(STATUE_OF_EINHASAD_ID, 1);
			st.takeItems(PICTURE_OF_NAHIR_ID, 1);
			st.takeItems(HAIR_OF_NAHIR_ID, 1);
			st.set("cond", "12");
		}
		else if(npcId == 30036 && st.getInt("cond") == 12 && st.getQuestItemsCount(STATUE_OF_EINHASAD_ID) > 0)
			htmltext = "potter_q0215_04.htm";
		else if(npcId == 30362 && st.getInt("cond") == 12)
		{
			htmltext = "andellria_q0215_01.htm";
			st.set("cond", "13");
		}
		else if(npcId == 30362 && st.getInt("cond") == 13)
			htmltext = "andellria_q0215_02.htm";
		else if(npcId == 30362 && st.getInt("cond") == 15 && st.getQuestItemsCount(BOOK_OF_DARKNESS_ID) > 0)
			htmltext = "andellria_q0215_03.htm";
		else if(npcId == 30362 && st.getInt("cond") == 16)
			htmltext = "andellria_q0215_06.htm";
		else if(npcId == 30362 && st.getInt("cond") == 15 && st.getQuestItemsCount(BOOK_OF_DARKNESS_ID) == 0)
			htmltext = "andellria_q0215_07.htm";
		else if(npcId == 30652 && st.getInt("cond") == 14 && st.getQuestItemsCount(DEBRIS_OF_WILLOW_ID) > 0)
			htmltext = "uruha_q0215_01.htm";
		else if(npcId == 30652 && st.getInt("cond") == 15 && st.getQuestItemsCount(BOOK_OF_DARKNESS_ID) > 0)
			htmltext = "uruha_q0215_03.htm";
		else if(npcId == 30612 && st.getInt("cond") == 16)
		{
			htmltext = "sage_kasian_q0215_01.htm";
			st.giveItems(BOOK_OF_SAGE_ID, 1);

			if(st.getQuestItemsCount(BOOK_OF_DARKNESS_ID) > 0)
				st.takeItems(BOOK_OF_DARKNESS_ID, 1);
			if(st.getQuestItemsCount(BOOK_OF_GERALD_ID) > 0)
				st.takeItems(BOOK_OF_GERALD_ID, 1);
			st.set("cond", "17");
			st.takeItems(GREY_BADGE_ID, 1);
			st.takeItems(SPIRIT_OF_FLAME_ID, 1);
			st.takeItems(STATUE_OF_EINHASAD_ID, 1);
		}

		else if(npcId == 30612 && st.getInt("cond") == 17)
			htmltext = "sage_kasian_q0215_02.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == 27116)
		{
			if(st.getInt("cond") == 3 && st.getQuestItemsCount(ESSENSE_OF_FLAME_ID) == 0)
				if(Rnd.chance(30))
				{
					st.giveItems(ESSENSE_OF_FLAME_ID, 1);
					st.set("cond", "4");
					st.playSound(SOUND_MIDDLE);
				}
		}
		else if(npcId == 27117)
		{
			if(st.getInt("cond") == 10 && st.getQuestItemsCount(HAIR_OF_NAHIR_ID) == 0)
			{
				st.giveItems(HAIR_OF_NAHIR_ID, 1);
				st.set("cond", "11");
				st.playSound(SOUND_MIDDLE);
			}
		}
		else if(npcId == 27118)
			if(st.getInt("cond") == 13 && st.getQuestItemsCount(DEBRIS_OF_WILLOW_ID) == 0)
				if(Rnd.chance(20))
				{
					st.giveItems(DEBRIS_OF_WILLOW_ID, 1);
					st.set("cond", "14");
					st.playSound(SOUND_MIDDLE);
				}
		return null;
	}
}