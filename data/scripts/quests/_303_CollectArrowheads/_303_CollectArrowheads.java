package quests._303_CollectArrowheads;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _303_CollectArrowheads extends Quest implements ScriptFile
{
	int ORCISH_ARROWHEAD = 963;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _303_CollectArrowheads()
	{
		super(false);

		addStartNpc(30029);

		addTalkId(30029);

		addKillId(20361);

		addQuestItem(ORCISH_ARROWHEAD);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("minx_q0303_04.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");

		if(cond == 0)
			if(st.getPlayer().getLevel() >= 10)
				htmltext = "minx_q0303_03.htm";
			else
			{
				htmltext = "minx_q0303_02.htm";
				st.exitCurrentQuest(true);
			}
		else if(st.getQuestItemsCount(ORCISH_ARROWHEAD) < 10)
			htmltext = "minx_q0303_05.htm";
		else
		{
			st.takeItems(ORCISH_ARROWHEAD, -1);
			st.giveItems(ADENA_ID, 1000);
			st.addExpAndSp(2000, 0);
			htmltext = "minx_q0303_06.htm";
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(ORCISH_ARROWHEAD) < 10)
		{
			st.giveItems(ORCISH_ARROWHEAD, 1);
			if(st.getQuestItemsCount(ORCISH_ARROWHEAD) == 10)
			{
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}