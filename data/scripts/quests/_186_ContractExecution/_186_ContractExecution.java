package quests._186_ContractExecution;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._184_NikolasCooperationContract._184_NikolasCooperationContract;

public class _186_ContractExecution extends Quest implements ScriptFile
{
	private static final int Luka = 31437;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;

	private static final int Certificate = 10362;
	private static final int MetalReport = 10366;
	private static final int Accessory = 10367;

	private static final int LetoLizardman = 20577;
	private static final int LetoLizardmanArcher = 20578;
	private static final int LetoLizardmanSoldier = 20579;
	private static final int LetoLizardmanWarrior = 20580;
	private static final int LetoLizardmanShaman = 20581;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int TimakOrc = 20583;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _186_ContractExecution()
	{
		super(false);

		addTalkId(Luka, Nikola, Lorain);
		addFirstTalkId(Lorain);
		addKillId(LetoLizardman, LetoLizardmanArcher, LetoLizardmanSoldier, LetoLizardmanWarrior, LetoLizardmanShaman, LetoLizardmanOverlord, TimakOrc);
		addQuestItem(Certificate, MetalReport, Accessory);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("researcher_lorain_q0186_03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
			st.takeItems(Certificate, -1);
			st.giveItems(MetalReport, 1);
		}
		else if(event.equalsIgnoreCase("maestro_nikola_q0186_03.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("blueprint_seller_luka_q0186_06.htm"))
		{
			st.giveItems(ADENA_ID, 105083, true);
			st.addExpAndSp(285935, 18711, true);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(st.getState() == STARTED)
			if(npcId == Lorain)
			{
				if(cond == 0)
					if(st.getPlayer().getLevel() < 41)
						htmltext = "researcher_lorain_q0186_02.htm";
					else
						htmltext = "researcher_lorain_q0186_01.htm";
				else if(cond == 1)
					htmltext = "researcher_lorain_q0186_04.htm";
			}
			else if(npcId == Nikola)
			{
				if(cond == 1)
					htmltext = "maestro_nikola_q0186_01.htm";
				else if(cond == 2)
					htmltext = "maestro_nikola_q0186_04.htm";
			}
			else if(npcId == Luka)
				if(st.getQuestItemsCount(Accessory) <= 0)
					htmltext = "blueprint_seller_luka_q0186_01.htm";
				else
					htmltext = "blueprint_seller_luka_q0186_02.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() == STARTED && st.getQuestItemsCount(Accessory) <= 0 && st.getInt("cond") == 2 && Rnd.get(5) == 0)
		{
			st.playSound(SOUND_MIDDLE);
			st.giveItems(Accessory, 1);
		}
		return null;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_184_NikolasCooperationContract.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}
}