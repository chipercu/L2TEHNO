package quests._287_FiguringItOut;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * @author Drizzy
 * @date 10.01.11
 */
 
public class _287_FiguringItOut extends Quest implements ScriptFile
{
	// NPC
	private static final int LAKI = 32742;
	// MOB
	private static final int MOB[] = { 22768, 22769, 22770, 22771, 22772, 22773, 22774 };
	// ITEMS
	private static final int BLOOD = 15499;
	private static final int REWARD[] = { 15646, 15649, 15652, 15655, 15658, 15772, 15773, 15774, 15776, 15779, 15782, 15785, 15788, 15812, 15813, 15814 };
	private static final int REWARDCOUNT[] = { 5, 5, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	private static final int REWARD1[] = { 10381, 10405 };
	private static final int REWARD1COUNT[] = { 1, 4 }; // TODO нормальное кол-во награды. для 1го айди 1 шт, для 2го (1, 2, 4) рандом.
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
	
	public _287_FiguringItOut()
	{
		super(false);	
		addStartNpc(LAKI);
		for(int npcId : MOB)
			addKillId(npcId);
		addQuestItem(BLOOD);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;	
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		if(npcId != LAKI)
			return event;
			
		if(id == CREATED)
			if(cond == 0)
				if (event.equalsIgnoreCase("32742-03.htm"))
				{
					st.setState(STARTED);
					st.set("cond", "1");
					st.playSound(SOUND_ACCEPT);
				}		
		if(id == STARTED)
		{
			if(cond == 1)
				if (event.equalsIgnoreCase("32742-arm.htm"))
				{
					if(st.getQuestItemsCount(BLOOD) < 100)
						return "32742-noitem.htm";
					st.takeItems(BLOOD, 100);
					int j = Rnd.get(REWARD.length);
					st.giveItems(REWARD[j], REWARDCOUNT[j]);
					return "32742-arm.htm";				
				}
				if (event.equalsIgnoreCase("32742-bow.htm"))
				{
					if(st.getQuestItemsCount(BLOOD) < 500)
						return "32742-noitem2.htm";
					st.takeItems(BLOOD, 500);
					int j = Rnd.get(REWARD1.length);
					st.giveItems(REWARD1[j], REWARD1COUNT[j]);
					return "32742-bow.htm";					
				}	
				if(event.equalsIgnoreCase("32742-quit.htm"))
				{
					st.takeItems(BLOOD, -1);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
					htmltext = "32742-quit.htm";
				}
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
		if(npcId != LAKI)
			return htmltext;
		
		if(id == CREATED)
			if (st.getPlayer().getLevel() >= 82)
				htmltext = "32742-01.htm";
			else
			{
				htmltext = "32742-00.htm";
				st.exitCurrentQuest(true);
			}	
		if(id == STARTED)
			if(cond == 1)
				if(st.getQuestItemsCount(BLOOD) < 100)
					htmltext = "32742-noitem.htm";
				else
					htmltext = "32742-reward.htm";
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");	
		
		if(cond == 1)
		{
			if(arrayContains(MOB, npcId))
			{
				if(Rnd.chance(80))
				{
					st.giveItems(BLOOD, (int)ConfigValue.RateQuestsDrop, false);
					st.playSound(SOUND_ITEMGET);
				}
			}			
		}
		return null;
	}

	private boolean arrayContains(int[] array, int id)
	{
		for(int i : array)
			if(i == id)
				return true;
		return false;
	}	
}	