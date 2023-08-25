package quests._608_SlayTheEnemyCommander;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _608_SlayTheEnemyCommander extends Quest implements ScriptFile
{
	// npc
	private static final int KADUN_ZU_KETRA = 31370;
	private static final int VARKAS_COMMANDER_MOS = 25312;

	//quest items
	private static final int HEAD_OF_MOS = 7236;
	private static final int TOTEM_OF_WISDOM = 7220;
	@SuppressWarnings("unused")
	private static final int MARK_OF_KETRA_ALLIANCE1 = 7211;
	@SuppressWarnings("unused")
	private static final int MARK_OF_KETRA_ALLIANCE2 = 7212;
	@SuppressWarnings("unused")
	private static final int MARK_OF_KETRA_ALLIANCE3 = 7213;
	private static final int MARK_OF_KETRA_ALLIANCE4 = 7214;
	private static final int MARK_OF_KETRA_ALLIANCE5 = 7215;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _608_SlayTheEnemyCommander()
	{
		super(true);
		addStartNpc(KADUN_ZU_KETRA);
		addKillId(VARKAS_COMMANDER_MOS);
		addQuestItem(HEAD_OF_MOS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "elder_kadun_zu_ketra_q0608_0104.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("608_3"))
			if(st.getQuestItemsCount(HEAD_OF_MOS) >= 1)
			{
				htmltext = "elder_kadun_zu_ketra_q0608_0201.htm";
				st.takeItems(HEAD_OF_MOS, -1);
				st.giveItems(TOTEM_OF_WISDOM, 1);
				st.addExpAndSp(0, 10000);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "elder_kadun_zu_ketra_q0608_0106.htm";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 75)
			{
				if(st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) == 1)
					htmltext = "elder_kadun_zu_ketra_q0608_0101.htm";
				else
				{
					htmltext = "elder_kadun_zu_ketra_q0608_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "elder_kadun_zu_ketra_q0608_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1 && st.getQuestItemsCount(HEAD_OF_MOS) == 0)
			htmltext = "elder_kadun_zu_ketra_q0608_0106.htm";
		else if(cond == 2 && st.getQuestItemsCount(HEAD_OF_MOS) >= 1)
			htmltext = "elder_kadun_zu_ketra_q0608_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1)
		{
			st.giveItems(HEAD_OF_MOS, 1);
			st.set("cond", "2");
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}