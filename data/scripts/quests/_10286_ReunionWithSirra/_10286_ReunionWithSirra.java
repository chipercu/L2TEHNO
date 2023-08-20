package quests._10286_ReunionWithSirra;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Location;

/*
 * @author GKR
 * 2011-04-24
 * work in progress
 */
public class _10286_ReunionWithSirra extends Quest implements ScriptFile
{
	// NPC's
	private static final int _rafforty = 32020;
	private static final int _jinia = 32760;
	private static final int _sirra = 32762;
	private static final int _jinia2 = 32781;
	
	private static final int _blackCore = 15470;

	public _10286_ReunionWithSirra()
	{
		super(false);

		addStartNpc(_rafforty);
		addTalkId(_rafforty);
		addFirstTalkId(_rafforty);
		addTalkId(_jinia);
		addTalkId(_jinia2);
		addTalkId(_sirra);
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
				st.playSound("ItemSound.quest_accept");
			}
			else if(event.equalsIgnoreCase("32020-05.htm") && st.getInt("progress") == 1)
				st.set("Ex", "0");
		}
		
		else if(npc.getNpcId() == _jinia)
		{
			if(event.equalsIgnoreCase("32760-06.htm"))
			{
				addSpawnToInstance(_sirra, new Location(-23905, -8790, -5384, 56238), 0, npc.getReflection().getId());
				st.set("Ex", "1");
				st.set("cond", "3");
				st.playSound("ItemSound.quest_middle");
				htmltext = "";
			}
			else if(event.equalsIgnoreCase("32760-09.htm") && st.getInt("progress") == 1 && st.getInt("Ex") == 2)
			{
				st.set("progress", "2");
			}
		}

		else if(npc.getNpcId() == _sirra)
		{
			if(event.equalsIgnoreCase("32762-04.htm") && st.getInt("progress") == 1 && st.getInt("Ex") == 1)
			{
				if(st.getQuestItemsCount(_blackCore) == 0)
					st.giveItems(_blackCore, 5);

				st.set("Ex", "2");
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
					QuestState _prev = st.getPlayer().getQuestState("_10285_MeetingSirra");
					if (_prev != null && _prev.getState() == COMPLETED && st.getPlayer().getLevel() >= 82)
						htmltext = "32020-01.htm";
					else
						htmltext = "32020-03.htm";
					break;
				case STARTED:
					if (st.getInt("progress") == 1)
						htmltext = "32020-06.htm";
					else if (st.getInt("progress") == 2)
						htmltext = "32020-09.htm";
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
					return "32760-07.htm";
				case 2:
					return "32760-08.htm";
			}
		}
		else if(npc.getNpcId() == _sirra && st.getInt("progress") == 1)
		{
			switch(st.getInt("Ex"))
			{
				case 1:
					return "32762-01.htm";
				case 2:
					return "32762-05.htm";
			}
		}
		else if(npc.getNpcId() == _jinia2 && st.getInt("progress") == 2)
			htmltext = "32781-01.htm";
		else if(npc.getNpcId() == _jinia2 && st.getInt("progress") == 3)
		{
			st.addExpAndSp(2152200, 181070);
			st.playSound("ItemSound.quest_finish");
			st.exitCurrentQuest(false); 
			return "32781-08.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState st = player.getQuestState("_10286_ReunionWithSirra");
		QuestState _prev = player.getQuestState("_10285_MeetingSirra");

		if(npc.getNpcId() == _rafforty && _prev != null && _prev.getState() == COMPLETED && st == null && player.getLevel() >= 82)
			npc.showChatWindow(player, "data/html/default/repre003.htm");
		else
			npc.showChatWindow(player, 0); // Нужно проверить...
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}