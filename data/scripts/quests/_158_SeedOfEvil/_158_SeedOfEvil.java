package quests._158_SeedOfEvil;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _158_SeedOfEvil extends Quest implements ScriptFile
{
	int CLAY_TABLET_ID = 1025;
	int ENCHANT_ARMOR_D = 956;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _158_SeedOfEvil()
	{
		super(false);

		addStartNpc(30031);

		addKillId(27016);

		addQuestItem(CLAY_TABLET_ID);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			st.set("id", "0");
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "quilt_q0158_04.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if(id == CREATED)
		{
			st.setState(STARTED);
			st.set("id", "0");
		}
		if(npcId == 30031 && st.getInt("cond") == 0)
		{
			if(st.getInt("cond") < 15)
			{
				if(st.getPlayer().getLevel() >= 21)
				{
					htmltext = "quilt_q0158_03.htm";
					return htmltext;
				}
				htmltext = "quilt_q0158_02.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "quilt_q0158_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == 30031 && st.getInt("cond") == 0)
			htmltext = "completed";
		else if(npcId == 30031 && st.getInt("cond") != 0 && st.getQuestItemsCount(CLAY_TABLET_ID) == 0)
			htmltext = "quilt_q0158_05.htm";
		else if(npcId == 30031 && st.getInt("cond") != 0 && st.getQuestItemsCount(CLAY_TABLET_ID) != 0)
		{
			st.takeItems(CLAY_TABLET_ID, st.getQuestItemsCount(CLAY_TABLET_ID));
			st.playSound(SOUND_FINISH);
			st.giveItems(ADENA_ID, 1495);
			st.addExpAndSp(17818, 927);
			st.giveItems(ENCHANT_ARMOR_D, 1);
			htmltext = "quilt_q0158_06.htm";
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(CLAY_TABLET_ID) == 0)
		{
			st.giveItems(CLAY_TABLET_ID, 1);
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
		}
		return null;
	}
}