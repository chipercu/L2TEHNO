package quests._10288_SecretMission;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 ** @author Gnacik
 **
 ** 2010-08-07 Based on Freya PTS
 */
public class _10288_SecretMission extends Quest implements ScriptFile
{
	// NPC's
	private static final int _dominic  = 31350;
	private static final int _aquilani = 32780;
	private static final int _greymore = 32757;
	// Items
	private static final int _letter = 15529;

	public _10288_SecretMission()
	{
		super(false);
		addStartNpc(_dominic);
		addTalkId(_greymore);
		addTalkId(_aquilani);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;

		if(st == null)
			return htmltext;
		if(npc.getNpcId() == _dominic)
		{
			if (event.equalsIgnoreCase("31350-05.htm"))
			{
				st.setState(STARTED);
				st.set("cond", "1");
				st.giveItems(_letter, 1);
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if(npc.getNpcId() == _greymore && event.equalsIgnoreCase("32757-03.htm"))
		{
			st.unset("cond");
			st.takeItems(_letter, -1);
			st.giveItems(57, 106583);
			st.addExpAndSp(417788, 46320);
			st.playSound("ItemSound.quest_finish");
			st.exitCurrentQuest(false);
		}
		else if(npc.getNpcId() == _aquilani)
		{
			if(st.getState() == STARTED)
			{
				if(event.equalsIgnoreCase("32780-05.htm"))
				{
					st.set("cond", "2");
					st.playSound("ItemSound.quest_middle");
				}
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(st == null)
			return htmltext;
		if(npc.getNpcId() == _dominic)
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.getPlayer().getLevel() >= 82)
						htmltext = "31350-01.htm";
					else
						htmltext = "31350-00.htm";
					break;
				case STARTED :
					if(st.getInt("cond") == 1)
						htmltext = "31350-06.htm";
					else if(st.getInt("cond") == 2)
						htmltext = "31350-07.htm";
					break;
				case COMPLETED :
					htmltext = "31350-08.htm";
					break;
			}
		}
		else if(npc.getNpcId() == _aquilani)
		{
			if(st.getInt("cond") == 1)
			{
				htmltext = "32780-03.htm";
			}
			else if(st.getInt("cond") == 2)
			{
				htmltext = "32780-06.htm";
			}
		}
		else if(npc.getNpcId() == _greymore && st.getInt("cond") == 2)
		{
			return "32757-01.htm";
		}
		return htmltext;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}