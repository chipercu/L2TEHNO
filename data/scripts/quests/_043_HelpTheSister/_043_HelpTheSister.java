package quests._043_HelpTheSister;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _043_HelpTheSister extends Quest implements ScriptFile
{
	private static final int COOPER = 30829;
	private static final int GALLADUCCI = 30097;

	private static final int CRAFTED_DAGGER = 220;
	private static final int MAP_PIECE = 7550;
	private static final int MAP = 7551;
	private static final int PET_TICKET = 7584;

	private static final int SPECTER = 20171;
	private static final int SORROW_MAIDEN = 20197;

	private static final int MAX_COUNT = 30;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _043_HelpTheSister()
	{
		super(false);

		addStartNpc(COOPER);

		addTalkId(GALLADUCCI);

		addKillId(SPECTER);
		addKillId(SORROW_MAIDEN);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			htmltext = "pet_manager_cooper_q0043_0104.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("3") && st.getQuestItemsCount(CRAFTED_DAGGER) > 0)
		{
			htmltext = "pet_manager_cooper_q0043_0201.htm";
			st.takeItems(CRAFTED_DAGGER, 1);
			st.set("cond", "2");
		}
		else if(event.equals("4") && st.getQuestItemsCount(MAP_PIECE) >= MAX_COUNT)
		{
			htmltext = "pet_manager_cooper_q0043_0301.htm";
			st.takeItems(MAP_PIECE, MAX_COUNT);
			st.giveItems(MAP, 1);
			st.set("cond", "4");
		}
		else if(event.equals("5") && st.getQuestItemsCount(MAP) > 0)
		{
			htmltext = "galladuchi_q0043_0401.htm";
			st.takeItems(MAP, 1);
			st.set("cond", "5");
		}
		else if(event.equals("7"))
		{
			htmltext = "pet_manager_cooper_q0043_0501.htm";
			st.giveItems(PET_TICKET, 1);
			st.set("cond", "0");
			st.exitCurrentQuest(false);
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
			if(st.getPlayer().getLevel() >= 26)
				htmltext = "pet_manager_cooper_q0043_0101.htm";
			else
			{
				st.exitCurrentQuest(true);
				htmltext = "pet_manager_cooper_q0043_0103.htm";
			}
		}
		else if(id == STARTED)
		{
			int cond = st.getInt("cond");
			if(npcId == COOPER)
			{
				if(cond == 1)
				{
					if(st.getQuestItemsCount(CRAFTED_DAGGER) == 0)
						htmltext = "pet_manager_cooper_q0043_0106.htm";
					else
						htmltext = "pet_manager_cooper_q0043_0105.htm";
				}
				else if(cond == 2)
					htmltext = "pet_manager_cooper_q0043_0204.htm";
				else if(cond == 3)
					htmltext = "pet_manager_cooper_q0043_0203.htm";
				else if(cond == 4)
					htmltext = "pet_manager_cooper_q0043_0303.htm";
				else if(cond == 5)
					htmltext = "pet_manager_cooper_q0043_0401.htm";
			}
			else if(npcId == GALLADUCCI)
				if(cond == 4 && st.getQuestItemsCount(MAP) > 0)
					htmltext = "galladuchi_q0043_0301.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		if(cond == 2)
		{
			long pieces = st.getQuestItemsCount(MAP_PIECE);
			if(pieces < MAX_COUNT)
			{
				st.giveItems(MAP_PIECE, 1);
				if(pieces < MAX_COUNT - 1)
					st.playSound(SOUND_ITEMGET);
				else
				{
					st.playSound(SOUND_MIDDLE);
					st.set("cond", "3");
				}
			}
		}
		return null;
	}
}