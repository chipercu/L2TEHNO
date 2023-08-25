package quests._146_TheZeroHour;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._109_InSearchOfTheNest._109_InSearchOfTheNest;


public class _146_TheZeroHour extends Quest implements ScriptFile
{
	private static final int KAHMAN = 31554;
	private static final int FANG = 14859;	
	private static final int Reward =  14849;
	private static final int QUEEN = 25671;
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _146_TheZeroHour()
	{
		super(true);
		addStartNpc(KAHMAN);
		addKillId(QUEEN);
		addQuestItem(FANG);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int cond = st.getInt("cond");	
		String htmltext = event;
		
		if(event.equals("31554-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("reward"))
		{
			if(st.getQuestItemsCount(FANG) >= 1)
			{
				htmltext = "31554-06.htm";
				st.takeItems(FANG, 1);
				st.giveItems(Reward, 1);
			}
			else
				htmltext = "Not enough fang!";	
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();

		QuestState InSearchOfTheNest = st.getPlayer().getQuestState(_109_InSearchOfTheNest.class);
		if(npcId == KAHMAN)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 81)
				{
					if(InSearchOfTheNest != null && InSearchOfTheNest.isCompleted())
						htmltext = "31554-01.htm";
					else
						htmltext = "31554-00.htm";
				}
				else
					htmltext = "31554-03.htm";
			}
			else if(cond == 1)
				htmltext = "31554-04.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		
		if(npcId == QUEEN && cond >= 1)
		{
			st.giveItems(FANG, (int)ConfigValue.RateQuestsDrop);	
			st.playSound(SOUND_ITEMGET);	
		}
		return null;		
	}
}