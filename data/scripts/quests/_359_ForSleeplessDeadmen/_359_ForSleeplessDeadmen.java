package quests._359_ForSleeplessDeadmen;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._713_PathToBecomingALordAden._713_PathToBecomingALordAden;

public class _359_ForSleeplessDeadmen extends Quest implements ScriptFile
{

	//Variables
	private static final int DROP_RATE = 10;

	private static final int REQUIRED = 60; //how many items will be paid for a reward

	//Quest items
	private static final int REMAINS = 5869;

	//Rewards
	private static final int PhoenixEarrPart = 6341;
	private static final int MajEarrPart = 6342;
	private static final int PhoenixNeclPart = 6343;
	private static final int MajNeclPart = 6344;
	private static final int PhoenixRingPart = 6345;
	private static final int MajRingPart = 6346;

	private static final int DarkCryShieldPart = 5494;
	private static final int NightmareShieldPart = 5495;

	//NPCs
	private static final int ORVEN = 30857;

	//Mobs
	private static final int DOOMSERVANT = 21006;
	private static final int DOOMGUARD = 21007;
	private static final int DOOMARCHER = 21008;
	private static final int DOOMTROOPER = 21009;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _359_ForSleeplessDeadmen()
	{
		super(false);
		addStartNpc(ORVEN);

		addKillId(DOOMSERVANT);
		addKillId(DOOMGUARD);
		addKillId(DOOMARCHER);
		addKillId(DOOMTROOPER);

		addQuestItem(REMAINS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance myself)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30857-06.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30857-07.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
		}
		else if(event.equalsIgnoreCase("30857-08.htm"))
		{
			st.set("cond", "1");
			//Vibor nagradi
			int chance = Rnd.get(100);
			int item;
			if(chance <= 16)
				item = PhoenixNeclPart;
			else if(chance <= 33)
				item = PhoenixEarrPart;
			else if(chance <= 50)
				item = PhoenixRingPart;
			else if(chance <= 58)
				item = MajNeclPart;
			else if(chance <= 67)
				item = MajEarrPart;
			else if(chance <= 76)
				item = MajRingPart;
			else if(chance <= 84)
				item = DarkCryShieldPart;
			else
				item = NightmareShieldPart;
			st.giveItems(item, 4, true);

			L2Player c0 = myself.Pledge_GetLeader(st.getPlayer());
			if(myself.IsNullCreature(c0) == 0)
			{
				if(myself.HaveMemo(c0,713) == 1 && (myself.GetMemoState(c0,713) % 100 == 2 || myself.GetMemoState(c0,713) % 100 == 12) && myself.GetMemoState(c0, 713) / 100 < 5)
				{
					int i1 = myself.GetMemoState(c0,713);
					if((i1 / 100) >= 4)
					{
						if((i1 % 100) == 2)
						{
							myself.SetFlagJournal(c0,713,4);
						}
						else if((i1 % 100) == 12)
						{
							myself.SetFlagJournal(c0,713,6);
						}
					}
					myself.SetMemoState(c0,713,(i1 + 100));
					myself.ShowQuestMark(c0,713);
					myself.SoundEffect(c0,"ItemSound.quest_middle");
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getInt("cond");
		if(id == CREATED)
		{
			if(st.getPlayer().getLevel() < 60)
			{
				st.exitCurrentQuest(true);
				htmltext = "30857-01.htm";
			}
			else
				htmltext = "30857-02.htm";
		}
		else if(id == STARTED)
		{
			if(cond == 3)
				htmltext = "30857-03.htm";
			else if(cond == 2 && st.getQuestItemsCount(REMAINS) >= REQUIRED)
			{
				st.takeItems(REMAINS, REQUIRED);
				st.set("cond", "3");
				htmltext = "30857-04.htm";
			}
		}
		else
			htmltext = "30857-05.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(REMAINS) < REQUIRED && Rnd.chance(DROP_RATE))
		{
			st.giveItems(REMAINS, 1);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getInt("cond") == 1)
		{
			if(st.getQuestItemsCount(REMAINS) >= REQUIRED)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
		}
		return null;
	}
}