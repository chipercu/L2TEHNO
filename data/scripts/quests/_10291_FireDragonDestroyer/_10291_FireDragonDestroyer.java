package quests._10291_FireDragonDestroyer;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10291_FireDragonDestroyer extends Quest implements ScriptFile
{
	private static final int Klein = 31540;
	private static final int PoorNecklace = 15524;
	private static final int ValorNecklace = 15525;
	private static final int Valakas = 29028;

	public _10291_FireDragonDestroyer()
	{
		super(PARTY_ALL);
		addStartNpc(Klein);
		addQuestItem(PoorNecklace, ValorNecklace);
		addKillId(Valakas);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klein_q10291_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(PoorNecklace, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Klein)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 83 && st.getQuestItemsCount(7267) >= 1)
					htmltext = "klein_q10291_01.htm";
				else if(st.getQuestItemsCount(7267) < 1)
					htmltext = "klein_q10291_00a.htm";
				else
					htmltext = "klein_q10291_00.htm";
			}
			else if(cond == 1)
				htmltext = "klein_q10291_05.htm";
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(ValorNecklace) >= 1)
				{
					htmltext = "klein_q10291_07.htm";
					st.takeAllItems(ValorNecklace);
					st.giveItems(8567, 1);
					st.giveItems(ADENA_ID, 126549);
					st.addExpAndSp(717291, 77397);
					st.playSound(SOUND_FINISH);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
				}
				else
					htmltext = "klein_q10291_06.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
    {
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == Valakas)
		{
			st.takeAllItems(PoorNecklace);
			st.giveItems(ValorNecklace, 1);
			st.setCond(2);
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}