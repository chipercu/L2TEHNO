package quests._182_NewRecruits;

import l2open.gameserver.ai.CtrlIntention;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
public class _182_NewRecruits extends Quest implements ScriptFile
{
	// NPCs
	private static int KEKROPUS = 32138;
	private static int MACHINE = 32258;
	// ITEMS
	private int CONDITION = 0;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}	
	public _182_NewRecruits()
	{
		super(false);

		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS);
		addTalkId(MACHINE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32138-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32258-03.htm"))
		{
			CONDITION = 1;
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32258-04.htm"))
		{
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			st.giveItems(890,2);
			
		}		
		else if(event.equalsIgnoreCase("32258-05.htm"))
		{
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			st.giveItems(847,2);
			
		}		
		else if(event.equalsIgnoreCase("32258-06.htm"))
		{
			player.getReflection().startCollapseTimer(5000);
			
		}			
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		L2Player player = st.getPlayer();
		if(npcId == KEKROPUS)
		{
			if(id == COMPLETED)
			{
				
				return "This quest has already been completed.";	
			}		
			else if(id == CREATED)
			{
				if(player.getRace() == Race.kamael)
					return "32138-00.htm";
				if(player.getLevel() > 20 || player.getLevel() <18)
				{
					st.exitCurrentQuest(true);
					return "32138-00.htm";
				}	
				else
					return "32138-01.htm";
			}		
			if(cond == 1)
				return "32138-04.htm";
				
					
		}
		else if(npcId == MACHINE)	
		{
			if(CONDITION == 0)
			{
				if(cond == 1)
					return "32258-01.htm";
			}
			else if(CONDITION == 1)
			{
				if(cond == 1)
					return "32258-03.htm";
			}
			if(id == COMPLETED)
				return "32258-05.htm";
		}		
		return "noquest";
	}
}