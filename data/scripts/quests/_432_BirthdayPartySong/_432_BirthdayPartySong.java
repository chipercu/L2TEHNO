package quests._432_BirthdayPartySong;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _432_BirthdayPartySong extends Quest implements ScriptFile
{
	//NPC
	private static int MELODY_MAESTRO_OCTAVIA = 31043;
	//MOB
	private static int ROUGH_HEWN_ROCK_GOLEMS = 21103;
	//Quest items
	private static int RED_CRYSTALS = 7541;
	private static int BIRTHDAY_ECHO_CRYSTAL = 7061;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _432_BirthdayPartySong()
	{
		super(false);

		addStartNpc(MELODY_MAESTRO_OCTAVIA);

		addKillId(ROUGH_HEWN_ROCK_GOLEMS);

		addQuestItem(RED_CRYSTALS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("muzyko_q0432_0104.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("muzyko_q0432_0201.htm"))
			if(st.getQuestItemsCount(RED_CRYSTALS) == 50)
			{
				st.takeItems(RED_CRYSTALS, -1);
				st.giveItems(BIRTHDAY_ECHO_CRYSTAL, 25);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "muzyko_q0432_0202.htm";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int condition = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == MELODY_MAESTRO_OCTAVIA)
			if(condition == 0)
			{
				if(st.getPlayer().getLevel() >= 31)
					htmltext = "muzyko_q0432_0101.htm";
				else
				{
					htmltext = "muzyko_q0432_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(condition == 1)
				htmltext = "muzyko_q0432_0106.htm";
			else if(condition == 2 && st.getQuestItemsCount(RED_CRYSTALS) == 50)
				htmltext = "muzyko_q0432_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() != STARTED)
			return null;
		int npcId = npc.getNpcId();

		if(npcId == ROUGH_HEWN_ROCK_GOLEMS)
			if(st.getInt("cond") == 1 && st.getQuestItemsCount(RED_CRYSTALS) < 50)
			{
				st.giveItems(RED_CRYSTALS, 1);

				if(st.getQuestItemsCount(RED_CRYSTALS) == 50)
				{
					st.playSound(SOUND_MIDDLE);
					st.set("cond", "2");
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}