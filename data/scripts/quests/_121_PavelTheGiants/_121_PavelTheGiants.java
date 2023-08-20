package quests._121_PavelTheGiants;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _121_PavelTheGiants extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	//NPCs
	private static int NEWYEAR = 31961;
	private static int YUMI = 32041;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _121_PavelTheGiants()
	{
		super(false);

		addStartNpc(NEWYEAR);
		addTalkId(NEWYEAR, YUMI);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equals("collecter_yumi_q0121_0201.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(346320, 26069, true);
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();

		if(id == CREATED && npcId == NEWYEAR)
		{
			if(st.getPlayer().getLevel() >= 70)
			{
				htmltext = "head_blacksmith_newyear_q0121_0101.htm";
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
			else
			{
				htmltext = "head_blacksmith_newyear_q0121_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(id == STARTED)
			if(npcId == YUMI && cond == 1)
				htmltext = "collecter_yumi_q0121_0101.htm";
			else
				htmltext = "head_blacksmith_newyear_q0121_0105.htm";
		return htmltext;
	}
}