package quests._10279_MutatedKaneusOren;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10279_MutatedKaneusOren extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	// NPCs
	private static final int Mouen = 30196;
	private static final int Rovia = 30189;

	// MOBs
	private static final int KaimAbigore = 18566;
	private static final int KnightMontagnar = 18568;

	// Items
	private static final int Tissue1 = 13836;
	private static final int Tissue2 = 13837;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public _10279_MutatedKaneusOren()
	{
		super(PARTY_ALL);
		addStartNpc(Mouen);
		addTalkId(Rovia);
		addKillId(KaimAbigore, KnightMontagnar);
		addQuestItem(Tissue1, Tissue2);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30196-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30189-02.htm"))
		{
			st.giveItems(57, 100000);
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
			if(npcId == Mouen)
				htmltext = "30196-0a.htm";
		}
		else if(id == CREATED && npcId == Mouen)
		{
			if(st.getPlayer().getLevel() >= 48)
				htmltext = "30196-01.htm";
			else
				htmltext = "30196-00.htm";
		}
		else
		{
			if(npcId == Mouen)
			{
				if(cond == 1)
					htmltext = "30196-04.htm";
				else if(cond == 2)
					htmltext = "30196-05.htm";
			}
			else if(npcId == Rovia)
			{
				if(cond == 1)
					htmltext = "30189-01a.htm";
				else if(cond == 2)
					htmltext = "30189-01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() == STARTED && st.getCond() == 1)
		{
			if((npc.getNpcId() == KaimAbigore) && (st.getQuestItemsCount(Tissue1) == 0))
			{
				st.giveItems(Tissue1, 1);
				st.playSound("ItemSound.quest_itemget");
			}
			else if((npc.getNpcId() == KnightMontagnar) && (st.getQuestItemsCount(Tissue2) == 0))
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