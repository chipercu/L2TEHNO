package quests._190_LostDream;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._187_NikolasHeart._187_NikolasHeart;

public class _190_LostDream extends Quest implements ScriptFile
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;
	private static final int Juris = 30113;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _190_LostDream()
	{
		super(false);

		addTalkId(Kusto, Nikola, Lorain, Juris);
		addFirstTalkId(Kusto);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("head_blacksmith_kusto_q0190_03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
		}
		else if(event.equalsIgnoreCase("head_blacksmith_kusto_q0190_06.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "3");
		}
		else if(event.equalsIgnoreCase("juria_q0190_03.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
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
		{
			if(npcId == Kusto)
			{
				if(cond == 0)
					if(st.getPlayer().getLevel() < 42)
						htmltext = "head_blacksmith_kusto_q0190_02.htm";
					else
						htmltext = "head_blacksmith_kusto_q0190_01.htm";
				else if(cond == 1)
					htmltext = "head_blacksmith_kusto_q0190_04.htm";
				else if(cond == 2)
					htmltext = "head_blacksmith_kusto_q0190_05.htm";
				else if(cond == 3)
					htmltext = "head_blacksmith_kusto_q0190_07.htm";
				else if(cond == 5)
				{
					htmltext = "head_blacksmith_kusto_q0190_08.htm";
					st.giveItems(ADENA_ID, 109427, true);
					st.addExpAndSp(309467, 20614, true);
					st.exitCurrentQuest(false);
					st.playSound(SOUND_FINISH);
				}
			}
			else if(npcId == Juris)
			{
				if(cond == 1)
					htmltext = "juria_q0190_01.htm";
				else if(cond == 2)
					htmltext = "juria_q0190_04.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 3)
				{
					htmltext = "researcher_lorain_q0190_01.htm";
					st.playSound(SOUND_MIDDLE);
					st.set("cond", "4");
				}
				else if(cond == 4)
					htmltext = "researcher_lorain_q0190_02.htm";
			}
			else if(npcId == Nikola)
			{
				if(cond == 4)
				{
					htmltext = "maestro_nikola_q0190_01.htm";
					st.playSound(SOUND_MIDDLE);
					st.set("cond", "5");
				}
				else if(cond == 5)
					htmltext = "maestro_nikola_q0190_02.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_187_NikolasHeart.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}
}