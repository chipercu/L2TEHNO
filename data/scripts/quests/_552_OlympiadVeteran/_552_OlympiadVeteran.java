package quests._552_OlympiadVeteran;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.GArray;

public class _552_OlympiadVeteran extends Quest implements ScriptFile
{
	private static int OLYMPIAD_MANAGER = 31688;

	private static int OLYMPIAD_TREASURE_CHEST = 17169;
	private static int MEDAL_OF_GLORY = 21874;

	private static int[] PARTICIPATION_CERTIFICATE = { 17243, 17242, 17241 };

	public _552_OlympiadVeteran()
	{
		super(false);
		addStartNpc(OLYMPIAD_MANAGER);
		addQuestItem(PARTICIPATION_CERTIFICATE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("quest_accept"))
		{
			st.setCond(1);
			st.setState(2);
			st.playSound("ItemSound.quest_accept");
			st.addNotifyOfPlayerKill();
			event = "31688-2.htm";
		}
		else if(event.equalsIgnoreCase("give_reward"))
			event = addReward(st);
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(st.getState() == 1)
		{
			if(st.isNowAvailable())
			{
				if(st.getPlayer().getLevel() >= 75 && st.getPlayer().isNoble() && st.getPlayer().getActiveClass().getLevel() >= 4)
					return "31688.htm";
				else
				{
					htmltext = "31688-lowlvl.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
				return "31688-00.htm";
		}
		else if(cond == 1)
		{
			if(st.getQuestItemsCount(PARTICIPATION_CERTIFICATE[0]) == 0 && st.getQuestItemsCount(PARTICIPATION_CERTIFICATE[1]) == 0 && st.getQuestItemsCount(PARTICIPATION_CERTIFICATE[2]) == 0)
				return "31688-4.htm";
			htmltext = addReward(st);
		}
		else if(cond == 2)
			htmltext = addReward(st);
		return htmltext;
	}

	@Override
	public void notifyOlympiadGame(QuestState qs, GArray<L2Player> winTeam, GArray<L2Player> lossTeam, boolean haveWin, CompType type)
	{
		L2Player player = qs.getPlayer();
		if(player == null || qs.getCond() != 1)
			return;
		int count;
		switch(type)
		{
			case CLASSED:
				count = qs.getInt("count_classed");
				if(count < 5)
				{
					count++;
					qs.set("count_classed", count);
					if(count == 5)
					{
						qs.giveItems(PARTICIPATION_CERTIFICATE[0], 1);
						qs.playSound("ItemSound.quest_itemget");
					}
				}
				break;
			case NON_CLASSED:
				count = qs.getInt("count_nonclassed");
				if(count < 5)
				{
					count++;
					qs.set("count_nonclassed", count);
					if(count == 5)
					{
						qs.giveItems(PARTICIPATION_CERTIFICATE[1], 1);
						qs.playSound("ItemSound.quest_itemget");
					}
				}
				break;
			case TEAM_RANDOM:
				count = qs.getInt("count_team");
				if(count < 5)
				{
					count++;
					qs.set("count_team", count);
					if(count == 5)
					{
						qs.giveItems(PARTICIPATION_CERTIFICATE[2], 1);
						qs.playSound("ItemSound.quest_itemget");
					}
				}
				break;
		}
		for(int id : PARTICIPATION_CERTIFICATE)
			if(qs.getQuestItemsCount(id) == 0)
				return;
		qs.setCond(2);
		qs.playSound("ItemSound.quest_middle");
	}

	public void onLoad()
	{
	}

	public void onReload()
	{
	}

	public void onShutdown()
	{
	}

	private String addReward(QuestState qs)
	{
		int count = 0;
		for(int i = 0; i <= 2; i++)
		{
			if(qs.getQuestItemsCount(PARTICIPATION_CERTIFICATE[i]) > 0)
			{
				switch(i)
				{
					case 0:
						count++;
						break;
					case 1:
						count += 2;
						break;
					case 2:
						count += 3;
						break;
				}
			}
		}
		if(count == 0)
			return "31688-notreward.htm";
		qs.giveItems(OLYMPIAD_TREASURE_CHEST, count);
		qs.giveItems(MEDAL_OF_GLORY, 1L);
		qs.takeItems(17243, -1);
		qs.takeItems(17242, -1);
		qs.takeItems(17241, -1);
		qs.playSound("ItemSound.quest_finish");
		qs.exitCurrentQuest(this);
		return "31688-addreward.htm";
	}
}