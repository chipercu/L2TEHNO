package quests._008_AnAdventureBegins;

// version = Unknown

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _008_AnAdventureBegins extends Quest implements ScriptFile
{
	int JASMINE = 30134;
	int ROSELYN = 30355;
	int HARNE = 30144;

	int ROSELYNS_NOTE = 7573;

	int SCROLL_OF_ESCAPE_GIRAN = 7126;
	int MARK_OF_TRAVELER = 7570;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _008_AnAdventureBegins()
	{
		super(false);

		addStartNpc(JASMINE);

		addTalkId(JASMINE);
		addTalkId(ROSELYN);
		addTalkId(HARNE);

		addQuestItem(ROSELYNS_NOTE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("jasmine_q0008_0104.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("sentry_roseline_q0008_0201.htm"))
		{
			st.giveItems(ROSELYNS_NOTE, 1);
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("harne_q0008_0301.htm"))
		{
			st.takeItems(ROSELYNS_NOTE, -1);
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("jasmine_q0008_0401.htm"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.giveItems(MARK_OF_TRAVELER, 1);
			st.set("cond", "0");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == JASMINE)
		{
			if(cond == 0 && st.getPlayer().getRace() == Race.darkelf)
				if(st.getPlayer().getLevel() >= 3)
					htmltext = "jasmine_q0008_0101.htm";
				else
				{
					htmltext = "jasmine_q0008_0102.htm";
					st.exitCurrentQuest(true);
				}
			else if(cond == 1)
				htmltext = "jasmine_q0008_0105.htm";
			else if(cond == 3)
				htmltext = "jasmine_q0008_0301.htm";
		}
		else if(npcId == ROSELYN)
		{
			if(st.getQuestItemsCount(ROSELYNS_NOTE) == 0)
				htmltext = "sentry_roseline_q0008_0101.htm";
			else
				htmltext = "sentry_roseline_q0008_0202.htm";
		}
		else if(npcId == HARNE)
			if(cond == 2 && st.getQuestItemsCount(ROSELYNS_NOTE) > 0)
				htmltext = "harne_q0008_0201.htm";
			else if(cond == 2 && st.getQuestItemsCount(ROSELYNS_NOTE) == 0)
				htmltext = "harne_q0008_0302.htm";
			else if(cond == 3)
				htmltext = "harne_q0008_0303.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		return null;
	}
}