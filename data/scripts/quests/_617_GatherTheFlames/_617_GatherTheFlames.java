package quests._617_GatherTheFlames;

import java.io.File;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _617_GatherTheFlames extends Quest implements ScriptFile
{
	//npc
	private final static int VULCAN = 31539;
	private final static int HILDA = 31271;
	//items
	private final static int TORCH = 7264;
	//DROPLIST (MOB_ID, CHANCE)
	private final static int[][] DROPLIST = {
			{ 22634, 48 },
			{ 22635, 48 },
			{ 22636, 48 },
			{ 22638, 48 },
			{ 22639, 51 },
			{ 22641, 51 },
			{ 22647, 51 },
			{ 22642, 51 },
			{ 22643, 51 },
			{ 22644, 53 },
			{ 22646, 56 },
			{ 18799, 56 },
			{ 22645, 56 },
			{ 21391, 56 },
			{ 22648, 56 },
			{ 22649, 58 },
			{ 22640, 60 },
			{ 22637, 60 },
			{ 18800, 64 },
			{ 18801, 53 },
			{ 18802, 60 },
			{ 18803, 59 } };

	public static final int[] Recipes = { 6881, 6883, 6885, 6887, 7580, 6891, 6893, 6895, 6897, 6899 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _617_GatherTheFlames()
	{
		super(true);

		addStartNpc(VULCAN);
		addStartNpc(HILDA);

		for(int[] element : DROPLIST)
			addKillId(element[0]);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31539-03.htm")) //VULCAN
		{
			if(st.getPlayer().getLevel() < 74)
				return "31539-02.htm";
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
		}
		else if(event.equalsIgnoreCase("31271-03.htm")) //HILDA
		{
			if(st.getPlayer().getLevel() < 74)
				return "31271-01.htm";
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
		}
		else if(event.equalsIgnoreCase("31539-08.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(TORCH, -1);
			st.exitCurrentQuest(true);
		}
		else if(event.equalsIgnoreCase("31539-07.htm"))
		{
			if(st.getQuestItemsCount(TORCH) < 1000)
				return "31539-05.htm";
			st.takeItems(TORCH, 1000);
			st.giveItems(Recipes[Rnd.get(Recipes.length)] + (ConfigValue.Alt100PercentRecipesS ? 1 : 0), 1);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == VULCAN)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() < 74)
				{
					htmltext = "31539-02.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "31539-01.htm";
			}
			else
				htmltext = st.getQuestItemsCount(TORCH) < 1000 ? "31539-05.htm" : "31539-04.htm";
		}
		else if(npcId == HILDA)
			if(cond < 1)
				htmltext = st.getPlayer().getLevel() < 74 ? "31271-01.htm" : "31271-02.htm";
			else
				htmltext = "31271-04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		for(int[] element : DROPLIST)
			if(npc.getNpcId() == element[0])
			{
				st.rollAndGive(TORCH, 1, element[1]);
				return null;
			}
		return null;
	}
}