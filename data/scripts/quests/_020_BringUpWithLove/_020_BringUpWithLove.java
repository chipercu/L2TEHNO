package quests._020_BringUpWithLove;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _020_BringUpWithLove extends Quest implements ScriptFile
{
	int TUNATUN = 31537;
	int JEWEL_INNOCENCE = 15533; // Выдается при кормежке, в ядре.

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _020_BringUpWithLove()
	{
		super(false);

		addStartNpc(TUNATUN);

		addQuestItem(JEWEL_INNOCENCE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int id = st.getState();
		if(id == CREATED)
		{
			st.set("cond", "0");
			if(event.equals("beast_herder_tunatun_q0020_03.htm") || event.equals("beast_herder_tunatun_q0020_04.htm") || event.equals("beast_herder_tunatun_q0020_06.htm") || event.equals("beast_herder_tunatun_q0020_07.htm") || event.equals("beast_herder_tunatun_q0020_08.htm"))
				return event;
			if(event.equals("beast_herder_tunatun_q0020_09.htm"))
			{
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.giveItems(15473,1);
				return event;
			}
		}
		else if(event.equals("beast_herder_tunatun_q0020_12.htm"))
		{
			st.takeItems(JEWEL_INNOCENCE, -1);
			st.giveItems(ADENA_ID, 68500);
			st.giveItems(9553,1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getInt("cond");

		if(cond == 0)
		{
			if(id == CREATED)
				if(st.getPlayer().getLevel() < 82)
				{
					st.exitCurrentQuest(true);
					htmltext = "beast_herder_tunatun_q0020_02.htm";
				}
				else
					htmltext = "beast_herder_tunatun_q0020_01.htm";
		}
		else if(cond == 1 && st.getQuestItemsCount(JEWEL_INNOCENCE) == 0)
			htmltext = "beast_herder_tunatun_q0020_10.htm";
		else if(cond == 2 && st.getQuestItemsCount(JEWEL_INNOCENCE) >= 1)
			htmltext = "beast_herder_tunatun_q0020_11.htm";
		return htmltext;
	}
}