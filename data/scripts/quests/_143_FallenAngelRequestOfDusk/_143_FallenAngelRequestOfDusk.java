package quests._143_FallenAngelRequestOfDusk;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _143_FallenAngelRequestOfDusk extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	// NPCs
	private final static int NATOOLS = 30894;
	private final static int TOBIAS = 30297;
	private final static int CASIAN = 30612;
	private final static int ROCK = 32368;
	private final static int ANGEL = 32369;

	private final static int MonsterAngel = 27338;

	// Items
	private final static int SEALED_PATH = 10354;
	private final static int PATH = 10355;
	private final static int EMPTY_CRYSTAL = 10356;
	private final static int MEDICINE = 10357;
	private final static int MESSAGE = 10358;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public _143_FallenAngelRequestOfDusk()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addTalkId(NATOOLS, TOBIAS, CASIAN, ROCK, ANGEL);
		addQuestItem(SEALED_PATH, PATH, EMPTY_CRYSTAL, MEDICINE, MESSAGE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("warehouse_chief_natools_q0143_01.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("warehouse_chief_natools_q0143_04.htm"))
		{
			st.set("cond", "2");
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(SEALED_PATH, 1);
		}
		else if(event.equalsIgnoreCase("master_tobias_q0143_05.htm"))
		{
			st.set("cond", "3");
			st.setState(STARTED);
			st.unset("talk");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(PATH, 1);
			st.giveItems(EMPTY_CRYSTAL, 1);
		}
		else if(event.equalsIgnoreCase("sage_kasian_q0143_09.htm"))
		{
			st.set("cond", "4");
			st.setState(STARTED);
			st.unset("talk");
			st.giveItems(MEDICINE, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("stained_rock_q0143_05.htm"))
		{
			if(L2ObjectsStorage.getByNpcId(MonsterAngel) != null)
				htmltext = "stained_rock_q0143_03.htm";
			else if(L2ObjectsStorage.getByNpcId(ANGEL) != null)
				htmltext = "stained_rock_q0143_04.htm";
			else
			{
				st.addSpawn(ANGEL, 180000);
				st.playSound(SOUND_MIDDLE);
			}
		}
		else if(event.equalsIgnoreCase("q_fallen_angel_npc_q0143_14.htm"))
		{
			st.set("cond", "5");
			st.setState(STARTED);
			st.unset("talk");
			st.takeItems(EMPTY_CRYSTAL, -1);
			st.giveItems(MESSAGE, 1);
			st.playSound(SOUND_MIDDLE);

			L2NpcInstance n = L2ObjectsStorage.getByNpcId(ANGEL);
			if(n != null)
				n.deleteMe();
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == NATOOLS)
		{
			if(cond == 1)
				htmltext = "warehouse_chief_natools_q0143_01.htm";
			else if(cond == 2)
				htmltext = "warehouse_chief_natools_q0143_05.htm";
		}
		else if(npcId == TOBIAS)
		{
			if(cond == 2)
				if(st.getInt("talk") == 1)
					htmltext = "master_tobias_q0143_03.htm";
				else
				{
					htmltext = "master_tobias_q0143_02.htm";
					st.takeItems(SEALED_PATH, -1);
					st.set("talk", "1");
				}
			else if(cond == 3)
				htmltext = "master_tobias_q0143_06.htm";
			else if(cond == 5)
			{
				htmltext = "master_tobias_q0143_07.htm";
				st.playSound(SOUND_FINISH);
				st.giveItems(ADENA_ID, 89046);
				st.exitCurrentQuest(false);
			}
		}
		else if(npcId == CASIAN)
		{
			if(cond == 3)
			{
				if(st.getInt("talk") == 1)
					htmltext = "sage_kasian_q0143_03.htm";
				else
				{
					htmltext = "sage_kasian_q0143_02.htm";
					st.takeItems(PATH, -1);
					st.set("talk", "1");
				}
			}
			else if(cond == 4)
				htmltext = "sage_kasian_q0143_09.htm";
		}
		else if(npcId == ROCK)
		{
			if(cond <= 3)
				htmltext = "stained_rock_q0143_01.htm";
			else if(cond == 4)
				htmltext = "stained_rock_q0143_02.htm";
			else
				htmltext = "stained_rock_q0143_06.htm";
		}
		else if(npcId == ANGEL)
		{
			if(cond == 4)
				if(st.getInt("talk") == 1)
					htmltext = "q_fallen_angel_npc_q0143_04.htm";
				else
				{
					htmltext = "q_fallen_angel_npc_q0143_03.htm";
					st.takeItems(MEDICINE, -1);
					st.set("talk", "1");
				}
		}
		else if(cond == 5)
			htmltext = "q_fallen_angel_npc_q0143_14.htm";
		return htmltext;
	}
}