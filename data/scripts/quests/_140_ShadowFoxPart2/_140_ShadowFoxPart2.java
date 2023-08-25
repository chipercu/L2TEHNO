package quests._140_ShadowFoxPart2;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._139_ShadowFoxPart1._139_ShadowFoxPart1;
import quests._141_ShadowFoxPart3._141_ShadowFoxPart3;

public class _140_ShadowFoxPart2 extends Quest implements ScriptFile
{
	// NPCs
	private final static int KLUCK = 30895;
	private final static int XENOVIA = 30912;

	// Items
	private final static int CRYSTAL = 10347;
	private final static int OXYDE = 10348;
	private final static int CRYPT = 10349;

	// Monsters
	private final static int Crokian = 20789;
	private final static int Dailaon = 20790;
	private final static int CrokianWarrior = 20791;
	private final static int Farhite = 20792;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _140_ShadowFoxPart2()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addFirstTalkId(KLUCK);
		addTalkId(KLUCK, XENOVIA);
		addQuestItem(CRYSTAL, OXYDE, CRYPT);
		addKillId(Crokian, Dailaon, CrokianWarrior, Farhite);
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_139_ShadowFoxPart1.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("warehouse_keeper_kluck_q0140_04.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("warehouse_keeper_kluck_q0140_09.htm"))
		{
			st.set("cond", "2");
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("warehouse_keeper_kluck_q0140_14.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(30000, 2000, false);
			st.giveItems(ADENA_ID, 18775);
			Quest q = QuestManager.getQuest(_141_ShadowFoxPart3.class);
			if(q != null)
				q.newQuestState(st.getPlayer(), STARTED);
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("magister_xenovia_q0140_09.htm"))
		{
			st.set("cond", "3");
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("magister_xenovia_q0140_12.htm"))
		{
			st.takeItems(CRYSTAL, 5);
			if(Rnd.chance(60))
			{
				st.giveItems(OXYDE, 1);
				if(st.getQuestItemsCount(OXYDE) >= 3)
				{
					htmltext = "magister_xenovia_q0140_13.htm";
					st.set("cond", "4");
					st.setState(STARTED);
					st.playSound(SOUND_MIDDLE);
					st.takeItems(CRYSTAL, -1);
					st.takeItems(OXYDE, -1);
					st.giveItems(CRYPT, 1);
				}
			}
			else
				htmltext = "magister_xenovia_q0140_14.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		String htmltext = "noquest";
		if(npcId == KLUCK)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 37)
					htmltext = "warehouse_keeper_kluck_q0140_01.htm";
				else
					htmltext = "warehouse_keeper_kluck_q0140_02.htm";
			}
			else if(cond == 1)
				htmltext = "warehouse_keeper_kluck_q0140_04.htm";
			else if(cond == 2 || cond == 3)
				htmltext = "warehouse_keeper_kluck_q0140_10.htm";
			else if(cond == 4)
				if(st.getInt("talk") == 1)
					htmltext = "warehouse_keeper_kluck_q0140_12.htm";
				else
				{
					htmltext = "warehouse_keeper_kluck_q0140_11.htm";
					st.takeItems(CRYPT, -1);
					st.set("talk", "1");
				}
		}
		else if(npcId == XENOVIA)
			if(cond == 2)
				htmltext = "magister_xenovia_q0140_02.htm";
			else if(cond == 3)
				if(st.getQuestItemsCount(CRYSTAL) >= 5)
					htmltext = "magister_xenovia_q0140_11.htm";
				else
					htmltext = "magister_xenovia_q0140_09.htm";
			else if(cond == 4)
				htmltext = "magister_xenovia_q0140_15.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 3)
			st.rollAndGive(CRYSTAL, 1, 80 * npc.getTemplate().rateHp);
		return null;
	}
}