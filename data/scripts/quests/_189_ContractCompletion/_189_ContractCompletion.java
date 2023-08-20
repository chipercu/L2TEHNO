package quests._189_ContractCompletion;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._186_ContractExecution._186_ContractExecution;

public class _189_ContractCompletion extends Quest implements ScriptFile
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Luka = 30621;
	private static final int Shegfield = 30068;

	private static final int Metal = 10370;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _189_ContractCompletion()
	{
		super(false);

		addTalkId(Kusto, Luka, Lorain, Shegfield);
		addFirstTalkId(Luka);
		addQuestItem(Metal);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("blueprint_seller_luka_q0189_03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("researcher_lorain_q0189_02.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
			st.takeItems(Metal, -1);
		}
		else if(event.equalsIgnoreCase("shegfield_q0189_03.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("head_blacksmith_kusto_q0189_02.htm"))
		{
			st.giveItems(ADENA_ID, 121527, true);
			st.addExpAndSp(309467, 20614, true);
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
			if(npcId == Luka)
			{
				if(cond == 0)
					if(st.getPlayer().getLevel() < 42)
						htmltext = "blueprint_seller_luka_q0189_02.htm";
					else
						htmltext = "blueprint_seller_luka_q0189_01.htm";
				else if(cond == 1)
					htmltext = "blueprint_seller_luka_q0189_04.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 1)
					htmltext = "researcher_lorain_q0189_01.htm";
				else if(cond == 2)
					htmltext = "researcher_lorain_q0189_03.htm";
				else if(cond == 3)
				{
					htmltext = "researcher_lorain_q0189_04.htm";
					st.set("cond", "4");
					st.playSound(SOUND_MIDDLE);
				}
				else if(cond == 4)
					htmltext = "researcher_lorain_q0189_05.htm";
			}
			else if(npcId == Shegfield)
			{
				if(cond == 2)
					htmltext = "shegfield_q0189_01.htm";
				else if(cond == 3)
					htmltext = "shegfield_q0189_04.htm";
			}
			else if(npcId == Kusto)
				if(cond == 4)
					htmltext = "head_blacksmith_kusto_q0189_01.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_186_ContractExecution.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}
}