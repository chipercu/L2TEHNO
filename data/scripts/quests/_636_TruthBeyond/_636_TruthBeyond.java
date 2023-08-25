package quests._636_TruthBeyond;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _636_TruthBeyond extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	//Npc
	public final int ELIYAH = 31329;
	public final int FLAURON = 32010;

	//Items
	public final int VISITORSMARK = 8064;
	public final int FADED_MARK = 8065;
	public final int PERMANENT_MARK = 8067;

	public _636_TruthBeyond()
	{
		super(false);

		addStartNpc(ELIYAH);
		addTalkId(FLAURON);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equals("priest_eliyah_q0636_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("falsepriest_flauron_q0636_02.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_FINISH);
			st.giveItems(VISITORSMARK, 1);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if(npcId == ELIYAH && cond == 0)
		{
			if(st.getQuestItemsCount(VISITORSMARK, FADED_MARK, PERMANENT_MARK) == 0)
			{
				if(st.getPlayer().getLevel() > 72)
					htmltext = "priest_eliyah_q0636_01.htm";
				else
				{
					htmltext = "priest_eliyah_q0636_03.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
				htmltext = "priest_eliyah_q0636_06.htm";
		}
		else if(npcId == FLAURON)
			if(cond == 1 || st.getQuestItemsCount(VISITORSMARK, FADED_MARK, PERMANENT_MARK) == 0)
				htmltext = "falsepriest_flauron_q0636_01.htm";
			else
				htmltext = "falsepriest_flauron_q0636_03.htm";
		return htmltext;
	}
}