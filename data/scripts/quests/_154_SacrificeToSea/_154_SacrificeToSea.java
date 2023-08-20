package quests._154_SacrificeToSea;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _154_SacrificeToSea extends Quest implements ScriptFile
{
	private static final int FOX_FUR_ID = 1032;
	private static final int FOX_FUR_YARN_ID = 1033;
	private static final int MAIDEN_DOLL_ID = 1034;
	private static final int MYSTICS_EARRING_ID = 113;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _154_SacrificeToSea()
	{
		super(false);

		addStartNpc(30312);

		addTalkId(30051);
		addTalkId(30055);

		addKillId(20481);
		addKillId(20544);
		addKillId(20545);

		addQuestItem(new int[] { FOX_FUR_ID, FOX_FUR_YARN_ID, MAIDEN_DOLL_ID });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			st.set("id", "0");
			htmltext = "rockswell_q0154_04.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
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
			st.setState(STARTED);
			st.set("cond", "0");
			st.set("id", "0");
		}
		if(npcId == 30312 && st.getInt("cond") == 0)
		{
			if(st.getInt("cond") < 15)
			{
				if(st.getPlayer().getLevel() >= 2)
				{
					htmltext = "rockswell_q0154_03.htm";
					return htmltext;
				}
				htmltext = "rockswell_q0154_02.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "rockswell_q0154_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == 30312 && st.getInt("cond") == 0)
			htmltext = "completed";
		else if(npcId == 30312 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 0 && st.getQuestItemsCount(FOX_FUR_ID) < 10)
			htmltext = "rockswell_q0154_05.htm";
		else if(npcId == 30312 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_ID) >= 10)
			htmltext = "rockswell_q0154_08.htm";
		else if(npcId == 30051 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_ID) < 10 && st.getQuestItemsCount(FOX_FUR_ID) > 0)
			htmltext = "cristel_q0154_01.htm";
		else if(npcId == 30051 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_ID) >= 10 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) < 10)
		{
			htmltext = "cristel_q0154_02.htm";
			st.giveItems(FOX_FUR_YARN_ID, 1);
			st.takeItems(FOX_FUR_ID, st.getQuestItemsCount(FOX_FUR_ID));
		}
		else if(npcId == 30051 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) >= 1)
			htmltext = "cristel_q0154_03.htm";
		else if(npcId == 30051 && st.getInt("cond") == 1 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 1)
			htmltext = "cristel_q0154_04.htm";
		else if(npcId == 30312 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) >= 1)
			htmltext = "rockswell_q0154_06.htm";
		else if(npcId == 30055 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) >= 1)
		{
			htmltext = "rollfnan_q0154_01.htm";
			st.giveItems(MAIDEN_DOLL_ID, 1);
			st.takeItems(FOX_FUR_YARN_ID, st.getQuestItemsCount(FOX_FUR_YARN_ID));
		}
		else if(npcId == 30055 && st.getInt("cond") == 1 && st.getQuestItemsCount(MAIDEN_DOLL_ID) >= 1)
			htmltext = "rollfnan_q0154_02.htm";
		else if(npcId == 30055 && st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 0)
			htmltext = "rollfnan_q0154_03.htm";
		else if(npcId == 30312 && st.getInt("cond") == 1 && st.getQuestItemsCount(MAIDEN_DOLL_ID) >= 1)
			if(st.getInt("id") != 154)
			{
				st.set("id", "154");
				htmltext = "rockswell_q0154_07.htm";
				st.takeItems(MAIDEN_DOLL_ID, st.getQuestItemsCount(MAIDEN_DOLL_ID));
				st.giveItems(MYSTICS_EARRING_ID, 1);
				st.addExpAndSp(1000, 0);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0)
			st.rollAndGive(FOX_FUR_ID, 1, 1, 10, 14);
		return null;
	}
}