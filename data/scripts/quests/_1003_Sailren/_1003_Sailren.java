package quests._1003_Sailren;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import bosses.SailrenManager;

public class _1003_Sailren extends Quest implements ScriptFile
{
	private static final int STATUE = 32109;
	private static final int GAZKH = 8784;

	public void onLoad()
	{
		ScriptFile._log.info("Loaded Quest: 1003: Sailren");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _1003_Sailren()
	{
		super("Sailren", false);
		addStartNpc(STATUE);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = null;
		if(st.getPlayer().getLevel() < 75)
			htmltext = "32109-03.htm";
		else if(st.getQuestItemsCount(GAZKH) > 0)
		{
			int check = SailrenManager.canIntoSailrenLair(st.getPlayer());
			if(check == 1 || check == 2)
				htmltext = "32109-05.htm";
			else if(check == 3)
				htmltext = "32109-04.htm";
			else if(check == 4)
				htmltext = "32109-01.htm";
			else if(check == 0)
			{
				st.takeItems(GAZKH, 1);
				SailrenManager.setSailrenSpawnTask();
				SailrenManager.entryToSailrenLair(st.getPlayer());
			}
		}
		else
			htmltext = "32109-02.htm";
		st.exitCurrentQuest(true);
		return htmltext;
	}
}