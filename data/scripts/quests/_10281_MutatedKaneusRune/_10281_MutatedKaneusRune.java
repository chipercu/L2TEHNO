package quests._10281_MutatedKaneusRune;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10281_MutatedKaneusRune extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	// NPCs
	private static final int Mathias = 31340;
	private static final int Kayan = 31335;

	// MOBs
	private static final int WhiteAllosce = 18577;

	// Items
	private static final int Tissue = 13840;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public _10281_MutatedKaneusRune()
	{
		super(PARTY_ALL);
		addStartNpc(Mathias);
		addTalkId(Kayan);
		addKillId(WhiteAllosce);
		addQuestItem(Tissue);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31340-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("31335-02.htm"))
		{
			st.giveItems(57, 360000);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if(id == COMPLETED)
		{
			if(npcId == Mathias)
				htmltext = "31340-0a.htm";
		}
		else if(id == CREATED && npcId == Mathias)
		{
			if(st.getPlayer().getLevel() >= 68)
				htmltext = "31340-01.htm";
			else
				htmltext = "31340-00.htm";
		}
		else
		{
			if(npcId == Mathias)
			{
				if(cond == 1)
					htmltext = "31340-04.htm";
				else if(cond == 2)
					htmltext = "31340-05.htm";
			}
			else if(npcId == Kayan)
			{
				if(cond == 1)
					htmltext = "31335-01a.htm";
				else if(cond == 2)
					htmltext = "31335-01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() == STARTED && st.getCond() == 1)
		{
			st.giveItems(Tissue, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}