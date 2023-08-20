package quests._551_OlympiadStarter;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.GArray;

public class _551_OlympiadStarter extends Quest implements ScriptFile
{
	private static int OLYMPIAD_MANAGER = 31688;

	private static int OLYMPIAD_TREASURE_CHEST = 17169;
	private static int MEDAL_OF_GLORY = 21874;

	private static int[] PARTICIPATION_CERTIFICATE = { 17238, 17239, 17240 };

	public _551_OlympiadStarter()
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
			htmltext = "31688-4.htm";
		else if(cond == 2)
			htmltext = addReward(st);
		else if(st.isCompleted())
			htmltext = "31688-3.htm";
		return htmltext;
	}

	@Override
	public void notifyOlympiadGame(QuestState qs, GArray<L2Player> winTeam, GArray<L2Player> lossTeam, boolean haveWin, CompType type)
	{
		L2Player player = qs.getPlayer();
		if(player == null || qs.getCond() != 1)
			return;
		int count = qs.getInt("count") + 1;
		qs.set("count", count);

		switch(count)
		{
			case 3:
				qs.giveItems(PARTICIPATION_CERTIFICATE[0], 1);
				qs.playSound("ItemSound.quest_itemget");
				break;
			case 5:
				qs.giveItems(PARTICIPATION_CERTIFICATE[1], 1);
				qs.playSound("ItemSound.quest_itemget");
				break;
			case 10:
				qs.giveItems(PARTICIPATION_CERTIFICATE[2], 1);
				qs.playSound("ItemSound.quest_itemget");
				qs.playSound("ItemSound.quest_middle");
				qs.setCond(2);
				break;
		}
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
		if(qs.getQuestItemsCount(PARTICIPATION_CERTIFICATE[2]) > 0)
		{
			qs.giveItems(OLYMPIAD_TREASURE_CHEST, 4);
			qs.giveItems(MEDAL_OF_GLORY, 5);
			qs.takeItems(17238, -1);
			qs.takeItems(17239, -1);
			qs.takeItems(17240, -1);
			qs.playSound("ItemSound.quest_finish");
			qs.exitCurrentQuest(this);
		}
		else if(qs.getQuestItemsCount(PARTICIPATION_CERTIFICATE[1]) > 0)
		{
			qs.giveItems(OLYMPIAD_TREASURE_CHEST, 2);
			qs.giveItems(MEDAL_OF_GLORY, 3);
			qs.takeItems(17238, -1);
			qs.takeItems(17239, -1);
			qs.playSound("ItemSound.quest_finish");
			qs.exitCurrentQuest(this);
		}
		else if(qs.getQuestItemsCount(PARTICIPATION_CERTIFICATE[0]) > 0)
		{
			qs.giveItems(OLYMPIAD_TREASURE_CHEST, 1);
			qs.takeItems(17238, -1);
			qs.playSound("ItemSound.quest_finish");
			qs.exitCurrentQuest(this);
		}
		else
			return "31688-notreward.htm";
		return "31688-addreward.htm";
	}
}