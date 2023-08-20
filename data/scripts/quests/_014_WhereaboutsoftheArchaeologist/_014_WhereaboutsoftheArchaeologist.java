package quests._014_WhereaboutsoftheArchaeologist;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _014_WhereaboutsoftheArchaeologist extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	private static final int LETTER_TO_ARCHAEOLOGIST = 7253;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _014_WhereaboutsoftheArchaeologist()
	{
		super(false);

		addStartNpc(31263);
		addTalkId(31538);

		addQuestItem(LETTER_TO_ARCHAEOLOGIST);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("trader_liesel_q0014_0104.htm"))
		{
			st.set("cond", "1");
			st.giveItems(LETTER_TO_ARCHAEOLOGIST, 1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("explorer_ghost_a_q0014_0201.htm"))
		{
			st.takeItems(LETTER_TO_ARCHAEOLOGIST, -1);
			st.addExpAndSp(325881, 32524, true);
			st.giveItems(ADENA_ID, 136928, true);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
			return "explorer_ghost_a_q0014_0201.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == 31263)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 74)
					htmltext = "trader_liesel_q0014_0101.htm";
				else
				{
					htmltext = "trader_liesel_q0014_0103.htm";
					st.exitCurrentQuest(true);
				}
			else if(cond == 1)
				htmltext = "trader_liesel_q0014_0104.htm";
		}
		else if(npcId == 31538)
			if(cond == 1 && st.getQuestItemsCount(LETTER_TO_ARCHAEOLOGIST) == 1)
				htmltext = "explorer_ghost_a_q0014_0101.htm";
		return htmltext;
	}
}