package quests._452_FindingtheLostSoldiers;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _452_FindingtheLostSoldiers extends Quest implements ScriptFile
{
	private static final int JAKAN = 32773;
	private static final int TAG_ID = 15513;
	private static final int[] SOLDIER_CORPSES = { 32769, 32770, 32771, 32772 };

	public _452_FindingtheLostSoldiers()
	{
		super(false);	
		addQuestItem(TAG_ID);
		addStartNpc(JAKAN);
		for(int i : SOLDIER_CORPSES)
			addTalkId(i);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		
		if (npc.getNpcId() == JAKAN)
		{
			if (event.equalsIgnoreCase("32773-3.htm"))
			{
				st.setState(STARTED);
				st.set("cond", "1");
				st.playSound(SOUND_ACCEPT);
			}
		}
		else if (arrayContains(SOLDIER_CORPSES, npc.getNpcId()))
		{
			if (st.getInt("cond") == 1)
			{
				st.giveItems(TAG_ID, 1);
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
				npc.deleteMe();
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		
		if (npcId == JAKAN)
		{
			if(id == CREATED)
            {
				if(!st.isNowAvailable())
					return "32773-6.htm";
				else
				{
					if (st.getPlayer().getLevel() >= 84)
						return "32773-1.htm";
					else
						return "32773-0.htm";
				}
            }
			if(id == STARTED)
				if (st.getInt("cond") == 1)
					return "32773-4.htm";
				else if (st.getInt("cond") == 2)
				{
					st.unset("cond");
					st.takeItems(TAG_ID, 1);
					st.giveItems(57, 95200);
					st.addExpAndSp(435024, 50366);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
					return "32773-5.htm";
				}
		}
		else if (arrayContains(SOLDIER_CORPSES, npc.getNpcId()))
		{
			if (st.getInt("cond") == 1)
				return "corpse-1.htm";
		}
		return "noquest";
	}

	private boolean arrayContains(int[] array, int id)
	{
		for(int i : array)
			if(i == id)
				return true;
		return false;
	}
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}		
}