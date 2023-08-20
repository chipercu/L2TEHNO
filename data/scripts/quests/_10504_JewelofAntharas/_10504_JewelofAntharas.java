package quests._10504_JewelofAntharas;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10504_JewelofAntharas extends Quest implements ScriptFile
{
	//NPC's
	private static final int THEODRIC = 30755;
	private static final int ULTIMATE_ANTHARAS = 29068;
	//Item's
	private static final int CLEAR_CRYSTAL = 21905;
	private static final int FILLED_CRYSTAL_ANTHARAS = 21907;
	private static final int PORTAL_STONE = 3865;
	private static final int JEWEL_OF_ANTHARAS = 21898;

	public _10504_JewelofAntharas()
	{
		super(PARTY_ALL);
		addStartNpc(THEODRIC);
		addQuestItem(CLEAR_CRYSTAL, FILLED_CRYSTAL_ANTHARAS);
		addKillId(ULTIMATE_ANTHARAS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("antharas_watchman_theodric_q10504_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(CLEAR_CRYSTAL, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == THEODRIC)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() < 84)
					htmltext = "antharas_watchman_theodric_q10504_00.htm";
				else if(st.getQuestItemsCount(PORTAL_STONE) < 1)
					htmltext = "antharas_watchman_theodric_q10504_00a.htm";
				else if(st.isNowAvailable())
					htmltext = "antharas_watchman_theodric_q10504_01.htm";
				else
					htmltext = "antharas_watchman_theodric_q10504_09.htm";
			}
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(CLEAR_CRYSTAL) < 1)
				{
					htmltext = "antharas_watchman_theodric_q10504_08.htm";
					st.giveItems(CLEAR_CRYSTAL, 1);
				}
				else
					htmltext = "antharas_watchman_theodric_q10504_05.htm";
			}
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(FILLED_CRYSTAL_ANTHARAS) >= 1)
				{
					htmltext = "antharas_watchman_theodric_q10504_07.htm";
					st.takeAllItems(FILLED_CRYSTAL_ANTHARAS);
					st.giveItems(JEWEL_OF_ANTHARAS, 1);
					st.playSound(SOUND_FINISH);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
				}
				else
					htmltext = "antharas_watchman_theodric_q10504_06.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == ULTIMATE_ANTHARAS)
		{
			st.takeAllItems(CLEAR_CRYSTAL);
			st.giveItems(FILLED_CRYSTAL_ANTHARAS, 1);
			st.playSound("ItemSound.quest_itemget");
			st.playSound("ItemSound.quest_middle");
			st.setCond(2);
		}
		return null;
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}