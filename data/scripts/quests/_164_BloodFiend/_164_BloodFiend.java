package quests._164_BloodFiend;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _164_BloodFiend extends Quest implements ScriptFile
{
	//NPC
	private static final int Creamees = 30149;
	//Quest Items
	private static final int KirunakSkull = 1044;
	//MOB
	private static final int Kirunak = 27021;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _164_BloodFiend()
	{
		super(false);

		addStartNpc(Creamees);
		addTalkId(Creamees);
		addKillId(Kirunak);
		addQuestItem(KirunakSkull);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30149-04.htm"))
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
		if(npcId == Creamees)
			if(cond == 0)
			{
				if(st.getPlayer().getRace() == Race.darkelf)
				{
					htmltext = "30149-00.htm";
					st.exitCurrentQuest(true);
				}
				else if(st.getPlayer().getLevel() < 21)
				{
					htmltext = "30149-02.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "30149-03.htm";
			}
			else if(cond == 1)
				htmltext = "30149-05.htm";
			else if(cond == 2)
			{
				st.takeItems(KirunakSkull, -1);
				st.giveItems(ADENA_ID, 42130, true);
				st.addExpAndSp(35637, 1854);
				htmltext = "30149-06.htm";
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
		if(cond == 1 && npcId == Kirunak)
		{
			if(st.getQuestItemsCount(KirunakSkull) == 0)
				st.giveItems(KirunakSkull, 1);
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
			st.setState(STARTED);
		}
		return null;
	}
}