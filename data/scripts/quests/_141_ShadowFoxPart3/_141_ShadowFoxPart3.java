package quests._141_ShadowFoxPart3;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._140_ShadowFoxPart2._140_ShadowFoxPart2;
import quests._142_FallenAngelRequestOfDawn._142_FallenAngelRequestOfDawn;
import quests._143_FallenAngelRequestOfDusk._143_FallenAngelRequestOfDusk;

public class _141_ShadowFoxPart3 extends Quest implements ScriptFile
{
	// NPC
	private final static int NATOOLS = 30894;

	// Items
	private final static int REPORT = 10350;

	// Monsters
	private final static int CrokianWarrior = 20791;
	private final static int Farhite = 20792;
	private final static int Alligator = 20135;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _141_ShadowFoxPart3()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addFirstTalkId(NATOOLS);
		addTalkId(NATOOLS);
		addQuestItem(REPORT);
		addKillId(CrokianWarrior, Farhite, Alligator);
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_140_ShadowFoxPart2.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("warehouse_chief_natools_q0141_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("warehouse_chief_natools_q0141_06.htm"))
		{
			st.set("cond", "2");
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("warehouse_chief_natools_q0141_19.htm"))
		{
			st.set("cond", "4");
			st.setState(STARTED);
			st.unset("talk");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("warehouse_chief_natools_q0141_23.htm"))
		{
			if(st.getInt("reward") != 1)
			{
				st.playSound(SOUND_FINISH);
				st.addExpAndSp(278005, 17058, false);
				st.giveItems(ADENA_ID, 88888);
				st.set("reward", "1");
				htmltext = "warehouse_chief_natools_q0141_25.htm";
			}
			else
				htmltext = "warehouse_chief_natools_q0141_25.htm";
		}
		else if(event.equalsIgnoreCase("quest_accept_142"))
		{
			Quest q1 = QuestManager.getQuest(_142_FallenAngelRequestOfDawn.class);
			if(q1 != null)
			{
				st.exitCurrentQuest(false);
				QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
				q1.notifyEvent("stained_rock_q0142_03.htm", qs1, npc);
				return null;
			}
		}
		else if(event.equalsIgnoreCase("quest_accept_143"))
		{
			Quest q1 = QuestManager.getQuest(_143_FallenAngelRequestOfDusk.class);
			if(q1 != null)
			{
				st.exitCurrentQuest(false);
				QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
				q1.notifyEvent("warehouse_chief_natools_q0143_01.htm", qs1, npc);
				return null;
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		String htmltext = "noquest";
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 37)
				htmltext = "warehouse_chief_natools_q0141_01.htm";
			else
				htmltext = "warehouse_chief_natools_q0141_02.htm";
		}
		else if(cond == 1)
			htmltext = "warehouse_chief_natools_q0141_03.htm";
		else if(cond == 2)
			htmltext = "warehouse_chief_natools_q0141_07.htm";
		else if(cond == 3)
		{
			if(st.getInt("talk") == 1)
				htmltext = "warehouse_chief_natools_q0141_10.htm";
			else
			{
				htmltext = "warehouse_chief_natools_q0141_08.htm";
				st.takeItems(REPORT, -1);
				st.set("talk", "1");
			}
		}
		else if(cond == 4)
			htmltext = "warehouse_chief_natools_q0141_20.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 2 && st.rollAndGive(REPORT, 1, 1, 30, 80 * npc.getTemplate().rateHp))
			st.setCond(3);
		return null;
	}
}