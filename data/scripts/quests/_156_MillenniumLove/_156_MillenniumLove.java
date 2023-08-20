package quests._156_MillenniumLove;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _156_MillenniumLove extends Quest implements ScriptFile
{
	int LILITHS_LETTER = 1022;
	int THEONS_DIARY = 1023;
	int GR_COMP_PACKAGE_SS = 5250;
	int GR_COMP_PACKAGE_SPS = 5256;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _156_MillenniumLove()
	{
		super(false);

		addStartNpc(30368);

		addTalkId(30368);
		addTalkId(30368);
		addTalkId(30368);
		addTalkId(30369);

		addQuestItem(new int[] { LILITHS_LETTER, THEONS_DIARY });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			htmltext = "rylith_q0156_06.htm";
			st.giveItems(LILITHS_LETTER, 1);
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("156_1"))
		{
			st.takeItems(LILITHS_LETTER, -1);
			if(st.getQuestItemsCount(THEONS_DIARY) == 0)
			{
				st.giveItems(THEONS_DIARY, 1);
				st.set("cond", "2");
			}
			htmltext = "master_baenedes_q0156_03.htm";
		}
		else if(event.equals("156_2"))
		{
			st.takeItems(LILITHS_LETTER, -1);
			st.playSound(SOUND_FINISH);
			htmltext = "master_baenedes_q0156_04.htm";
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(npcId == 30368)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 15)
					htmltext = "rylith_q0156_02.htm";
				else
				{
					htmltext = "rylith_q0156_05.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 && st.getQuestItemsCount(LILITHS_LETTER) == 1)
				htmltext = "rylith_q0156_07.htm";
			else if(cond == 2 && st.getQuestItemsCount(THEONS_DIARY) == 1)
			{
				st.takeItems(THEONS_DIARY, -1);
				if(st.getPlayer().getClassId().isMage())
					st.giveItems(GR_COMP_PACKAGE_SPS, 1);
				else
					st.giveItems(GR_COMP_PACKAGE_SS, 1);
				st.addExpAndSp(3000, 0);
				st.playSound(SOUND_FINISH);
				htmltext = "rylith_q0156_08.htm";
				st.exitCurrentQuest(false);
			}
		}
		else if(npcId == 30369)
			if(cond == 1 && st.getQuestItemsCount(LILITHS_LETTER) == 1)
				htmltext = "master_baenedes_q0156_02.htm";
			else if(cond == 2 && st.getQuestItemsCount(THEONS_DIARY) == 1)
				htmltext = "master_baenedes_q0156_05.htm";
		return htmltext;
	}
}