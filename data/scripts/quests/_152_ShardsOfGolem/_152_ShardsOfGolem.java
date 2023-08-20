package quests._152_ShardsOfGolem;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _152_ShardsOfGolem extends Quest implements ScriptFile
{
	int HARRYS_RECEIPT1 = 1008;
	int HARRYS_RECEIPT2 = 1009;
	int GOLEM_SHARD = 1010;
	int TOOL_BOX = 1011;
	int WOODEN_BP = 23;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _152_ShardsOfGolem()
	{
		super(false);

		addStartNpc(30035);

		addTalkId(30035);
		addTalkId(30035);
		addTalkId(30283);
		addTalkId(30035);

		addKillId(20016);
		addKillId(20101);

		addQuestItem(new int[] { HARRYS_RECEIPT1, GOLEM_SHARD, TOOL_BOX, HARRYS_RECEIPT2 });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("harry_q0152_04.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			if(st.getQuestItemsCount(HARRYS_RECEIPT1) == 0)
				st.giveItems(HARRYS_RECEIPT1, 1);
		}
		else if(event.equals("152_2"))
		{
			st.takeItems(HARRYS_RECEIPT1, -1);
			if(st.getQuestItemsCount(HARRYS_RECEIPT2) == 0)
			{
				st.giveItems(HARRYS_RECEIPT2, 1);
				st.set("cond", "2");
			}
			htmltext = "blacksmith_alltran_q0152_02.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(npcId == 30035)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 10)
				{
					htmltext = "harry_q0152_03.htm";
					return htmltext;
				}
				htmltext = "harry_q0152_02.htm";
				st.exitCurrentQuest(true);
			}
			else if(cond == 1 && st.getQuestItemsCount(HARRYS_RECEIPT1) != 0)
				htmltext = "harry_q0152_05.htm";
			else if(cond == 2 && st.getQuestItemsCount(HARRYS_RECEIPT2) != 0)
				htmltext = "harry_q0152_05.htm";
			else if(cond == 4 && st.getQuestItemsCount(TOOL_BOX) != 0)
			{
				st.takeItems(TOOL_BOX, -1);
				st.takeItems(HARRYS_RECEIPT2, -1);
				st.set("cond", "0");
				st.playSound(SOUND_FINISH);
				st.giveItems(WOODEN_BP, 1);
				st.addExpAndSp(5000, 0);
				htmltext = "harry_q0152_06.htm";
				st.exitCurrentQuest(false);
			}
		}
		else if(npcId == 30283)
		{
			if(cond == 1 && st.getQuestItemsCount(HARRYS_RECEIPT1) != 0)
				htmltext = "blacksmith_alltran_q0152_01.htm";
			else if(cond == 2 && st.getQuestItemsCount(HARRYS_RECEIPT2) != 0 && st.getQuestItemsCount(GOLEM_SHARD) < 5)
				htmltext = "blacksmith_alltran_q0152_03.htm";
			else if(cond == 3 && st.getQuestItemsCount(HARRYS_RECEIPT2) != 0 && st.getQuestItemsCount(GOLEM_SHARD) == 5)
			{
				st.takeItems(GOLEM_SHARD, -1);
				if(st.getQuestItemsCount(TOOL_BOX) == 0)
				{
					st.giveItems(TOOL_BOX, 1);
					st.set("cond", "4");
				}
				htmltext = "blacksmith_alltran_q0152_04.htm";
			}
		}
		else if(cond == 4 && st.getQuestItemsCount(HARRYS_RECEIPT2) != 0 && st.getQuestItemsCount(TOOL_BOX) != 0)
			htmltext = "blacksmith_alltran_q0152_05.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 2 && Rnd.chance(30) && st.getQuestItemsCount(GOLEM_SHARD) < 5)
		{
			st.giveItems(GOLEM_SHARD, 1);
			if(st.getQuestItemsCount(GOLEM_SHARD) == 5)
			{
				st.set("cond", "3");
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}