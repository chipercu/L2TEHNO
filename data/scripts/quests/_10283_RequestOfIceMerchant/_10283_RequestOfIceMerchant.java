package quests._10283_RequestOfIceMerchant;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10283_RequestOfIceMerchant extends Quest implements ScriptFile
{
	private static final int rafforty = 32020;
	private static final int kier = 32022;
	private static final int jinia = 32760;
	
	public _10283_RequestOfIceMerchant()
	{
		super(false);
		
		addStartNpc(rafforty);
		addTalkId(kier);
		addTalkId(jinia);
		addFirstTalkId(jinia);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equals("32020-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("32020-07.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("spawn"))
		{
			st.addSpawn(jinia,104322,-107669,-3680,60000);
			return null;
		}
		else if(event.equalsIgnoreCase("32760-04.htm"))
		{
			st.giveItems(57, 190000);
			st.addExpAndSp(627000, 50300);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			npc.deleteMe();
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		
		if(npcId == rafforty)
		{
			if(id == CREATED)
			{
				QuestState qs = st.getPlayer().getQuestState("_115_TheOtherSideOfTruth");
				if(qs != null && qs.getState() == COMPLETED && st.getPlayer().getLevel() >= 53)
					return "32020-01.htm";
				else
					return "32020-00.htm";
			}
			if(id == STARTED)
			{
				if (st.getInt("cond") == 1)
					return "32020-04.htm";
				else if (st.getInt("cond") == 2)
					return "32020-08.htm";
			}
			if(id == COMPLETED)
			{
				return "31350-08.htm";
			}
		}
		if(npcId == kier)
			if(cond == 2)
				return "32022-01.htm";
		if(npcId == jinia)
			if(cond == 2)
				return "32760-02.htm";
		return "noquest";
	}
	
	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		if(st == null)
			return htmltext;
		if(npc.getNpcId() == jinia && st.getCond() == 2)
			return "32760-01.htm";
		return htmltext;
	}
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

}