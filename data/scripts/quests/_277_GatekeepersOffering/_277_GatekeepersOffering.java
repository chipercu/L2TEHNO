package quests._277_GatekeepersOffering;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _277_GatekeepersOffering extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int STARSTONE1_ID = 1572;
	private static final int GATEKEEPER_CHARM_ID = 1658;

	public _277_GatekeepersOffering()
	{
		super(false);
		addStartNpc(30576);
		addKillId(20333);
		addQuestItem(STARSTONE1_ID);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
			if(st.getPlayer().getLevel() >= 15)
			{
				htmltext = "gatekeeper_tamil_q0277_03.htm";
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
			else
				htmltext = "gatekeeper_tamil_q0277_01.htm";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");

		if(npcId == 30576 && cond == 0)
			htmltext = "gatekeeper_tamil_q0277_02.htm";
		else if(npcId == 30576 && cond == 1 && st.getQuestItemsCount(STARSTONE1_ID) < 20)
			htmltext = "gatekeeper_tamil_q0277_04.htm";
		else if(npcId == 30576 && cond == 2 && st.getQuestItemsCount(STARSTONE1_ID) < 20)
			htmltext = "gatekeeper_tamil_q0277_04.htm";
		else if(npcId == 30576 && cond == 2 && st.getQuestItemsCount(STARSTONE1_ID) >= 20)
		{
			htmltext = "gatekeeper_tamil_q0277_05.htm";
			st.takeItems(STARSTONE1_ID, -1);
			st.giveItems(GATEKEEPER_CHARM_ID, 2);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.rollAndGive(STARSTONE1_ID, 1, 1, 20, 33);
		if(st.getQuestItemsCount(STARSTONE1_ID) >= 20)
			st.set("cond", "2");
		return null;
	}
}