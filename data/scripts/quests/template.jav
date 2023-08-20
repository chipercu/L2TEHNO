package quests.qqqqq;

import l2p.extensions.scripts.ScriptFile;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.model.quest.Quest;
import l2p.gameserver.model.quest.QuestState;

public class qqqqq extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public qqqqq()
	{
		super(false);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		return null;
	}
}