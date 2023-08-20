package quests._701_Proof_Of_Existence;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _701_Proof_Of_Existence extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int ARTIUS = 32559;

	private static final int DEADMANS_REMAINS = 13875;
	private static final int QUEEN2 = 25625;
	private static final int EYE = 13876;
	

	private static int[] MOBS = new int[] {22606,22607,22608,22609};


	public _701_Proof_Of_Existence()
	{
		super(true);
		addStartNpc(ARTIUS);
		addTalkId(ARTIUS);	
		addKillId(MOBS);
		addKillId(QUEEN2);
		addQuestItem(DEADMANS_REMAINS);
		addQuestItem(EYE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32559-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("32559-quit.htm"))
		{
			st.exitCurrentQuest(true);
			st.unset("cond");
			st.playSound(SOUND_FINISH);
		}		
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		QuestState qs = player.getQuestState("_10273_GoodDayToFly");
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == ARTIUS)
		{
			if(qs != null && qs.isCompleted() && player.getLevel() >= 78 && id == CREATED)
				htmltext = "32559-01.htm";	
			else if (cond == 1)
			{
				long Deadman_remains = st.getQuestItemsCount(DEADMANS_REMAINS);
				if(Deadman_remains > 0 || st.getQuestItemsCount(EYE) > 0)
				{
					st.takeItems(DEADMANS_REMAINS, -1);
					st.giveItems(57,Deadman_remains * 2500);
					if(st.getQuestItemsCount(EYE) >= 1)
					{
						st.giveItems(57,5000000);
						st.takeItems(EYE,1);
						return "banshee.htm";
					}	
					else					
						htmltext = "32559-06.htm";	
				}
				else
					htmltext = "32559-04.htm";
			}
			else if(cond == 0)
			{
				htmltext = "32559-00.htm";
				st.exitCurrentQuest(true);
			}
				
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");	
		if(contains(MOBS, npcId) && cond == 1)
		{
			st.rollAndGive(DEADMANS_REMAINS, 1, 1, 80);
			int chance = Rnd.get(0, 100);
			if(chance >= 99)
			{
				Functions.spawn(player.getLoc().rnd(50, 100, false), QUEEN2);
				return null;
			}
			else
				return null;
		}
		if(npcId == QUEEN2)
			st.giveItems(EYE,1);
			
		return null;
	}
}