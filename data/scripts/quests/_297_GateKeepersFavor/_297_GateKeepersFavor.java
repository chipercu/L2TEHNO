package quests._297_GateKeepersFavor;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _297_GateKeepersFavor extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int STARSTONE = 1573;
	private static final int GATEKEEPER_TOKEN = 1659;

	public _297_GateKeepersFavor()
	{
		super(false);
		addStartNpc(30540);
		addTalkId(30540);
		addKillId(20521);
		addQuestItem(STARSTONE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gatekeeper_wirphy_q0297_03.htm"))
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
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == 30540)
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 15)
					htmltext = "gatekeeper_wirphy_q0297_02.htm";
				else
					htmltext = "gatekeeper_wirphy_q0297_01.htm";
			}
			else if(cond == 1 && st.getQuestItemsCount(STARSTONE) < 20)
				htmltext = "gatekeeper_wirphy_q0297_04.htm";
			else if(cond == 2 && st.getQuestItemsCount(STARSTONE) < 20)
				htmltext = "gatekeeper_wirphy_q0297_04.htm";
			else if(cond == 2 && st.getQuestItemsCount(STARSTONE) >= 20)
			{
				htmltext = "gatekeeper_wirphy_q0297_05.htm";
				st.takeItems(STARSTONE, -1);
				st.giveItems(GATEKEEPER_TOKEN, 2);
				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.rollAndGive(STARSTONE, 1, 1, 20, 33);
		if(st.getQuestItemsCount(STARSTONE) >= 20)
			st.set("cond", "2");
		return null;
	}
}