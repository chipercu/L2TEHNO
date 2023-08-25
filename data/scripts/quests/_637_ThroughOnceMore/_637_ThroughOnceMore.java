package quests._637_ThroughOnceMore;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _637_ThroughOnceMore extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	//Drop rate
	public final int CHANCE = 40;

	//Npc
	public final int FLAURON = 32010;

	//Items
	public final int VISITOR_MARK = 8064;
	public final int FADED_MARK = 8065;
	public final int NECROHEART = 8066;
	public final int PERMANENT_MARK = 8067;
	public final int ANTEROOM_KEY = 8273;

	public _637_ThroughOnceMore()
	{
		super(false);
		addStartNpc(FLAURON);
		addKillId(21565, 21566, 21567, 21568);
		addQuestItem(NECROHEART);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("falsepriest_flauron_q0637_11.htm"))
		{
			st.setCond(1);
			st.takeItems(VISITOR_MARK, 1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("falsepriest_flauron_q0637_09.htm"))
			st.exitCurrentQuest(true);
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext;
		int cond = st.getCond();
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() > 72 && st.getQuestItemsCount(FADED_MARK) > 0)
				htmltext = "falsepriest_flauron_q0637_01.htm";
			else if(st.getPlayer().getLevel() > 72 && st.getQuestItemsCount(VISITOR_MARK) < 0)
			{
				htmltext = "falsepriest_flauron_q0637_03.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getPlayer().getLevel() > 72 && st.getQuestItemsCount(PERMANENT_MARK) < 0)
			{
				htmltext = "falsepriest_flauron_q0637_05.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "falsepriest_flauron_q0637_04.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 2 && st.getQuestItemsCount(NECROHEART) >= 10)
		{
			htmltext = "falsepriest_flauron_q0637_13.htm";
			st.takeItems(NECROHEART, -1);
			st.takeItems(FADED_MARK, -1);
			st.takeItems(VISITOR_MARK, -1);
			st.giveItems(PERMANENT_MARK, 1);
			st.giveItems(ANTEROOM_KEY, 10);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else
			htmltext = "falsepriest_flauron_q0637_12.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		long count = st.getQuestItemsCount(NECROHEART);
		if(st.getCond() == 1 && Rnd.chance(CHANCE) && count < 10)
		{
			st.giveItems(NECROHEART, 1);
			if(count == 9)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}

	@Override
	public void onAbort(QuestState st)
	{
		if(st.getQuestItemsCount(VISITOR_MARK) == 0)
			st.giveItems(VISITOR_MARK, 1);
	}
}