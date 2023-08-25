package quests._10282_ToTheSeedOfAnnihilation;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10282_ToTheSeedOfAnnihilation extends Quest implements ScriptFile
{
	private static final int KBALDIR = 32733;
	private static final int KLEMIS = 32734;
	private static final int SOA_ORDERS = 15512;

	public _10282_ToTheSeedOfAnnihilation()
	{
		super(false);	
		addStartNpc(KBALDIR);
		addTalkId(KLEMIS);
		addQuestItem(SOA_ORDERS);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equals("32733-07.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(SOA_ORDERS, 1);
		}
		if(event.equals("32734-02.htm"))
		{
			st.unset("cond");
			st.addExpAndSp(1148480,99110);
			st.takeItems(SOA_ORDERS, -1);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		
		if(npcId == KBALDIR)
		{
			if(id == CREATED)
			{
				if (st.getPlayer().getLevel() >= 84)
					return "32733-01.htm";
				else
					return "32733-00.htm";			
			}
			if(id == STARTED)
			{
				if(cond == 1)
					return "32733-08.htm";
			}
			if(id == COMPLETED)
			{
				return "32733-09.htm";
			}
		}
		if(npcId == KLEMIS)
		{
			if(id == STARTED)
			{
				if(cond == 1)
					return "32734-01.htm";
			}
			if(id == COMPLETED)
			{
				return "32734-03.htm";
			}
		}

		return "noquest";
	}
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}	
}