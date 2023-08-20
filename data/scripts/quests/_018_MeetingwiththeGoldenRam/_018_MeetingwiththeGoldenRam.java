package quests._018_MeetingwiththeGoldenRam;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _018_MeetingwiththeGoldenRam extends Quest implements ScriptFile
{
	private static final int SUPPLY_BOX = 7245;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _018_MeetingwiththeGoldenRam()
	{
		super(false);

		addStartNpc(31314);

		addTalkId(31314);
		addTalkId(31315);
		addTalkId(31555);

		addQuestItem(SUPPLY_BOX);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("warehouse_chief_donal_q0018_0104.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("freighter_daisy_q0018_0201.htm"))
		{
			st.set("cond", "2");
			st.giveItems(SUPPLY_BOX, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("supplier_abercrombie_q0018_0301.htm"))
		{
			st.takeItems(SUPPLY_BOX, -1);
			st.addExpAndSp(126668, 11731);
			st.giveItems(ADENA_ID, 40000);
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
		if(npcId == 31314)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 66)
					htmltext = "warehouse_chief_donal_q0018_0101.htm";
				else
				{
					htmltext = "warehouse_chief_donal_q0018_0103.htm";
					st.exitCurrentQuest(true);
				}
			else if(cond == 1)
				htmltext = "warehouse_chief_donal_q0018_0105.htm";
		}
		else if(npcId == 31315)
		{
			if(cond == 1)
				htmltext = "freighter_daisy_q0018_0101.htm";
			else if(cond == 2)
				htmltext = "freighter_daisy_q0018_0202.htm";
		}
		else if(npcId == 31555)
			if(cond == 2 && st.getQuestItemsCount(SUPPLY_BOX) == 1)
				htmltext = "supplier_abercrombie_q0018_0201.htm";
		return htmltext;
	}
}