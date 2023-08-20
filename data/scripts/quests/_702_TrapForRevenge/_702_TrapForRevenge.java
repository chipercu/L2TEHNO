package quests._702_TrapForRevenge;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._10273_GoodDayToFly._10273_GoodDayToFly;

public class _702_TrapForRevenge extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _702_TrapForRevenge()
	{
		super(true);
		addStartNpc(32563);
		addTalkId(32555);
		addKillId(22612, 25631, 22610);
		addQuestItem(13877, 9629);
	}

	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;

		if (event.equalsIgnoreCase("wharf_soldier_plenos_q0702_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("soldier_tenis_q0702_03.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		if(event.equalsIgnoreCase("exit"))
		{
			if (st.getQuestItemsCount(13877) < 100)
				return "soldier_tenis_q0702_06.htm";
			st.takeItems(13877, -1);
			st.giveItems(57, 3144000);
			st.giveItems(9629, 3);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return "exit.htm";
		}
		return htmltext;
	}

	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		if(npcId == 32563)
		{
			if(state == 1)
			{
				QuestState qs = st.getPlayer().getQuestState(_10273_GoodDayToFly.class);
				if (qs != null && qs.isCompleted() && st.getPlayer().getLevel() >= 78)
					return "wharf_soldier_plenos_q0702_01.htm";
				return "lvl.htm";
			}
			if(state == 2 && cond == 1)
				return "wharf_soldier_plenos_q0702_05.htm";
		}
		if(npcId == 32555)
		{
			if(cond == 1)
				return "soldier_tenis_q0702_01.htm";
			if(cond == 2)
				return "soldier_tenis_q0702_04.htm";
		}
		return "noquest";
	}

	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() != 2)
			return null;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 2 && Rnd.chance(50) && (npcId == 22612 || npcId == 25631 || npcId == 22610))
		{
			st.giveItems(13877, 1);
			st.playSound("ItemSound.quest_itemget");
		}
		return null;
	}
}