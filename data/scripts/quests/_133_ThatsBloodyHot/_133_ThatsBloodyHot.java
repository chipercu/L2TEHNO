package quests._133_ThatsBloodyHot;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._131_BirdInACage._131_BirdInACage;

/**
 * User: Keiichi
 * Date: 06.10.2008
 * Time: 11:31:36
 * Info: Один из 2х квестов для прохода на остров Hellbound.
 * Info: Пройдя его ведьма Galate открывает ТП до Beleth's stronghold on Hellbound Island
 */
public class _133_ThatsBloodyHot extends Quest implements ScriptFile
{
	// NPC's
	private static int KANIS = 32264;
	private static int GALATE = 32292;
	// ITEMS
	private static int CRYSTAL_SAMPLE = 9785;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _133_ThatsBloodyHot()
	{
		super(false);

		addStartNpc(KANIS);
		addTalkId(KANIS);
		addTalkId(GALATE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int cond = st.getInt("cond");
		String htmltext = event;

		if(event.equals("priest_kanis_q0133_04.htm") && cond == 0)
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}

		if(event.equals("priest_kanis_q0133_12.htm") && cond == 1)
		{
			st.set("cond", "2");
			st.giveItems(CRYSTAL_SAMPLE, 1);
		}

		if(event.equals("Galate_q0133_06.htm") && cond == 2)
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(CRYSTAL_SAMPLE, -1);
			st.giveItems(ADENA_ID, 254247);
			st.addExpAndSp(331457, 32524);
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

		if(npcId == KANIS)
		{
			if(cond == 0)
			{
				//_131_BirdInACage
				QuestState BirdInCage = st.getPlayer().getQuestState(_131_BirdInACage.class);
				if(BirdInCage != null)
				{
					if(BirdInCage.isCompleted())
					{
						if(st.getPlayer().getLevel() >= 78)
							htmltext = "priest_kanis_q0133_01.htm";
					}
					else
						htmltext = "priest_kanis_q0133_03.htm";
					st.exitCurrentQuest(true);
				}
			}
		}

		else if(id == STARTED)
			if(npcId == GALATE)
				if(cond == 2)
					htmltext = "Galate_q0133_02.htm";

		return htmltext;
	}
}
