package quests._288_HandleWithCare;

import ai.PlainsOfLizardmen.ai_tantaar_ugoros;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * @author Drizzy
 * @date 16.05.11
 */
 
public class _288_HandleWithCare extends Quest implements ScriptFile
{
	//NPC
	private static final int ANKUMI = 32741;
	//MOB
	private static final int RB = 18863;
	//ITEMS
	private static final int HGLS = 15497;
	private static final int MGLS = 15498;
	private static final int [] reward = { 959, 960 };
	private static final int [] reward2 = { 9557, 959, 960 };
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
	
	public _288_HandleWithCare()
	{
		super(false);		
		addStartNpc(ANKUMI);
		addKillId(RB);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;	
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		if(npcId != ANKUMI)
			return event;
			
		if(id == CREATED)
		{
			if(cond == 0)
			{
				if (event.equalsIgnoreCase("angkumi_q0288_04.htm"))
				{
					st.setState(STARTED);
					st.set("cond", "1");
					st.playSound(SOUND_ACCEPT);
				}
			}
		}
		if(id == STARTED)
		{
			if (event.equalsIgnoreCase("angkumi_q0288_08.htm"))
			{
				if(cond == 3)
				{
					if(st.getQuestItemsCount(HGLS) >= 1)
					{
						st.giveItems(9557, 1);
						int j = Rnd.get(reward.length);
						if(j == 0)
							st.giveItems(959, 1);
						if(j == 1)
							st.giveItems(960, Rnd.get(1,3));
						st.takeItems(HGLS, -1);
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(true);
						return "angkumi_q0288_08.htm";
					}
				}
				if(cond == 2)
				{
					if(st.getQuestItemsCount(MGLS) >= 1)
					{
						int j = Rnd.get(reward2.length);
						if(j == 1)
							st.giveItems(959, 1);
						if(j == 2)
							st.giveItems(960, Rnd.get(1,3));
						if(j == 0)
							st.giveItems(9557, 1);
						st.takeItems(MGLS, -1);
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(true);
						return "angkumi_q0288_08.htm";
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
		if(npcId != ANKUMI)
			return htmltext;
		
		if(id == CREATED)
		{
			if (st.getPlayer().getLevel() >= 82)
			{
				htmltext = "angkumi_q0288_02.htm";
			}
			else
			{
				htmltext = "angkumi_q0288_01.htm";
				st.exitCurrentQuest(true);
			}
		}
		if(id == STARTED)
		{
			if(cond == 1)
			{
				htmltext = "angkumi_q0288_05.htm";
			}
			if(cond == 3)
			{
				if(st.getQuestItemsCount(HGLS) >= 1)
				{
					htmltext = "angkumi_q0288_07.htm";
				}
			}
			if(cond == 2)
			{
				if(st.getQuestItemsCount(MGLS) >= 1)
				{
					htmltext = "angkumi_q0288_06.htm";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(cond == 1)
		{
			if(npcId == RB)
			{
				if(ai_tantaar_ugoros.get_i_quest4() == 1)
				{
				    st.giveItems(MGLS, 1);
					st.set("cond", "2");
				}
				if(ai_tantaar_ugoros.get_i_quest4() == 0)
				{
					st.giveItems(HGLS, 1);
					st.set("cond", "3");
				}
			}
		}
		return null;
	}
}