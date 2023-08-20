package quests._165_ShilensHunt;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _165_ShilensHunt extends Quest implements ScriptFile
{
	private static final int DARK_BEZOAR = 1160;
	private static final int LESSER_HEALING_POTION = 1060;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _165_ShilensHunt()
	{
		super(false);

		addStartNpc(30348);

		addTalkId(30348);

		addKillId(20456);
		addKillId(20529);
		addKillId(20532);
		addKillId(20536);

		addQuestItem(DARK_BEZOAR);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "30348-03.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");

		if(cond == 0)
		{
			if(st.getPlayer().getRace() != Race.darkelf)
				htmltext = "30348-00.htm";
			else if(st.getPlayer().getLevel() >= 3)
			{
				htmltext = "30348-02.htm";
				return htmltext;
			}
			else
			{
				htmltext = "30348-01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1 || st.getQuestItemsCount(DARK_BEZOAR) < 13)
			htmltext = "30348-04.htm";
		else if(cond == 2)
		{
			htmltext = "30348-05.htm";
			st.takeItems(DARK_BEZOAR, -1);
			st.giveItems(LESSER_HEALING_POTION, 5);
			st.addExpAndSp(1000, 0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		if(cond == 1 && st.getQuestItemsCount(DARK_BEZOAR) < 13 && Rnd.chance(90))
		{
			st.giveItems(DARK_BEZOAR, 1);
			if(st.getQuestItemsCount(DARK_BEZOAR) == 13)
			{
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}