package quests._250_WatchWhatYouEat;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _250_WatchWhatYouEat extends Quest implements ScriptFile
{
	// NPCs
	private static final int SALLY = 32743;
	// Mobs - Items
	private static final int MOB[] = { 18864, 18865, 18868 };
	
	private static final int ITEM[] = { 15493, 15494, 15495 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
	
	public _250_WatchWhatYouEat()
	{
		super(false);		
		addStartNpc(SALLY);
		for(int npcId : MOB)
			addKillId(npcId);
		for(int itemId : ITEM)
			addKillId(itemId);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;	
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		
		if (npcId != SALLY)
			return event;
		
		if (event.equalsIgnoreCase("32743-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32743-end.htm"))
		{
			st.unset("cond");
			st.giveItems(57, 135661);
			st.addExpAndSp(698334, 76369);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("32743-22.html") && id == COMPLETED)
		{
			return "32743-23.html";
		}
		
		return event;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");
		
		if (npcId != SALLY)
			return htmltext;
		
		if(id == COMPLETED)
			htmltext = "32743-done.htm";
		if(id == CREATED)
			if (st.getPlayer().getLevel() >= 82)
				htmltext = "32743-01.htm";
			else
			{
				htmltext = "32743-00.htm";
				st.exitCurrentQuest(true);
			}
		if(id == STARTED)
			if(cond == 1)
				htmltext = "32743-04.htm";
			if(cond == 2)
				if(st.getQuestItemsCount(ITEM[0]) == 1 && st.getQuestItemsCount(ITEM[1]) == 1 && st.getQuestItemsCount(ITEM[2]) == 1)
				{
					htmltext = "32743-05.htm";
					st.takeItems(ITEM[0], -1);
					st.takeItems(ITEM[1], -1);
					st.takeItems(ITEM[2], -1);
				}
				else
					htmltext = "32743-06.htm";
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		if(id == STARTED && st.getInt("cond") == 1)
		{			
			if(npcId == MOB[0] && st.getQuestItemsCount(ITEM[0]) == 0)
			{
				st.giveItems(ITEM[0], 1, false);
				st.playSound(SOUND_ITEMGET);
			}
			if(npcId == MOB[1] && st.getQuestItemsCount(ITEM[1]) == 0)
			{
				st.giveItems(ITEM[1], 1, false);
				st.playSound(SOUND_ITEMGET);
			}
			if(npcId == MOB[2] && st.getQuestItemsCount(ITEM[2]) == 0)
			{
				st.giveItems(ITEM[2], 1, false);
				st.playSound(SOUND_ITEMGET);
			}	
			if(st.getQuestItemsCount(ITEM[0]) == 1 && st.getQuestItemsCount(ITEM[1]) == 1 && st.getQuestItemsCount(ITEM[2]) == 1)
			{
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
			}			
		}
		return null;
	}
}
