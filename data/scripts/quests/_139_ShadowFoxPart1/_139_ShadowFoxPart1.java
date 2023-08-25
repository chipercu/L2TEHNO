package quests._139_ShadowFoxPart1;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._138_TempleChampionPart2._138_TempleChampionPart2;

public class _139_ShadowFoxPart1 extends Quest implements ScriptFile
{
	// NPC
	private final static int MIA = 30896;

	// Items
	private final static int FRAGMENT = 10345;
	private final static int CHEST = 10346;

	// Monsters
	private final static int TasabaLizardman1 = 20784;
	private final static int TasabaLizardman2 = 21639;
	private final static int TasabaLizardmanShaman1 = 20785;
	private final static int TasabaLizardmanShaman2 = 21640;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _139_ShadowFoxPart1()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addFirstTalkId(MIA);
		addTalkId(MIA);
		addQuestItem(FRAGMENT, CHEST);
		addKillId(TasabaLizardman1, TasabaLizardman2, TasabaLizardmanShaman1, TasabaLizardmanShaman2);
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_138_TempleChampionPart2.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "warehouse_keeper_mia_q0139_05.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("139_9"))
		{
			htmltext = "warehouse_keeper_mia_q0139_15.htm";
			st.set("cond", "2");
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("139_10"))
		{
			htmltext = "warehouse_keeper_mia_q0139_20.htm";
			st.takeItems(FRAGMENT, -1);
			st.takeItems(CHEST, -1);
			st.set("talk", "1");
		}
		else if(event.equalsIgnoreCase("139_12"))
		{
			htmltext = "warehouse_keeper_mia_q0139_23.htm";
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(30000, 2000, false);
			st.giveItems(ADENA_ID, 14050);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == MIA)
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 37)
					htmltext = "warehouse_keeper_mia_q0139_01.htm";
				else
					htmltext = "warehouse_keeper_mia_q0139_03.htm";
			}
			else if(cond == 1)
				htmltext = "warehouse_keeper_mia_q0139_05.htm";
			else if(cond == 2)
				if(st.getQuestItemsCount(FRAGMENT) >= 10 && st.getQuestItemsCount(CHEST) >= 1)
					htmltext = "warehouse_keeper_mia_q0139_17.htm";
				else if(st.getInt("talk") == 1)
					htmltext = "warehouse_keeper_mia_q0139_20.htm";
				else
					htmltext = "warehouse_keeper_mia_q0139_16.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		if(cond == 2)
		{
			st.giveItems(FRAGMENT, 1);
			st.playSound(SOUND_ITEMGET);
			if(Rnd.chance(10))
				st.giveItems(CHEST, 1);
		}
		return null;
	}
}