package quests._113_StatusOfTheBeaconTower;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * Last editor - LEXX
 */
public class _113_StatusOfTheBeaconTower extends Quest implements ScriptFile
{
	// NPC
	private static final int MOIRA = 31979;
	private static final int TORRANT = 32016;

	// QUEST ITEM
	private static final int BOX = 8086;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _113_StatusOfTheBeaconTower()
	{
		super(false);
		addStartNpc(MOIRA);
		addTalkId(TORRANT);

		addQuestItem(BOX);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("seer_moirase_q0113_0104.htm"))
		{
			st.set("cond", "1");
			st.giveItems(BOX, 1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("torant_q0113_0201.htm"))
		{
			st.giveItems(ADENA_ID, 154800);
			st.addExpAndSp(619300, 44200);
			st.takeItems(BOX, 1);
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
		int id = st.getState();
		int cond = st.getInt("cond");
		if(id == COMPLETED)
			htmltext = "completed";
		else if(npcId == MOIRA)
		{
			if(id == CREATED)
			{
				if(st.getPlayer().getLevel() >= 80)
					htmltext = "seer_moirase_q0113_0101.htm";
				else
				{
					htmltext = "seer_moirase_q0113_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "seer_moirase_q0113_0105.htm";
		}
		else if(npcId == TORRANT && st.getQuestItemsCount(BOX) == 1)
			htmltext = "torant_q0113_0101.htm";
		return htmltext;
	}
}