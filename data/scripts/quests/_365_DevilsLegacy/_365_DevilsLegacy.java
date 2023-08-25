package quests._365_DevilsLegacy;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

public class _365_DevilsLegacy extends Quest implements ScriptFile
{
	//NPC
	private static final int RANDOLF = 30095;
	private static final int COLLOB = 30092;

	//MOBS
	int[] MOBS = new int[] { 20836, 29027, 20845, 21629, 21630, 29026 };

	//VARIABLES
	private static final int CHANCE_OF_DROP = 25;
	private static final int REWARD_PER_ONE = 5070;

	//ITEMS
	private static final int TREASURE_CHEST = 5873;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _365_DevilsLegacy()
	{
		super(false);
		addStartNpc(RANDOLF);
		addTalkId(COLLOB);
		addKillId(MOBS);
		addQuestItem(TREASURE_CHEST);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30095-1.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30095-5.htm"))
		{
			long count = st.getQuestItemsCount(TREASURE_CHEST);
			if(count > 0)
			{
				long reward = count * REWARD_PER_ONE;
				st.takeItems(TREASURE_CHEST, -1);
				st.giveItems(ADENA_ID, reward);
			}
			else
				htmltext = "You don't have required items";
		}
		else if(event.equalsIgnoreCase("30095-6.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if(event.equalsIgnoreCase("30092_reward"))
		{
			if(st.getQuestItemsCount(TREASURE_CHEST) < 1)
			{
				htmltext = "collob_q0365_03.htm";
			}
			else if(st.getQuestItemsCount(57) < 600)
			{
				htmltext = "collob_q0365_04.htm";
			}
			else if(st.getInt("cond") == 0)
			{
				htmltext = "collob_q0365_05.htm";
			}
			else if(st.getQuestItemsCount(TREASURE_CHEST) >= 1 && st.getQuestItemsCount(57) >= 600 && st.getInt("cond") == 1)
			{
				if(Rnd.get(100) < 80)
				{
					int i0 = Rnd.get(100);
					if(i0 < 1)
					{
						st.giveItems(995, 1);
					}
					else if(i0 < 4)
					{
						st.giveItems(956, 1);
					}
					else if(i0 < 36)
					{
						st.giveItems(1868, 1);
					}
					else if(i0 < 68)
					{
						st.giveItems(1884, 1);
					}
					else
					{
						st.giveItems(1872, 1);
					}
					st.takeItems(TREASURE_CHEST, 1);
					st.takeItems(57, 600);
					htmltext = "collob_q0365_06.htm";
				}
				else
				{
					int i0 = Rnd.get(1000);
					if(i0 < 10)
					{
						st.giveItems(951,1);
					}
					else if(i0 < 40)
					{
						st.giveItems(952,1);
					}
					else if(i0 < 60)
					{
						st.giveItems(955,1);
					}
					else if(i0 < 260)
					{
						st.giveItems(956,1);
					}
					else if(i0 < 445)
					{
						st.giveItems(1879,1);
					}
					else if(i0 < 630)
					{
						st.giveItems(1880,1);
					}
					else if(i0 < 815)
					{
						st.giveItems(1882,1);
					}
					else
					{
						st.giveItems(1881,1);
					}
					SkillTable.getInstance().getInfo(4035, 2).getEffects(st.getPlayer(), st.getPlayer(), false, false);
					st.takeItems(TREASURE_CHEST,1);
					st.takeItems(57,600);
					htmltext = "collob_q0365_07.htm";
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == RANDOLF)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 39)
					htmltext = "30095-0.htm";
				else
				{
					htmltext = "30095-0a.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				if(st.getQuestItemsCount(TREASURE_CHEST) == 0)
					htmltext = "30095-2.htm";
				else
					htmltext = "30095-4.htm";
		}
		if(npcId == COLLOB)
		{
			if(cond == 0)
			{
				htmltext = "collob_q0365_02.htm";
			}
			else if(cond == 1)
			{
				htmltext = "collob_q0365_01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(Rnd.chance(CHANCE_OF_DROP))
		{
			st.giveItems(TREASURE_CHEST, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}