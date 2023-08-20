package quests._10271_TheEnvelopingDarkness;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _10271_TheEnvelopingDarkness extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int Orbyu = 32560;
	private static final int El = 32556;
	private static final int Medibal  = 32528;
	private static final int Document  = 13852;
	
	public _10271_TheEnvelopingDarkness()
	{
		super(true);
		addStartNpc(Orbyu);
		addTalkId(Orbyu);	
		addTalkId(El);	
		addTalkId(Medibal);			
		addQuestItem(Document);
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
			st.setCond(4);
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
		
		if(npcId == Orbyu)
		{
			if(id == COMPLETED)
				htmltext = "This quest is completed!";	
			else if (cond == 0)
			{
				if(player.getLevel() >= 75)
				{
					htmltext = "32560-01.htm";	
				}
				else
					htmltext = "32560-00.htm";
					st.exitCurrentQuest(true);
			}
			else if(cond >= 1 && cond <= 3)
			{
				htmltext = "32560-03.htm";
			}
			else if(cond == 4 && st.getQuestItemsCount(Document) != 0)
			{
                st.takeItems(Document, 1);
                st.giveItems(57, 62516);
                st.addExpAndSp(377403, 37867);
                st.playSound(SOUND_FINISH);
                st.unset("cond");
                st.exitCurrentQuest(false);
                htmltext = "32560-04.htm";
				st.setState(COMPLETED);
			}			
				
		}
		if(npcId == El)
		{
            if(cond == 1)
                htmltext = "32556-01.htm";
			else if(cond == 2)
                htmltext = "32556-03.htm";
            else if(cond == 3)
                htmltext = "32556-04.htm";
            else if(cond == 4)
				htmltext = "32556-06.htm";
		}
		if(npcId == Medibal)
		{
            if(cond == 2)
            {
                st.set("cond", "3");
                st.giveItems(Document, 1);
                st.playSound(SOUND_MIDDLE);
                htmltext = "32528-01.htm";
			}	
            else
                htmltext = "32528-02.htm";
            
		}	
		return htmltext;
	}
}