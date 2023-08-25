package quests._626_ADarkTwilight;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _626_ADarkTwilight extends Quest implements ScriptFile
{
	//NPC
	private static final int Hierarch = 31517;
	//QuestItem
	private static int BloodOfSaint = 7169;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _626_ADarkTwilight()
	{
		super(true);
		addStartNpc(Hierarch);
		for(int npcId = 21520; npcId <= 21542; npcId++)
			addKillId(npcId);
		addQuestItem(BloodOfSaint);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("dark_presbyter_q0626_0104.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("dark_presbyter_q0626_0201.htm"))
		{
			if(st.getQuestItemsCount(BloodOfSaint) < 300)
				htmltext = "dark_presbyter_q0626_0203.htm";
		}
		else if(event.equalsIgnoreCase("rew_exp"))
		{
			if(st.getQuestItemsCount(BloodOfSaint) >= 300)
			{
				st.takeItems(BloodOfSaint, -1);
				st.addExpAndSp((int)(162773 * ConfigValue.RateQuestsRewardExpSp), (int)(12500 * ConfigValue.RateQuestsRewardExpSp), true);
				htmltext = "dark_presbyter_q0626_0202.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(event.equalsIgnoreCase("rew_adena"))
		{
			if(st.getQuestItemsCount(BloodOfSaint) >= 300)
			{
				st.takeItems(BloodOfSaint, -1);
				st.giveItems(ADENA_ID, (int)(100000 * ConfigValue.RateQuestsRewardAdena), true);
				htmltext = "dark_presbyter_q0626_0202.htm";
				st.exitCurrentQuest(true);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == Hierarch)
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() < 60)
				{
					htmltext = "dark_presbyter_q0626_0103.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "dark_presbyter_q0626_0101.htm";
			}
			else if(cond == 1)
				htmltext = "dark_presbyter_q0626_0106.htm";
			else if(cond == 2)
				htmltext = "dark_presbyter_q0626_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1 && Rnd.chance(70))
		{
			st.giveItems(BloodOfSaint, (int)ConfigValue.RateQuestsDrop);
			if(st.getQuestItemsCount(BloodOfSaint) == 300)
				st.set("cond", "2");
		}
		return null;
	}
}