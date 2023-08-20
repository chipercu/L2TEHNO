package quests._10272_LightFragment;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _10272_LightFragment extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int Orbyu = 32560;
	private static final int Artius  = 32559;
	private static final int Ginby  = 32566;
	private static final int Lelikia   = 32567;
	private static final int Lekon  = 32557;
	private static final int Document   = 13852;
	private static final int Darkness_Fragment = 13853;
	private static final int Light_Fragment  = 13854;	
	private static final int Sacred_Fragment  = 13855;	
	

	private static int[] MOBS = new int[] {  22537, 22538, 22539, 22541, 22542, 22543, 22544, 22547, 22548, 22549, 22559, 22560, 22561, 22562, 22563, 22564, 22566, 22567};


	public _10272_LightFragment()
	{
		super(true);
		addStartNpc(Orbyu);
		addTalkId(Orbyu);	
		addTalkId(Artius);
		addTalkId(Ginby);
		addTalkId(Lelikia);
		addTalkId(Lekon);
		addKillId(MOBS);
		addQuestItem(Document);
		addQuestItem(Darkness_Fragment);
		addQuestItem(Light_Fragment);
		addQuestItem(Sacred_Fragment);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32560-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Document,1);
		}
		else if(event.equalsIgnoreCase("32559-03.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}	
		else if(event.equalsIgnoreCase("32559-06.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}		
		else if(event.equalsIgnoreCase("32559-09.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}	
		else if(event.equalsIgnoreCase("teleSecret"))
		{
			if(st.getQuestItemsCount(57) >= 10000)
			{
				st.takeItems(57,10000);
				player.teleToLocation(-23530,-8963,-5413);
				return null;
			}	
			else
				return "32566-02.htm";
		}
		else if(event.equalsIgnoreCase("32567-02.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}			
		else if(event.equalsIgnoreCase("teleBack"))
		{		
			player.teleToLocation(-185100,242809,1576);
			return null;
		}	
		else if(event.equalsIgnoreCase("32557-02.htm"))
		{
			st.setCond(8);
			st.takeItems(Light_Fragment,-1);
			st.giveItems(Sacred_Fragment,1);
			st.playSound(SOUND_MIDDLE);
		}	
		else if(event.equalsIgnoreCase("32559-12.htm"))
		{
			st.exitCurrentQuest(false);
			st.setState(COMPLETED);
			st.playSound(SOUND_FINISH);
			st.giveItems(57,556980);
			st.addExpAndSp(1009016, 91363);
		}		
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		int id = st.getState();
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == Orbyu)
		{
			if(id == COMPLETED)
				return "This quest is completed!";	
			else if (cond == 0)
			{
				QuestState qs = player.getQuestState("_10271_TheEnvelopingDarkness");
				if(qs == null || !qs.isCompleted())
					return "32560-00.htm";
				else if( id == CREATED && player.getLevel() >= 75)
				{
					st.exitCurrentQuest(true);
					return "32560-01.htm";
				}	
			}
			else if(cond == 1)
				return "32560-03.htm";
		}
		else if(npcId == Artius)
		{
            if(cond == 1 && st.getQuestItemsCount(Document) != 0)
			{
				st.takeItems(Document, 1);
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
                return "32559-01.htm";
			}
            else if(cond == 2)
                return "32559-02.htm";
            else if(cond == 3)
                return "32559-04.htm";
            else if(cond == 4)
                return "32559-05.htm";
            else if(cond == 5)
			{
				if(st.getQuestItemsCount(Darkness_Fragment) < 100)
					return "32559-07.htm";
				else
					return "32559-08.htm";
			}
            else if(cond == 6)
			{
				if(st.getQuestItemsCount(Light_Fragment) >= 100)
				{
					st.setCond(7);
					return "32559-10.htm";
				}	
				else
					return "32559-09.htm";
			}		
            else if(cond == 8)
                return "32559-11.htm";
		}
		else if(npcId == Ginby)
            if(cond == 3)
                return "32566-01.htm";
		else if(npcId == Lelikia)
            if(cond == 3)
                return "32567-01.htm";
		else if(npcId == Lekon && cond == 7 && st.getQuestItemsCount(Light_Fragment) >= 100)	
			return "32557-01.htm";

		return "noquest";
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");	
		if(contains(MOBS, npcId) && cond == 1)
			st.rollAndGive(Darkness_Fragment, 1, 1, 80); //TODO: Chance!
		return null;
	}
}