package quests._132_MatrasCuriosity;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * @see http://www.linedia.ru/wiki/Matras'_Curiosity
 */
public class _132_MatrasCuriosity extends Quest implements ScriptFile
{
	// npc
	private static final int Matras = 32245;

	// monster
	private static final int Ranku = 25542;
	private static final int Demon_Prince = 25540;

	// quest items
	private static final int Rankus_Blueprint = 9800;
	private static final int Demon_Princes_Blueprint = 9801;

	// items
	private static final int Rough_Ore_of_Fire = 10521;
	private static final int Rough_Ore_of_Water = 10522;
	private static final int Rough_Ore_of_Earth = 10523;
	private static final int Rough_Ore_of_Wind = 10524;
	private static final int Rough_Ore_of_Darkness = 10525;
	private static final int Rough_Ore_of_Divinity = 10526;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _132_MatrasCuriosity()
	{
		super(PARTY_ALL);

		addStartNpc(Matras);

		addKillId(Ranku);
		addKillId(Demon_Prince);

		addQuestItem(new int[] { Rankus_Blueprint, Demon_Princes_Blueprint });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32245-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			String is_given = st.getPlayer().getVar("q132_Rough_Ore_is_given");
			if(is_given != null)
				htmltext = "32245-02a.htm";
			else
			{
				st.giveItems(Rough_Ore_of_Fire, 1, false);
				st.giveItems(Rough_Ore_of_Water, 1, false);
				st.giveItems(Rough_Ore_of_Earth, 1, false);
				st.giveItems(Rough_Ore_of_Wind, 1, false);
				st.giveItems(Rough_Ore_of_Darkness, 1, false);
				st.giveItems(Rough_Ore_of_Divinity, 1, false);
				st.getPlayer().setVar("q132_Rough_Ore_is_given", "1");
			}
		}
		else if(event.equalsIgnoreCase("32245-04.htm"))
		{
			st.set("cond", "3");
			st.setState(STARTED);
			st.startQuestTimer("talk_timer", 10000);
		}
		else if(event.equalsIgnoreCase("talk_timer"))
		{
			htmltext = st.getPlayer().isLangRus() ? "Matras хочет поговорить с Вами." : "Matras wishes to talk to you.";
		}
		else if(event.equalsIgnoreCase("get_reward"))
		{
			st.playSound(SOUND_FINISH);
			st.giveItems(ADENA_ID, 31210);
			st.exitCurrentQuest(false);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == Matras)
			if(cond < 1 && st.getPlayer().getLevel() >= 78) // Квест с 78 уровня, в клиенте опечатка
				htmltext = "32245-01.htm";
			else if(cond == 1)
				htmltext = "32245-02a.htm";
			else if(cond == 2 && st.getQuestItemsCount(Rankus_Blueprint) > 0 && st.getQuestItemsCount(Demon_Princes_Blueprint) > 0)
				htmltext = "32245-03.htm";
			else if(cond == 3)
				if(st.getQuestTimer("talk_timer") != null)
					htmltext = "32245-04.htm";
				else
					htmltext = "32245-04a.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1)
		{
			if(npc.getNpcId() == Ranku && st.getQuestItemsCount(Rankus_Blueprint) < 1)
			{
				st.playSound(SOUND_ITEMGET);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(Rankus_Blueprint, 1, false);
			}
			if(npc.getNpcId() == Demon_Prince && st.getQuestItemsCount(Demon_Princes_Blueprint) < 1)
			{
				st.playSound(SOUND_ITEMGET);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(Demon_Princes_Blueprint, 1, false);
			}
			if(st.getQuestItemsCount(Rankus_Blueprint) > 0 && st.getQuestItemsCount(Demon_Princes_Blueprint) > 0)
			{
				st.set("cond", "2");
				st.setState(STARTED);
			}
		}
		return null;
	}
}