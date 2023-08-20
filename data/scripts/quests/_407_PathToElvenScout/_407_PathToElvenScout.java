package quests._407_PathToElvenScout;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NewbieGuideInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _407_PathToElvenScout extends Quest implements ScriptFile
{

	public final int REISA = 30328;
	public final int MORETTI = 30337;
	public final int PIPPEN = 30426;

	public final int OL_MAHUM_SENTRY = 27031;
	public final int OL_MAHUM_PATROL = 20053;

	public final short REORIA_LETTER2_ID = 1207;
	public final short PRIGUNS_TEAR_LETTER1_ID = 1208;
	public final short PRIGUNS_TEAR_LETTER2_ID = 1209;
	public final short PRIGUNS_TEAR_LETTER3_ID = 1210;
	public final short PRIGUNS_TEAR_LETTER4_ID = 1211;
	public final short MORETTIS_HERB_ID = 1212;
	public final short MORETTIS_LETTER_ID = 1214;
	public final short PRIGUNS_LETTER_ID = 1215;
	public final short MONORARY_GUARD_ID = 1216;
	public final short REORIA_RECOMMENDATION_ID = 1217;
	public final short RUSTED_KEY_ID = 1293;
	public final short HONORARY_GUARD_ID = 1216;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _407_PathToElvenScout()
	{
		super(false);

		addStartNpc(REISA);

		addTalkId(MORETTI);
		addTalkId(PIPPEN);

		addKillId(OL_MAHUM_SENTRY);
		addKillId(OL_MAHUM_PATROL);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			if(st.getPlayer().getClassId().getId() == 0x12)
			{
				if(st.getPlayer().getLevel() >= 18)
				{
					if(st.getQuestItemsCount(REORIA_RECOMMENDATION_ID) > 0)
					{
						htmltext = "master_reoria_q0407_04.htm";
						st.exitCurrentQuest(true);
					}
					else
					{
						htmltext = "master_reoria_q0407_05.htm";
						st.giveItems(REORIA_LETTER2_ID, 1);
						st.set("cond", "1");
						st.setState(STARTED);
						st.playSound(SOUND_ACCEPT);
					}
				}
				else
				{
					htmltext = "master_reoria_q0407_03.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(st.getPlayer().getClassId().getId() == 0x16)
			{
				htmltext = "master_reoria_q0407_02a.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "master_reoria_q0407_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(event.equalsIgnoreCase("30337_1"))
		{
			st.takeItems(REORIA_LETTER2_ID, 1);
			st.set("cond", "2");
			htmltext = "guard_moretti_q0407_03.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if(id != CREATED)
			cond = st.getInt("cond");
		if(npcId == REISA)
		{
			if(cond == 0)
				htmltext = "master_reoria_q0407_01.htm";
			else if(cond == 1)
				htmltext = "master_reoria_q0407_06.htm";
			else if(cond > 1 && st.getQuestItemsCount(HONORARY_GUARD_ID) == 0)
				htmltext = "master_reoria_q0407_08.htm";
			else if(cond == 8 && st.getQuestItemsCount(HONORARY_GUARD_ID) == 1)
			{
				htmltext = "master_reoria_q0407_07.htm";
				st.takeItems(HONORARY_GUARD_ID, 1);
				if(st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(REORIA_RECOMMENDATION_ID, 1);
					if(!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1");
						st.addExpAndSp(228064, 16455, true);
						st.giveItems(ADENA_ID, 81900, ConfigValue.RateQuestsRewardOccupationChange);
					}
				}
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == MORETTI)
		{
			if(cond == 1)
				htmltext = "guard_moretti_q0407_01.htm";
			else if(cond == 2)
				htmltext = "guard_moretti_q0407_04.htm";
			else if(cond == 3)
			{
				if(st.getQuestItemsCount(PRIGUNS_TEAR_LETTER1_ID) == 1 && st.getQuestItemsCount(PRIGUNS_TEAR_LETTER2_ID) == 1 && st.getQuestItemsCount(PRIGUNS_TEAR_LETTER3_ID) == 1 && st.getQuestItemsCount(PRIGUNS_TEAR_LETTER4_ID) == 1)
				{
					htmltext = "guard_moretti_q0407_06.htm";
					st.takeItems(PRIGUNS_TEAR_LETTER1_ID, 1);
					st.takeItems(PRIGUNS_TEAR_LETTER2_ID, 1);
					st.takeItems(PRIGUNS_TEAR_LETTER3_ID, 1);
					st.takeItems(PRIGUNS_TEAR_LETTER4_ID, 1);
					st.giveItems(MORETTIS_HERB_ID, 1);
					st.giveItems(MORETTIS_LETTER_ID, 1);
					st.set("cond", "4");
				}
				else
					htmltext = "guard_moretti_q0407_05.htm";
			}
			else if(cond == 7 && st.getQuestItemsCount(PRIGUNS_LETTER_ID) == 1)
			{
				htmltext = "guard_moretti_q0407_07.htm";
				st.takeItems(PRIGUNS_LETTER_ID, 1);
				st.giveItems(HONORARY_GUARD_ID, 1);
				st.set("cond", "8");
			}
			else if(cond > 8)
				htmltext = "guard_moretti_q0407_08.htm";
		}
		else if(npcId == PIPPEN)
			if(cond == 4)
			{
				htmltext = "prigun_q0407_01.htm";
				st.set("cond", "5");
			}
			else if(cond == 5)
				htmltext = "prigun_q0407_01.htm";
			else if(cond == 6 && st.getQuestItemsCount(RUSTED_KEY_ID) == 1 && st.getQuestItemsCount(MORETTIS_HERB_ID) == 1 && st.getQuestItemsCount(MORETTIS_LETTER_ID) == 1)
			{
				htmltext = "prigun_q0407_02.htm";
				st.takeItems(RUSTED_KEY_ID, 1);
				st.takeItems(MORETTIS_HERB_ID, 1);
				st.takeItems(MORETTIS_LETTER_ID, 1);
				st.giveItems(PRIGUNS_LETTER_ID, 1);
				st.set("cond", "7");
			}
			else if(cond == 7)
				htmltext = "prigun_q0407_04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == OL_MAHUM_PATROL && cond == 2)
		{
			if(st.getQuestItemsCount(PRIGUNS_TEAR_LETTER1_ID) == 0)
			{
				st.giveItems(PRIGUNS_TEAR_LETTER1_ID, 1);
				st.playSound(SOUND_ITEMGET);
				return null;
			}
			if(st.getQuestItemsCount(PRIGUNS_TEAR_LETTER2_ID) == 0)
			{
				st.giveItems(PRIGUNS_TEAR_LETTER2_ID, 1);
				st.playSound(SOUND_ITEMGET);
				return null;
			}
			if(st.getQuestItemsCount(PRIGUNS_TEAR_LETTER3_ID) == 0)
			{
				st.giveItems(PRIGUNS_TEAR_LETTER3_ID, 1);
				st.playSound(SOUND_ITEMGET);
				return null;
			}
			if(st.getQuestItemsCount(PRIGUNS_TEAR_LETTER4_ID) == 0)
			{
				st.giveItems(PRIGUNS_TEAR_LETTER4_ID, 1);
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "3");
				return null;
			}
		}
		else if(npcId == OL_MAHUM_SENTRY && cond == 5 && Rnd.chance(60))
		{
			st.giveItems(RUSTED_KEY_ID, 1);
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "6");
		}
		return null;
	}
}
