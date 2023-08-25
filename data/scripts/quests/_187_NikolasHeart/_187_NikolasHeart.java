package quests._187_NikolasHeart;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._185_NikolasCooperationConsideration._185_NikolasCooperationConsideration;

public class _187_NikolasHeart extends Quest implements ScriptFile
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;

	private static final int Certificate = 10362;
	private static final int Metal = 10368;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _187_NikolasHeart()
	{
		super(false);

		addTalkId(Kusto, Nikola, Lorain);
		addFirstTalkId(Lorain);
		addQuestItem(Certificate, Metal);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("researcher_lorain_q0187_03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
			st.takeItems(Certificate, -1);
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("maestro_nikola_q0187_03.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("head_blacksmith_kusto_q0187_03.htm"))
		{
			st.giveItems(ADENA_ID, 93383, true);
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
						htmltext = "researcher_lorain_q0187_02.htm";
					else
						htmltext = "researcher_lorain_q0187_01.htm";
				else if(cond == 1)
					htmltext = "researcher_lorain_q0187_04.htm";
			}
			else if(npcId == Nikola)
			{
				if(cond == 1)
					htmltext = "maestro_nikola_q0187_01.htm";
				else if(cond == 2)
					htmltext = "maestro_nikola_q0187_04.htm";
			}
			else if(npcId == Kusto)
				if(cond == 2)
					htmltext = "head_blacksmith_kusto_q0187_01.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_185_NikolasCooperationConsideration.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}
}