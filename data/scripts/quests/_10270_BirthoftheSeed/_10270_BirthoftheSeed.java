package quests._10270_BirthoftheSeed;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.model.L2Player;
import l2open.util.Rnd;

public class _10270_BirthoftheSeed extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
    //NPC
	private static final int Plenos = 32563; //'-186692', '243539', '2608'
	private static final int Artius  = 32559; //'-186109', '242500', '2552'
	private static final int Ginby  = 32566; //'-185090', '242809', '1576'
	private static final int Lelrikia = 32567; //'-24040', '-8968', '-5360'
	//Mobs
	private static final int Klodekus = 25665;
	private static final int Klanikus = 25666;
	private static final int Cohemenes = 25634;
	//items
	private static final int ZnakKlod  = 13868;
	private static final int ZnakKlanik  = 13869;
	private static final int Kristal  = 13870;
	
	public _10270_BirthoftheSeed()
	{
		super(true);
		addStartNpc(Plenos);
		addTalkId(Plenos);	
		addTalkId(Artius);	
		addTalkId(Ginby);
		addTalkId(Lelrikia);
        addKillId(Cohemenes); // Cohemenes
		addKillId(Klodekus); // Yehan Klodekus
        addKillId(Klanikus); // Yehan Klanikus	
		addQuestItem(ZnakKlod);
		addQuestItem(ZnakKlanik);
		addQuestItem(Kristal);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32560-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("32556-02.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}	
		if(event.equalsIgnoreCase("32556-05.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}			
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == Plenos)
		{
			if(id == COMPLETED)
				htmltext = "This quest is completed!";	
			else if (cond == 0)
			{
				if(player.getLevel() >= 75)
				{
					htmltext = "32560-01.htm";	// начало => сказать что вы готовы
				}
				else
					htmltext = "32560-00.htm";  // не подходит по лвлу
					st.exitCurrentQuest(true);
			}
			else if(cond >= 1 && cond <= 2)
			{
				htmltext = "32560-03.htm"; // вы ещё не ушли?
			}
							
		}
		if(npcId == Artius)
		{
            if(cond == 1)
                htmltext = "32556-01.htm";
		    else if(cond == 2)
                htmltext = "32556-04.htm";
			else if(cond == 3 && st.getQuestItemsCount(Kristal,ZnakKlanik,ZnakKlod) != 0)
			{
                st.takeItems(Kristal, 1);//забирает
				st.takeItems(ZnakKlanik, 1);// итемы
				st.takeItems(ZnakKlod, 1);// исправно
                st.playSound(SOUND_FINISH);
                st.unset("cond");
                st.exitCurrentQuest(false);
                htmltext = "32560-04.htm";
				st.setState(COMPLETED);
			}			
            else if(cond == 4)
				htmltext = "32556-06.htm";
		}
		 
		if(npcId == Ginby)
		{
			if(cond == 1)
			{
				htmltext = "32566-01.htm";
				st.setState(COMPLETED);
			}
		}
		if(npcId == Lelrikia)
		{
            if(cond == 1)
                htmltext = "32567-02.htm";
		    else if(cond == 2)
			{
                htmltext = "32567-03.htm";
				st.setState(COMPLETED);
			}
		}
		if(npcId == Artius)
		{		
		   if(cond == 1 && st.getQuestItemsCount(Kristal,ZnakKlanik,ZnakKlod) != 0)
			{
                st.giveItems(57, 41677);// даёт адену (57 это ид адены)
                st.addExpAndSp(251602, 25244); // дать EXP & SP
                st.playSound(SOUND_FINISH);
                st.unset("cond");
                st.exitCurrentQuest(false);
                htmltext = "32560-04.htm";
				st.setState(COMPLETED);
			}			
			return htmltext;
		}
		return htmltext;
		
	}
		
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
	
		if(npcId == Klodekus)
		{
			if(cond > 2 && st.getQuestItemsCount(ZnakKlod) > 1 && st.getQuestItemsCount(ZnakKlod) < 1 && Rnd.chance(100))
			{
				st.giveItems(ZnakKlod, 1);
				if(st.getQuestItemsCount(ZnakKlod) < 1)
					st.playSound(SOUND_ITEMGET);
				else
					st.playSound(SOUND_MIDDLE);
			}
		}
		else if(npcId == Klanikus)
		{
			if(cond > 2 && st.getQuestItemsCount(ZnakKlanik) > 1 && st.getQuestItemsCount(ZnakKlanik) < 1 && Rnd.chance(100))
			{
				st.giveItems(ZnakKlanik, 1);
				if(st.getQuestItemsCount(ZnakKlanik) < 1)
					st.playSound(SOUND_ITEMGET);
				else
					st.playSound(SOUND_MIDDLE);
			}
		}
		else if(npcId == Cohemenes)
		{
			if(cond > 2 && st.getQuestItemsCount(Kristal) > 1 && st.getQuestItemsCount(Kristal) < 1 && Rnd.chance(100))
			{
				st.giveItems(Kristal, 1);
				if(st.getQuestItemsCount(Kristal) < 1)
					st.playSound(SOUND_ITEMGET);
				else
					st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}
