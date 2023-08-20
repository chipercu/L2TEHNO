package quests._040_ASpecialOrder;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _040_ASpecialOrder extends Quest implements ScriptFile
{
	//NPC
	private final static int HELVETIA = 30081;
	private final static int OFULLE = 31572;
	private final static int GESTO = 30511;
	//ITEMS
	private final static short ORANGE_NIMBLE_FISH = 6450;
	private final static short ORANGE_UNGLY_FISH = 6451;
	private final static short ORANGE_FAT_FISH = 6452;
	private final static short FISH_CHEST = 12764;
	private final static short GOLDEN_COBOL = 5079;
	private final static short THORN_COBOL = 5082;
	private final static short GREAT_COBOL = 5084;
	private final static short SEED_JAR = 12765;
	private final static short WONDROUS_CUBIC = 10632;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _040_ASpecialOrder()
	{
		super(false);

		addStartNpc(HELVETIA);
		addTalkId(new int[] { OFULLE, GESTO });
		addQuestItem(new int[] { FISH_CHEST, SEED_JAR });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30081-02.htm"))
		{
			if(Rnd.chance(50))
			{
				st.set("cond", "2");
				htmltext = "30081-02a.htm";
			}
			else
			{
				st.set("cond", "5");
				htmltext = "30081-02b.htm";
			}
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30511-03.htm"))
		{
			st.set("cond", "6");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31572-03.htm"))
		{
			st.set("cond", "3");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("30081-05a.htm"))
		{
			st.takeItems(FISH_CHEST, 1);
			st.giveItems(WONDROUS_CUBIC, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("30081-05b.htm"))
		{
			st.takeItems(SEED_JAR, 1);
			st.giveItems(WONDROUS_CUBIC, 1);
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
		if(st.getState() == CREATED && npcId == HELVETIA)
		{
			if(st.getQuestItemsCount(WONDROUS_CUBIC) > 0)
				st.exitCurrentQuest(true);
			else if(st.getPlayer().getLevel() < 40)
			{
				htmltext = "30081-00.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "30081-01.htm";
				st.set("cond", "1");
				st.setState(STARTED);
			}
		}
		else if(st.isStarted())
		{
			switch(npcId)
			{
				case HELVETIA:
				{
					if(cond == 2)
						htmltext = "30081-03a.htm";
					else if(cond == 5)
						htmltext = "30081-03b.htm";
					else if(cond == 4)
						htmltext = "30081-04a.htm";
					else if(cond == 7)
						htmltext = "30081-04b.htm";
					break;
				}
				case OFULLE:
				{
					if(cond == 2)
						htmltext = "31572-01.htm";
					else if(cond == 3)
						if(st.getQuestItemsCount(ORANGE_NIMBLE_FISH) >= 10 && st.getQuestItemsCount(ORANGE_UNGLY_FISH) >= 10 && st.getQuestItemsCount(ORANGE_FAT_FISH) >= 10)
						{
							st.set("cond", "4");
							st.setState(STARTED);
							st.takeItems(ORANGE_NIMBLE_FISH, 10);
							st.takeItems(ORANGE_UNGLY_FISH, 10);
							st.takeItems(ORANGE_FAT_FISH, 10);
							st.giveItems(FISH_CHEST, 1);
							htmltext = "31572-04.htm";
						}
						else
							htmltext = "31572-05.htm";
					else if(cond == 4)
						htmltext = "31572-06.htm";
					break;
				}
				case GESTO:
				{
					if(cond == 5)
						htmltext = "30511-01.htm";
					else if(cond == 6)
						if(st.getQuestItemsCount(GOLDEN_COBOL) >= 40 && st.getQuestItemsCount(THORN_COBOL) >= 40 && st.getQuestItemsCount(GREAT_COBOL) >= 40)
						{
							st.set("cond", "7");
							st.setState(STARTED);
							st.takeItems(GOLDEN_COBOL, 40);
							st.takeItems(THORN_COBOL, 40);
							st.takeItems(GREAT_COBOL, 40);
							st.giveItems(SEED_JAR, 1);
							htmltext = "30511-04.htm";
						}
						else
							htmltext = "30511-05.htm";
					else if(cond == 7)
						htmltext = "30511-06.htm";
					break;
				}
			}
		}
		return htmltext;
	}
}