package quests._155_FindSirWindawood;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _155_FindSirWindawood extends Quest implements ScriptFile
{
	int OFFICIAL_LETTER = 1019;
	int HASTE_POTION = 734;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _155_FindSirWindawood()
	{
		super(false);

		addStartNpc(30042);

		addTalkId(30042);
		addTalkId(30311);

		addQuestItem(OFFICIAL_LETTER);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			htmltext = "abel_q0155_04.htm";
			st.giveItems(OFFICIAL_LETTER, 1);
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
		int cond = st.getInt("cond");
		if(npcId == 30042)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 3)
				{
					htmltext = "abel_q0155_03.htm";
					return htmltext;
				}
				htmltext = "abel_q0155_02.htm";
				st.exitCurrentQuest(true);
			}
			else if(cond == 1 && st.getQuestItemsCount(OFFICIAL_LETTER) == 1)
				htmltext = "abel_q0155_05.htm";
		}
		else if(npcId == 30311 && cond == 1 && st.getQuestItemsCount(OFFICIAL_LETTER) == 1)
		{
			htmltext = "sir_collin_windawood_q0155_01.htm";
			st.takeItems(OFFICIAL_LETTER, -1);
			st.giveItems(HASTE_POTION, 1);
			st.set("cond", "0");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}
}