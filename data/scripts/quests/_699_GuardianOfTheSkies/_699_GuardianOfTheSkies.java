package quests._699_GuardianOfTheSkies;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _699_GuardianOfTheSkies extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int LEKON = 32557;

	private static final int GOLDEN_FEATHER = 13871;
	

	private static int[] MOBS = new int[] {22614,22615,25623,25633};


	public _699_GuardianOfTheSkies()
	{
		super(true);
		addStartNpc(LEKON);
		addTalkId(LEKON);	
		addKillId(MOBS);
		addQuestItem(GOLDEN_FEATHER);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32557-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("32557-quit.htm"))
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
		
		if(npcId == LEKON)
		{
			if(qs != null && qs.isCompleted() && player.getLevel() >= 75 && id == CREATED)
				htmltext = "32557-01.htm";	
			else if (cond == 1)
			{
				long Golden_count = st.getQuestItemsCount(GOLDEN_FEATHER);
				if(Golden_count > 0)
				{
					st.takeItems(GOLDEN_FEATHER, -1);
					st.giveItems(57,Golden_count * 2300);	
					htmltext = "32557-06.htm";	
				}
				else
					htmltext = "32557-04.htm";
			}
			else if(cond == 0)
			{
				htmltext = "32557-00.htm";
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
			st.rollAndGive(GOLDEN_FEATHER, 1, 1, 80);
		return null;
	}
}