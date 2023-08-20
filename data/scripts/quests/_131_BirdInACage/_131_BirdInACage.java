package quests._131_BirdInACage;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * @author Diamond
 */
public class _131_BirdInACage extends Quest implements ScriptFile
{
	// NPC's
	private static int KANIS = 32264;
	private static int PARME = 32271;
	// ITEMS
	private static int KANIS_ECHO_CRY = 9783;
	private static int PARMES_LETTER = 9784;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _131_BirdInACage()
	{
		super(false);

		addStartNpc(KANIS);
		addTalkId(PARME);

		addQuestItem(KANIS_ECHO_CRY);
		addQuestItem(PARMES_LETTER);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int cond = st.getInt("cond");
		String htmltext = event;

		if(event.equals("priest_kanis_q0131_04.htm") && cond == 0)
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("priest_kanis_q0131_12.htm") && cond == 1)
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(KANIS_ECHO_CRY, 1);
		}
		else if(event.equals("parme_131y_q0131_04.htm") && cond == 2)
		{
			st.set("cond", "3");
			st.giveItems(PARMES_LETTER, 1);
			st.playSound(SOUND_MIDDLE);
			st.getPlayer().teleToLocation(143472 + Rnd.get(-100, 100), 191040 + Rnd.get(-100, 100), -3696);
		}
		else if(event.equals("priest_kanis_q0131_17.htm") && cond == 3)
		{
			st.playSound(SOUND_MIDDLE);
			st.takeItems(PARMES_LETTER, -1);
		}
		else if(event.equals("priest_kanis_q0131_19.htm") && cond == 3)
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(KANIS_ECHO_CRY, -1);
			st.addExpAndSp(250677, 25019);
			st.exitCurrentQuest(false);
		}
		else if(event.equals("meet") && cond == 2)
			st.getPlayer().teleToLocation(153736, 142056, -9744);

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");

		if(npcId == KANIS)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 78)
					htmltext = "priest_kanis_q0131_01.htm";
				else
				{
					htmltext = "priest_kanis_q0131_02.htm";
					st.exitCurrentQuest(true);
				}
			else if(cond == 1)
				htmltext = "priest_kanis_q0131_05.htm";
			else if(cond == 2)
				htmltext = "priest_kanis_q0131_13.htm";
			else if(cond == 3)
				if(st.getQuestItemsCount(PARMES_LETTER) > 0)
					htmltext = "priest_kanis_q0131_16.htm";
				else
					htmltext = "priest_kanis_q0131_17.htm";
		}
		else if(npcId == PARME && cond == 2)
			htmltext = "parme_131y_q0131_02.htm";

		return htmltext;
	}
}