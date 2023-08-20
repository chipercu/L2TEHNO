package quests._379_FantasyWine;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _379_FantasyWine extends Quest implements ScriptFile
{
	//NPC
	public final int HARLAN = 30074;
	//Mobs
	public final int Enku_Orc_Champion = 20291;
	public final int Enku_Orc_Shaman = 20292;
	//Quest Item
	public final int LEAF_OF_EUCALYPTUS = 5893;
	public final int STONE_OF_CHILL = 5894;
	//Item
	public final int[] REWARD = { 5956, 5957, 5958 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _379_FantasyWine()
	{
		super(false);

		addStartNpc(HARLAN);

		addKillId(Enku_Orc_Champion);
		addKillId(Enku_Orc_Shaman);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hitsran_q0379_06.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("reward"))
		{
			st.takeItems(LEAF_OF_EUCALYPTUS, -1);
			st.takeItems(STONE_OF_CHILL, -1);
			int rand = Rnd.get(100);
			if(rand < 25)
			{
				st.giveItems(REWARD[0], 1);
				htmltext = "hitsran_q0379_11.htm";
			}
			else if(rand < 50)
			{
				st.giveItems(REWARD[1], 1);
				htmltext = "hitsran_q0379_12.htm";
			}
			else
			{
				st.giveItems(REWARD[2], 1);
				htmltext = "hitsran_q0379_13.htm";
			}
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if(event.equalsIgnoreCase("hitsran_q0379_05.htm"))
			st.exitCurrentQuest(true);
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if(id != CREATED)
			cond = st.getInt("cond");
		if(npcId == HARLAN)
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() < 20)
				{
					htmltext = "hitsran_q0379_01.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "hitsran_q0379_02.htm";
			}
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) < 80 && st.getQuestItemsCount(STONE_OF_CHILL) < 100)
					htmltext = "hitsran_q0379_07.htm";
				else if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) == 80 && st.getQuestItemsCount(STONE_OF_CHILL) < 100)
					htmltext = "hitsran_q0379_08.htm";
				else if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) < 80 && st.getQuestItemsCount(STONE_OF_CHILL) == 100)
					htmltext = "hitsran_q0379_09.htm";
				else
					htmltext = "hitsran_q0379_02.htm";
			}
			else if(cond == 2)
				if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) >= 80 && st.getQuestItemsCount(STONE_OF_CHILL) >= 100)
					htmltext = "hitsran_q0379_10.htm";
				else
				{
					st.set("cond", "1");
					if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) < 80 && st.getQuestItemsCount(STONE_OF_CHILL) < 100)
						htmltext = "hitsran_q0379_07.htm";
					else if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) >= 80 && st.getQuestItemsCount(STONE_OF_CHILL) < 100)
						htmltext = "hitsran_q0379_08.htm";
					else if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) < 80 && st.getQuestItemsCount(STONE_OF_CHILL) >= 100)
						htmltext = "hitsran_q0379_09.htm";
				}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getInt("cond") == 1)
		{
			if(npcId == Enku_Orc_Champion && st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) < 80)
				st.giveItems(LEAF_OF_EUCALYPTUS, 1);
			else if(npcId == Enku_Orc_Shaman && st.getQuestItemsCount(STONE_OF_CHILL) < 100)
				st.giveItems(STONE_OF_CHILL, 1);
			if(st.getQuestItemsCount(LEAF_OF_EUCALYPTUS) >= 80 && st.getQuestItemsCount(STONE_OF_CHILL) >= 100)
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}