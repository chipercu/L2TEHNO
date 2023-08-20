package quests._019_GoToThePastureland;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _019_GoToThePastureland extends Quest implements ScriptFile
{
	int VLADIMIR = 31302;
	int TUNATUN = 31537;

	int BEAST_MEAT = 7547;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _019_GoToThePastureland()
	{
		super(false);

		addStartNpc(VLADIMIR);

		addTalkId(VLADIMIR);
		addTalkId(TUNATUN);

		addQuestItem(BEAST_MEAT);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("trader_vladimir_q0019_0104.htm"))
		{
			st.giveItems(BEAST_MEAT, 1);
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equals("beast_herder_tunatun_q0019_0201.htm"))
		{
			st.takeItems(BEAST_MEAT, -1);
			st.addExpAndSp(385040, 75250);
			st.giveItems(ADENA_ID, 147200);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == VLADIMIR)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 82)
					htmltext = "trader_vladimir_q0019_0101.htm";
				else
				{
					htmltext = "trader_vladimir_q0019_0103.htm";
					st.exitCurrentQuest(true);
				}
			else
				htmltext = "trader_vladimir_q0019_0105.htm";
		}
		else if(npcId == TUNATUN)
			if(st.getQuestItemsCount(BEAST_MEAT) >= 1)
				htmltext = "beast_herder_tunatun_q0019_0101.htm";
			else
			{
				htmltext = "beast_herder_tunatun_q0019_0202.htm";
				st.exitCurrentQuest(true);
			}
		return htmltext;
	}
}