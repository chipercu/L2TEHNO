package quests._159_ProtectHeadsprings;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _159_ProtectHeadsprings extends Quest implements ScriptFile
{
	int PLAGUE_DUST_ID = 1035;
	int HYACINTH_CHARM1_ID = 1071;
	int HYACINTH_CHARM2_ID = 1072;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _159_ProtectHeadsprings()
	{
		super(false);

		addStartNpc(30154);

		addKillId(27017);

		addQuestItem(new int[] { PLAGUE_DUST_ID, HYACINTH_CHARM1_ID, HYACINTH_CHARM2_ID });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			if(st.getQuestItemsCount(HYACINTH_CHARM1_ID) == 0)
			{
				st.giveItems(HYACINTH_CHARM1_ID, 1);
				htmltext = "ozzy_q0159_04.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(cond == 0)
		{
			if(st.getPlayer().getRace() != Race.elf)
			{
				htmltext = "ozzy_q0159_00.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getPlayer().getLevel() >= 12)
			{
				htmltext = "ozzy_q0159_03.htm";
				return htmltext;
			}
			else
			{
				htmltext = "ozzy_q0159_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "ozzy_q0159_05.htm";
		else if(cond == 2)
		{
			st.takeItems(PLAGUE_DUST_ID, -1);
			st.takeItems(HYACINTH_CHARM1_ID, -1);
			st.giveItems(HYACINTH_CHARM2_ID, 1);
			st.set("cond", "3");
			htmltext = "ozzy_q0159_06.htm";
		}
		else if(cond == 3)
			htmltext = "ozzy_q0159_07.htm";
		else if(cond == 4)
		{
			st.takeItems(PLAGUE_DUST_ID, -1);
			st.takeItems(HYACINTH_CHARM2_ID, -1);
			st.giveItems(ADENA_ID, 18250);
			st.playSound(SOUND_FINISH);
			htmltext = "ozzy_q0159_08.htm";
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");

		if(cond == 1 && Rnd.chance(60))
		{
			st.giveItems(PLAGUE_DUST_ID, 1);
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(cond == 3 && Rnd.chance(60))
			if(st.getQuestItemsCount(PLAGUE_DUST_ID) == 4)
			{
				st.giveItems(PLAGUE_DUST_ID, 1);
				st.set("cond", "4");
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				st.giveItems(PLAGUE_DUST_ID, 1);
				st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}