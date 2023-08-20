package quests._163_LegacyOfPoet;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _163_LegacyOfPoet extends Quest implements ScriptFile
{
	int RUMIELS_POEM_1_ID = 1038;
	int RUMIELS_POEM_3_ID = 1039;
	int RUMIELS_POEM_4_ID = 1040;
	int RUMIELS_POEM_5_ID = 1041;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _163_LegacyOfPoet()
	{
		super(false);

		addStartNpc(30220);

		addTalkId(30220);

		addTalkId(30220);

		addKillId(20372);
		addKillId(20373);

		addQuestItem(new int[] { RUMIELS_POEM_1_ID, RUMIELS_POEM_3_ID, RUMIELS_POEM_4_ID, RUMIELS_POEM_5_ID });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			st.set("id", "0");
			htmltext = "30220-07.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if(id == CREATED)
		{
			st.setState(STARTED);
			st.set("cond", "0");
			st.set("id", "0");
		}
		if(npcId == 30220 && st.getInt("cond") == 0)
		{
			if(st.getInt("cond") < 15)
			{
				if(st.getPlayer().getRace() == Race.darkelf)
					htmltext = "30220-00.htm";
				else if(st.getPlayer().getLevel() >= 11)
				{
					htmltext = "30220-03.htm";
					return htmltext;
				}
				else
				{
					htmltext = "30220-02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "30220-02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == 30220 && st.getInt("cond") == 0)
			htmltext = "completed";
		else if(npcId == 30220 && st.getInt("cond") > 0)
			if(st.getQuestItemsCount(RUMIELS_POEM_1_ID) == 1 && st.getQuestItemsCount(RUMIELS_POEM_3_ID) == 1 && st.getQuestItemsCount(RUMIELS_POEM_4_ID) == 1 && st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 1)
			{
				if(st.getInt("id") != 163)
				{
					st.set("id", "163");
					htmltext = "30220-09.htm";
					st.takeItems(RUMIELS_POEM_1_ID, 1);
					st.takeItems(RUMIELS_POEM_3_ID, 1);
					st.takeItems(RUMIELS_POEM_4_ID, 1);
					st.takeItems(RUMIELS_POEM_5_ID, 1);
					st.giveItems(ADENA_ID, 13890);
					st.addExpAndSp(21643, 943);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(false);
				}
			}
			else
				htmltext = "30220-08.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == 20372 || npcId == 20373)
		{
			st.set("id", "0");
			if(st.getInt("cond") == 1)
			{
				if(Rnd.chance(10) && st.getQuestItemsCount(RUMIELS_POEM_1_ID) == 0)
				{
					st.giveItems(RUMIELS_POEM_1_ID, 1);
					if(st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
						st.playSound(SOUND_MIDDLE);
					else
						st.playSound(SOUND_ITEMGET);
				}
				if(Rnd.chance(70) && st.getQuestItemsCount(RUMIELS_POEM_3_ID) == 0)
				{
					st.giveItems(RUMIELS_POEM_3_ID, 1);
					if(st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
						st.playSound(SOUND_MIDDLE);
					else
						st.playSound(SOUND_ITEMGET);
				}
				if(Rnd.chance(70) && st.getQuestItemsCount(RUMIELS_POEM_4_ID) == 0)
				{
					st.giveItems(RUMIELS_POEM_4_ID, 1);
					if(st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
						st.playSound(SOUND_MIDDLE);
					else
						st.playSound(SOUND_ITEMGET);
				}
				//if(st.getRandom(10)>5 && st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 0)
				if(Rnd.chance(50) && st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 0)
				{
					st.giveItems(RUMIELS_POEM_5_ID, 1);
					if(st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
						st.playSound(SOUND_MIDDLE);
					else
						st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}