package quests._423_TakeYourBestShot;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
/**
 * @author Drizzy
 * @date 10.01.11
 */
 
public class _423_TakeYourBestShot extends Quest implements ScriptFile
{
	// NPCs
	private static final int JOHNNY = 32744;
	private static final int BATRAC = 32740;
	
	// Item
	private static final int PASS = 15496;
	
	// Mobs
	private static final int GUARD = 18862;
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
	
	public _423_TakeYourBestShot()
	{
		super(false);		
		addStartNpc(JOHNNY);
		addTalkId(BATRAC);
		addKillId(GUARD);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;	
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		if(id == CREATED)
		{
			if(cond == 0)
			{
				if(npcId == JOHNNY)
				{
					if (event.equalsIgnoreCase("32744-04.htm"))
					{
						st.setState(STARTED);
						st.set("cond", "1");
						st.playSound(SOUND_ACCEPT);
					}
				}
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
		if(npcId == JOHNNY)
		{
			if(id == COMPLETED)
				htmltext = "32744-done.htm";
			if(id == CREATED)
				if (st.getPlayer().getLevel() >= 82)
					htmltext = "32744-01.htm";
				else
				{
					htmltext = "32744-00.htm";
					st.exitCurrentQuest(true);
				}	
			if(id == STARTED)
				if(cond == 1)
					htmltext = "32744-already.htm";
				if(cond == 2)
					htmltext = "32744-already2.htm";
		}
		if(npcId == BATRAC)
		{
			if(id == STARTED)
				if(cond == 2)
				{
					st.giveItems(PASS, 1, false);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
					htmltext = "32740.htm";
				}
		}
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
			if(npcId == GUARD)
			{			
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}