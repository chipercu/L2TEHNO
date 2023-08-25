package quests._651_RunawayYouth;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _651_RunawayYouth extends Quest implements ScriptFile
{
	//Npc
	private static int IVAN = 32014;
	private static int BATIDAE = 31989;
	protected L2NpcInstance _npc;

	//Items
	private static int SOE = 736;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _651_RunawayYouth()
	{
		super(false);

		addStartNpc(IVAN);
		addTalkId(BATIDAE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("runaway_boy_ivan_q0651_03.htm"))
		{
			if(st.getQuestItemsCount(SOE) > 0)
			{
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.takeItems(SOE, 1);
				htmltext = "runaway_boy_ivan_q0651_04.htm";
				//npc.broadcastPacket(MagicSkillUser(npc,npc,2013,1,20000,0));
				//Каст СОЕ и изчезновение НПЦ
				st.startQuestTimer("ivan_timer", 20000);
			}
		}
		else if(event.equalsIgnoreCase("runaway_boy_ivan_q0651_05.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_GIVEUP);
		}
		else if(event.equalsIgnoreCase("ivan_timer"))
		{
			_npc.deleteMe();
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == IVAN && cond == 0)
		{
			if(st.getPlayer().getLevel() >= 26)
				htmltext = "runaway_boy_ivan_q0651_01.htm";
			else
			{
				htmltext = "runaway_boy_ivan_q0651_01a.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == BATIDAE && cond == 1)
		{
			htmltext = "fisher_batidae_q0651_01.htm";
			st.giveItems(ADENA_ID, Math.round(2883 * st.getRateQuestsRewardAdena()));
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}
}
