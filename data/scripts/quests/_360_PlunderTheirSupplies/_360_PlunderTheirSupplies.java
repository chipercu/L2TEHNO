package quests._360_PlunderTheirSupplies;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _360_PlunderTheirSupplies extends Quest implements ScriptFile
{
	//NPC
	private static final int COLEMAN = 30873;

	//MOBS
	private static final int TAIK_SEEKER = 20666;
	private static final int TAIK_LEADER = 20669;

	//QUEST ITEMS
	private static final int SUPPLY_ITEM = 5872;
	private static final int SUSPICIOUS_DOCUMENT = 5871;
	private static final int RECIPE_OF_SUPPLY = 5870;

	//DROP CHANCES
	private static final int ITEM_DROP_SEEKER = 50;
	private static final int ITEM_DROP_LEADER = 65;
	private static final int DOCUMENT_DROP = 5;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _360_PlunderTheirSupplies()
	{
		super(false);
		addStartNpc(COLEMAN);

		addKillId(TAIK_SEEKER);
		addKillId(TAIK_LEADER);

		addQuestItem(SUPPLY_ITEM);
		addQuestItem(SUSPICIOUS_DOCUMENT);
		addQuestItem(RECIPE_OF_SUPPLY);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("guard_coleman_q0360_04.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("guard_coleman_q0360_10.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		long docs = st.getQuestItemsCount(RECIPE_OF_SUPPLY);
		long supplies = st.getQuestItemsCount(SUPPLY_ITEM);
		if(id != STARTED)
		{
			if(st.getPlayer().getLevel() >= 52)
				htmltext = "guard_coleman_q0360_02.htm";
			else
				htmltext = "guard_coleman_q0360_01.htm";
		}
		else if(docs > 0 || supplies > 0)
		{
			long reward = 6000 + supplies * 100 + docs * 6000;
			st.takeItems(SUPPLY_ITEM, -1);
			st.takeItems(RECIPE_OF_SUPPLY, -1);
			st.giveItems(ADENA_ID, reward);
			htmltext = "guard_coleman_q0360_08.htm";
		}
		else
			htmltext = "guard_coleman_q0360_05.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == TAIK_SEEKER && Rnd.chance(ITEM_DROP_SEEKER) || npcId == TAIK_LEADER && Rnd.chance(ITEM_DROP_LEADER))
		{
			st.giveItems(SUPPLY_ITEM, 1);
			st.playSound(SOUND_ITEMGET);
		}
		if(Rnd.chance(DOCUMENT_DROP))
		{
			if(st.getQuestItemsCount(SUSPICIOUS_DOCUMENT) < 4)
				st.giveItems(SUSPICIOUS_DOCUMENT, 1);
			else
			{
				st.takeItems(SUSPICIOUS_DOCUMENT, -1);
				st.giveItems(RECIPE_OF_SUPPLY, 1);
			}
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}