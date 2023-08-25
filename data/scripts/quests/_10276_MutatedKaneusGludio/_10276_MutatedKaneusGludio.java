package quests._10276_MutatedKaneusGludio;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10276_MutatedKaneusGludio extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	// NPCs
	private static final int Bathis = 30332;
	private static final int Rohmer = 30344;

	// MOBs
	private static final int TomlanKamos = 18554;
	private static final int OlAriosh = 18555;

	// Items
	private static final int Tissue1 = 13830;
	private static final int Tissue2 = 13831;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public _10276_MutatedKaneusGludio()
	{
		super(PARTY_ALL);
		addStartNpc(Bathis);
		addTalkId(Rohmer);
		addKillId(TomlanKamos, OlAriosh);
		addQuestItem(Tissue1, Tissue2);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30332-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30344-02.htm"))
		{
			st.giveItems(57, 8500);
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
			if(npcId == Bathis)
				htmltext = "30332-0a.htm";
		}
		else if(id == CREATED && npcId == Bathis)
		{
			if(st.getPlayer().getLevel() >= 18)
				htmltext = "30332-01.htm";
			else
				htmltext = "30332-00.htm";
		}
		else
		{
			if(npcId == Bathis)
			{
				if(cond == 1)
					htmltext = "30332-04.htm";
				else if(cond == 2)
					htmltext = "30332-05.htm";
			}
			else if(npcId == Rohmer)
			{
				if(cond == 1)
					htmltext = "30344-01a.htm";
				else if(cond == 2)
					htmltext = "30344-01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
	
		if(st.getState() == STARTED && st.getCond() == 1)
		{
			if((npc.getNpcId() == TomlanKamos) && (st.getQuestItemsCount(Tissue1) == 0))
			{
				st.giveItems(Tissue1, 1);
				st.playSound("ItemSound.quest_itemget");
			}
			else if((npc.getNpcId() == OlAriosh) && (st.getQuestItemsCount(Tissue2) == 0))
			{
				st.giveItems(Tissue2, 1);
				st.playSound("ItemSound.quest_itemget");
			}
			if(st.getQuestItemsCount(Tissue2) > 0 && st.getQuestItemsCount(Tissue1) > 0)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}