package quests._607_ProveYourCourage;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _607_ProveYourCourage extends Quest implements ScriptFile
{
	private final static int KADUN_ZU_KETRA = 31370;
	private final static int VARKAS_HERO_SHADITH = 25309;

	// Quest items
	private final static int HEAD_OF_SHADITH = 7235;
	private final static int TOTEM_OF_VALOR = 7219;

	// etc
	@SuppressWarnings("unused")
	private final static int MARK_OF_KETRA_ALLIANCE1 = 7211;
	@SuppressWarnings("unused")
	private final static int MARK_OF_KETRA_ALLIANCE2 = 7212;
	private final static int MARK_OF_KETRA_ALLIANCE3 = 7213;
	private final static int MARK_OF_KETRA_ALLIANCE4 = 7214;
	private final static int MARK_OF_KETRA_ALLIANCE5 = 7215;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _607_ProveYourCourage()
	{
		super(true);

		addStartNpc(KADUN_ZU_KETRA);
		addKillId(VARKAS_HERO_SHADITH);

		addQuestItem(HEAD_OF_SHADITH);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			htmltext = "elder_kadun_zu_ketra_q0607_0104.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("607_3"))
			if(st.getQuestItemsCount(HEAD_OF_SHADITH) >= 1)
			{
				htmltext = "elder_kadun_zu_ketra_q0607_0201.htm";
				st.takeItems(HEAD_OF_SHADITH, -1);
				st.giveItems(TOTEM_OF_VALOR, 1);
				st.addExpAndSp(0, 10000);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
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
				if(st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE3) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) == 1)
					htmltext = "elder_kadun_zu_ketra_q0607_0101.htm";
				else
				{
					htmltext = "elder_kadun_zu_ketra_q0607_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "elder_kadun_zu_ketra_q0607_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1 && st.getQuestItemsCount(HEAD_OF_SHADITH) == 0)
			htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
		else if(cond == 2 && st.getQuestItemsCount(HEAD_OF_SHADITH) >= 1)
			htmltext = "elder_kadun_zu_ketra_q0607_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == VARKAS_HERO_SHADITH && st.getInt("cond") == 1)
		{
			st.giveItems(HEAD_OF_SHADITH, 1);
			st.set("cond", "2");
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}