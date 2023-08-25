package quests._157_RecoverSmuggled;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _157_RecoverSmuggled extends Quest implements ScriptFile
{
	int ADAMANTITE_ORE_ID = 1024;
	int BUCKLER = 20;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _157_RecoverSmuggled()
	{
		super(false);

		addStartNpc(30005);

		addTalkId(30005);

		addKillId(20121);

		addQuestItem(ADAMANTITE_ORE_ID);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			st.set("id", "0");
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "wilph_q0157_05.htm";
		}
		else if(event.equals("157_1"))
		{
			htmltext = "wilph_q0157_04.htm";
			return htmltext;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if(id == CREATED)
		{
			st.set("cond", "0");
			st.set("id", "0");
		}
		if(npcId == 30005 && st.getInt("cond") == 0)
		{
			if(st.getInt("cond") < 15)
			{
				if(st.getPlayer().getLevel() >= 5)
					htmltext = "wilph_q0157_03.htm";
				else
				{
					htmltext = "wilph_q0157_02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "wilph_q0157_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == 30005 && st.getInt("cond") != 0 && st.getQuestItemsCount(ADAMANTITE_ORE_ID) < 20)
			htmltext = "wilph_q0157_06.htm";
		else if(npcId == 30005 && st.getInt("cond") != 0 && st.getQuestItemsCount(ADAMANTITE_ORE_ID) >= 20)
		{
			st.takeItems(ADAMANTITE_ORE_ID, st.getQuestItemsCount(ADAMANTITE_ORE_ID));
			st.playSound(SOUND_FINISH);
			st.giveItems(BUCKLER, 1);
			htmltext = "wilph_q0157_07.htm";
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == 20121)
		{
			st.set("id", "0");
			if(st.getInt("cond") != 0 && st.getQuestItemsCount(ADAMANTITE_ORE_ID) < 20 && Rnd.chance(14))
			{
				st.giveItems(ADAMANTITE_ORE_ID, 1);
				if(st.getQuestItemsCount(ADAMANTITE_ORE_ID) == 20)
					st.playSound(SOUND_MIDDLE);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}