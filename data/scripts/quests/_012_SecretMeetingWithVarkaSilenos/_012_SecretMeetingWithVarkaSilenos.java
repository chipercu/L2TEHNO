package quests._012_SecretMeetingWithVarkaSilenos;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _012_SecretMeetingWithVarkaSilenos extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	int CADMON = 31296;
	int HELMUT = 31258;
	int NARAN_ASHANUK = 31378;

	int MUNITIONS_BOX = 7232;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _012_SecretMeetingWithVarkaSilenos()
	{
		super(false);

		addStartNpc(CADMON);

		addTalkId(HELMUT);
		addTalkId(NARAN_ASHANUK);

		addQuestItem(MUNITIONS_BOX);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("guard_cadmon_q0012_0104.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("trader_helmut_q0012_0201.htm"))
		{
			st.giveItems(MUNITIONS_BOX, 1);
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("herald_naran_q0012_0301.htm"))
		{
			st.takeItems(MUNITIONS_BOX, 1);
			st.addExpAndSp(233125, 18142, true);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == CADMON)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 74)
					htmltext = "guard_cadmon_q0012_0101.htm";
				else
				{
					htmltext = "guard_cadmon_q0012_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "guard_cadmon_q0012_0105.htm";
		}
		else if(npcId == HELMUT)
		{
			if(cond == 1)
				htmltext = "trader_helmut_q0012_0101.htm";
			else if(cond == 2)
				htmltext = "trader_helmut_q0012_0202.htm";
		}
		else if(npcId == NARAN_ASHANUK)
			if(cond == 2 && st.getQuestItemsCount(MUNITIONS_BOX) > 0)
				htmltext = "herald_naran_q0012_0201.htm";
		return htmltext;
	}
}
