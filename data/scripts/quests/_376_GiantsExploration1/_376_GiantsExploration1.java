package quests._376_GiantsExploration1;

import java.io.File;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _376_GiantsExploration1 extends Quest implements ScriptFile
{
	public void onLoad()
	{
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
    
	private static final int Sobling = 31147;
	private static final int AncientParchment = 14841;
	private static final int DropRate = 5;
	private static final int MysticBook = 5890;
	private static final int DropRateMystic = 1;
	private static final int[] Mobs = { 22670,22671,22672,22673,22674,22675,22676,22677 };
	private static final int BookPart1 = 14836;
	private static final int BookPart2 = 14837;
	private static final int BookPart3 = 14838;
	private static final int BookPart4 = 14839;
	private static final int BookPart5 = 14840;
	
	public _376_GiantsExploration1()
	{
		super(true);
		addStartNpc(Sobling);
		addTalkId(Sobling);
		addKillId(Mobs);
		addQuestItem(AncientParchment);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getInt("cond");
		
		if(event.equalsIgnoreCase("31147-02.htm") && cond == 0)
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		
		//Start Quest
        if(event.equalsIgnoreCase("31147-02.htm") && cond == 0)
        {
            st.set("cond", "1");
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
		
		//Quest Quit		
		if(event.equalsIgnoreCase("31147-quit.htm"))
		{
			st.unset("cond");
			st.takeItems(AncientParchment, -1);
			st.takeItems(BookPart1, -1);
			st.takeItems(BookPart2, -1);
			st.takeItems(BookPart3, -1);
			st.takeItems(BookPart4, -1);
			st.takeItems(BookPart5, -1);
			st.exitCurrentQuest(true);
		}
		
		//More Hunting
		if(event.equalsIgnoreCase("31147-cont"))
		{
			st.unset("cond");
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		
		if(npcId == Sobling)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 79)
					htmltext = "31147-01.htm";
				else
					htmltext = "31147-00.htm";

		    if(cond == 1)
				if(st.getQuestItemsCount(BookPart1) > 0 && st.getQuestItemsCount(BookPart2) > 0 && st.getQuestItemsCount(BookPart3) > 0 && st.getQuestItemsCount(BookPart4) > 0 && st.getQuestItemsCount(BookPart5) > 0)
					htmltext = "31147-03.htm";
				else
					htmltext = "31147-02a.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() == STARTED)
		{
			st.rollAndGive(AncientParchment, 1, DropRate);
			if(st.getQuestItemsCount(MysticBook) < 1)
				st.rollAndGive(MysticBook, 1, DropRateMystic);
		}
		return null;
	}
}