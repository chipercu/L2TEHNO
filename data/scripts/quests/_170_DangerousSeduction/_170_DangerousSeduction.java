package quests._170_DangerousSeduction;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _170_DangerousSeduction extends Quest implements ScriptFile
{
	//NPC
	private static final int Vellior = 30305;
	//Quest Items
	private static final int NightmareCrystal = 1046;
	//MOB
	private static final int Merkenis = 27022;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _170_DangerousSeduction()
	{
		super(false);
		addStartNpc(Vellior);
		addTalkId(Vellior);
		addKillId(Merkenis);
		addQuestItem(NightmareCrystal);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30305-04.htm"))
		{
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
		int cond = st.getInt("cond");
		if(npcId == Vellior)
			if(cond == 0)
			{
				if(st.getPlayer().getRace() != Race.darkelf)
				{
					htmltext = "30305-00.htm";
					st.exitCurrentQuest(true);
				}
				else if(st.getPlayer().getLevel() < 21)
				{
					htmltext = "30305-02.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "30305-03.htm";
			}
			else if(cond == 1)
				htmltext = "30305-05.htm";
			else if(cond == 2)
			{
				st.takeItems(NightmareCrystal, -1);
				st.giveItems(ADENA_ID, 102680, true);
				st.addExpAndSp(38607, 4018);
				htmltext = "30305-06.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(cond == 1 && npcId == Merkenis)
		{
			if(st.getQuestItemsCount(NightmareCrystal) == 0)
				st.giveItems(NightmareCrystal, 1);
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
			st.setState(STARTED);
		}
		return null;
	}
}