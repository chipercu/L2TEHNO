package quests._10287_StoryOfThoseLeft;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/*
 * @author GKR
 * 2011-04-24
 * work in progress
 */
public class _10287_StoryOfThoseLeft extends Quest implements ScriptFile
{
	private static final int _rafforty = 32020;
	private static final int _jinia = 32760;
	private static final int _kegor = 32761;

	public _10287_StoryOfThoseLeft()
	{
		super(false);

		addStartNpc(_rafforty);
		addTalkId(_rafforty);
		addTalkId(_jinia);
		addTalkId(_kegor);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;

		if(st == null)
			return htmltext;
		if(npc.getNpcId() == _rafforty)
		{
			if(event.equalsIgnoreCase("32020-04.htm"))
			{
				st.setState(STARTED);
				st.set("cond", "1");
				st.set("progress", "1");
				st.set("Ex1", "0");
				st.set("Ex2", "0");
				st.playSound("ItemSound.quest_accept");
			}
			else if(event.startsWith("reward_") && st.getInt("progress") == 2)
			{
				try
				{
					int itemId = Integer.parseInt(event.substring(7));
					if((itemId >= 10549 && itemId <= 10553) || itemId == 14219)
						st.giveItems(itemId, 1);
					st.playSound("ItemSound.quest_finished");
					st.exitCurrentQuest(false);
					htmltext = "32020-11.htm";
				}
				catch (Exception e)
				{
				}
			}
		}
		else if(npc.getNpcId() == _jinia)
		{
			if(event.equalsIgnoreCase("32760-03.htm") && st.getInt("progress") == 1 && st.getInt("Ex1") == 0)
			{
				st.set("Ex1", "1");
				st.set("cond", "3");
				st.playSound("ItemSound.quest_middle");
			}
		}
		else if(npc.getNpcId() == _kegor)
		{
			if (event.equalsIgnoreCase("32761-04.htm") && st.getInt("progress") == 1 && st.getInt("Ex1") == 1 && st.getInt("Ex2") == 0)
			{
				st.set("Ex2", "1");
				st.set("cond", "4");
				st.playSound("ItemSound.quest_middle");
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
					QuestState _prev = st.getPlayer().getQuestState("_10286_ReunionWithSirra");
					if(_prev != null && _prev.getState() == COMPLETED && st.getPlayer().getLevel() >= 82)
						htmltext = "32020-01.htm";
					else
						htmltext = "32020-03.htm";
					break;
				case STARTED:
					if(st.getInt("progress") == 1)
						htmltext = "32020-05.htm";
					else if(st.getInt("progress") == 2)
						htmltext = "32020-09.htm";
					break;
				case COMPLETED:
					htmltext = "32020-02.htm";
					break;
			}
		}
		else if(npc.getNpcId() == _jinia && st.getInt("progress") == 1)
		{
			if(st.getInt("Ex1") == 0)
				return "32760-01.htm";
			else if(st.getInt("Ex1") == 1 && st.getInt("Ex2") == 0)
				return "32760-04.htm"; 
			else if(st.getInt("Ex1") == 1 && st.getInt("Ex2") == 1)
			{
				st.set("cond", "5");
				st.playSound("ItemSound.quest_middle");
				st.set("progress", "2");
				st.set("Ex1", "0");
				st.set("Ex2", "0");
				//
				return "32760-05.htm";
			} 
		}
		else if(npc.getNpcId() == _kegor && st.getInt("progress") == 1)
		{
			if(st.getInt("Ex1") == 1 && st.getInt("Ex2") == 0)
				htmltext = "32761-01.htm";
			else if(st.getInt("Ex1") == 0 && st.getInt("Ex2") == 0)
				htmltext = "32761-02.htm";
			else if(st.getInt("Ex2") == 1)
				htmltext = "32761-05.htm";
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