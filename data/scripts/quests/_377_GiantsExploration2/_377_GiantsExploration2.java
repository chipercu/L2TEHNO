package quests._377_GiantsExploration2;

import java.io.File;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
*@author Drizzy
*@version Gracia Epilogue
*/

public class _377_GiantsExploration2 extends Quest implements ScriptFile
{
	public void onLoad()
	{
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int Sobling = 31147;
	private static final int TitanBook = 14847;
	private static final int DropRate = 5;
	private static final int[] Mobs = { 22661,22662,22663,22664,22665,22666,22667,22668,22669 };
	private static final int BookPart1 = 14842;
	private static final int BookPart2 = 14843;
	private static final int BookPart3 = 14844;
	private static final int BookPart4 = 14845;
	private static final int BookPart5 = 14846;
	private static final int MysticBook = 5890;
	private static final int oblivion = 9625;
	private static final int discipline = 9626;
	private static final int adamant = 9629;
	private static final int leonard = 9628;
	private static final int orihalcum = 9630;

	public _377_GiantsExploration2()
	{
		super(true);
		addStartNpc(Sobling);
		addTalkId(Sobling);
		addKillId(Mobs);
		addQuestItem(TitanBook);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getInt("cond");

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
			st.takeItems(TitanBook, -1);
			st.takeItems(BookPart1, -1);
			st.takeItems(BookPart2, -1);
			st.takeItems(BookPart3, -1);
			st.takeItems(BookPart4, -1);
			st.takeItems(BookPart5, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		
		//More Hunting
		if(event.equalsIgnoreCase("31147-cont"))
		{
			st.unset("cond");
		}
		
		// Reward
		if(event.equalsIgnoreCase("9625"))  // Giant's Codex - Oblivion
	    {
			if(st.getQuestItemsCount(BookPart1) >= 5 && st.getQuestItemsCount(BookPart2) >= 5 && st.getQuestItemsCount(BookPart3) >= 5 && st.getQuestItemsCount(BookPart4) >= 5 && st.getQuestItemsCount(BookPart5) >= 5)
			{
				st.takeItems(BookPart1, 5);
				st.takeItems(BookPart2, 5);
				st.takeItems(BookPart3, 5);
				st.takeItems(BookPart4, 5);
				st.takeItems(BookPart5, 5);
				st.giveItems(oblivion, 1);
				htmltext = "31147-04a.htm";
			}
			else
				htmltext = "31147-02a.htm";
			
		}

		if(event.equalsIgnoreCase("9626"))  // Giant's Codex - Discipline
	    {
			if(st.getQuestItemsCount(BookPart1) >= 5 && st.getQuestItemsCount(BookPart2) >= 5 && st.getQuestItemsCount(BookPart3) >= 5 && st.getQuestItemsCount(BookPart4) >= 5 && st.getQuestItemsCount(BookPart5) >= 5)
			{
				st.takeItems(BookPart1, 5);
				st.takeItems(BookPart2, 5);
				st.takeItems(BookPart3, 5);
				st.takeItems(BookPart4, 5);
				st.takeItems(BookPart5, 5);
				st.giveItems(discipline, 1);
				htmltext = "31147-04a.htm";
			}
			else
				htmltext = "31147-02a.htm";
		}

		if(event.equalsIgnoreCase("9627")) // Adamantin
		{
			if(st.getQuestItemsCount(BookPart1) > 0 && st.getQuestItemsCount(BookPart2) > 0 && st.getQuestItemsCount(BookPart3) > 0 && st.getQuestItemsCount(BookPart4) > 0 && st.getQuestItemsCount(BookPart5) > 0)
			{
				st.takeItems(BookPart1, 1);
				st.takeItems(BookPart2, 1);
				st.takeItems(BookPart3, 1);
				st.takeItems(BookPart4, 1);
				st.takeItems(BookPart5, 1);
				st.giveItems(adamant, 3);
				htmltext = "31147-04b.htm";
			}
			else
				htmltext = "31147-02a.htm";
		}

		if(event.equalsIgnoreCase("9628")) // Leonard
		{
			if(st.getQuestItemsCount(BookPart1) > 0 && st.getQuestItemsCount(BookPart2) > 0 && st.getQuestItemsCount(BookPart3) > 0 && st.getQuestItemsCount(BookPart4) > 0 && st.getQuestItemsCount(BookPart5) > 0)
			{
				st.takeItems(BookPart1, 1);
				st.takeItems(BookPart2, 1);
				st.takeItems(BookPart3, 1);
				st.takeItems(BookPart4, 1);
				st.takeItems(BookPart5, 1);
				st.giveItems(leonard, 6);
				htmltext = "31147-04b.htm";
			}
			else
				htmltext = "31147-02a.htm";
		}

		if(event.equalsIgnoreCase("9629")) // Orihalcum
		{
			if(st.getQuestItemsCount(BookPart1) > 0 && st.getQuestItemsCount(BookPart2) > 0 && st.getQuestItemsCount(BookPart3) > 0 && st.getQuestItemsCount(BookPart4) > 0 && st.getQuestItemsCount(BookPart5) > 0)
			{
				st.takeItems(BookPart1, 1);
				st.takeItems(BookPart2, 1);
				st.takeItems(BookPart3, 1);
				st.takeItems(BookPart4, 1);
				st.takeItems(BookPart5, 1);
				st.giveItems(orihalcum, 4);
				htmltext = "31147-04b.htm";
			}
			else
				htmltext = "31147-02a.htm";
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
				if(st.getPlayer().getLevel() >= 79 && st.getQuestItemsCount(MysticBook) >= 1)
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
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() == STARTED)
			st.rollAndGive(TitanBook, 1, DropRate);
		return null;
	}
}