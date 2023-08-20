package quests._10285_MeetingSirra;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Location;

/*
 * @author GKR
 * 2011-04-24
 * work in progress
 */
public class _10285_MeetingSirra extends Quest implements ScriptFile
{
	// NPC's
	private static final int _rafforty = 32020;
	private static final int _steward = 32029;
	private static final int _jinia = 32760;
	private static final int _kegor = 32761;
	private static final int _sirra = 32762;
	private static final int _jinia2 = 32781;

	public _10285_MeetingSirra()
	{
		super(false);

		addStartNpc(_rafforty);
		addTalkId(_rafforty);
		addTalkId(_jinia);
		addTalkId(_jinia2);
		addTalkId(_kegor);
		addTalkId(_sirra);
		addTalkId(_steward);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(st == null)
			return htmltext;
		if(npc.getNpcId() == _rafforty)
		{
			if(event.equalsIgnoreCase("32020-05.htm"))
			{
				st.setState(STARTED);
				st.set("cond", "1");
				st.set("progress", "1");
				st.set("Ex", "0");
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if(npc.getNpcId() == _jinia)
		{
			if(event.equalsIgnoreCase("32760-02.htm"))
			{
				st.set("Ex", "1");
				st.set("cond", "3");
				st.playSound("ItemSound.quest_middle");
			}
			else if(event.equalsIgnoreCase("32760-06.htm"))
			{
				st.set("Ex", "3");
				addSpawnToInstance(_sirra, new Location(-23905, -8790, -5384, 56238), 0, npc.getReflection().getId());
				st.set("cond", "5");
				st.playSound("ItemSound.quest_middle");
				htmltext = "";
			}
			else if(event.equalsIgnoreCase("32760-12.htm"))
			{
				st.set("Ex", "5");
				st.set("cond", "7");
				st.playSound("ItemSound.quest_middle");
			}
			else if(event.equalsIgnoreCase("32760-14.htm"))
			{
				st.set("Ex", "0");
				st.set("progress", "2");
				st.playSound("ItemSound.quest_middle");
			}
		}
		else if(npc.getNpcId() == _kegor)
		{
			if(event.equalsIgnoreCase("32761-02.htm"))
			{
				st.set("Ex", "2");
				st.set("cond", "4");
				st.playSound("ItemSound.quest_middle");
			}
		}
		else if(npc.getNpcId() == _sirra)
		{
			if(event.equalsIgnoreCase("32762-08.htm"))
			{
				st.set("Ex", "4");
				st.set("cond", "6");
				st.playSound("ItemSound.quest_middle");
				npc.deleteMe();
			}
		}
		else if(npc.getNpcId() == _steward)
		{
			if(event.equalsIgnoreCase("go"))
			{
				if(st.getPlayer().getLevel() >= 82)
				{
					st.getPlayer().teleToLocation(103045,-124361,-2768);
					htmltext = "";
				}
				else
					htmltext = "32029-01a";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(st == null)
			return htmltext;
		if(npc.getNpcId() == _rafforty)
		{
			switch(st.getState())
			{
				case CREATED:
					QuestState _prev = st.getPlayer().getQuestState("_10284_AcquisitionOfDivineSword");
					if(_prev != null && _prev.getState() == COMPLETED && st.getPlayer().getLevel() >= 82)
						htmltext = "32020-01.htm";
					else
						htmltext = "32020-03.htm";
					break;
				case STARTED:
					if(st.getInt("progress") == 1)
						htmltext = "32020-06.htm";
					else if(st.getInt("progress") == 2)
						htmltext = "32020-09.htm";
					else if(st.getInt("progress") == 3)
					{
						st.giveItems(57, 283425);
						st.addExpAndSp(939075, 83855);
						st.playSound("ItemSound.quest_finish");
						st.exitCurrentQuest(false); 
						htmltext = "32020-10.htm";
					}
					break;
				case COMPLETED:
					htmltext = "32020-02.htm";
					break;
			}
		}
		else if(npc.getNpcId() == _jinia && st.getInt("progress") == 1)
		{
			switch(st.getInt("Ex"))
			{
				case 0:
					return "32760-01.htm";
				case 1:
					return "32760-03.htm";
				case 2:
					return "32760-04.htm";
				case 3:
					return "32760-07.htm";
				case 4:
					return "32760-08.htm";
				case 5:
					return "32760-13.htm";
			}
		}
		else if(npc.getNpcId() == _kegor && st.getInt("progress") == 1)
		{
			switch(st.getInt("Ex"))
			{
				case 1:
					return "32761-01.htm";
				case 2:
					return "32761-03.htm";
				case 3:
					return "32761-04.htm";
			}
		}
		else if(npc.getNpcId() == _sirra && st.getInt("progress") == 1)
		{
			switch(st.getInt("Ex"))
			{
				case 3:
					return "32762-01.htm";
				case 4:
					return "32762-09.htm";
			}
		}
		else if(npc.getNpcId() == _steward && st.getInt("progress") == 2)
		{
			htmltext = "32029-01.htm";
			st.set("cond", "8");
			st.playSound("ItemSound.quest_middle");
		}
		else if(npc.getNpcId() == _jinia2 && st.getInt("progress") == 2)
		{
			htmltext = "32781-01.htm";
			st.playSound("ItemSound.quest_middle");
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