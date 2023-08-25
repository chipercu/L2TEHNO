package quests._269_InventionAmbition;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _269_InventionAmbition extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;
	//NPC
	public final int INVENTOR_MARU = 32486;
	//MOBS
	public final int RED_EYE_BARBED_BAT = 21124;
	public final int UNDERGROUND_KOBOLD = 21132;
	//ITEMS
	public final int ENERGY_ORES = 10866;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _269_InventionAmbition()
	{
		super(false);
		addStartNpc(INVENTOR_MARU);
		addKillId(RED_EYE_BARBED_BAT, UNDERGROUND_KOBOLD);
		addQuestItem(ENERGY_ORES);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("inventor_maru_q0269_04.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("inventor_maru_q0269_07.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		long count = st.getQuestItemsCount(ENERGY_ORES);
		if(st.getState() == CREATED)
			if(st.getPlayer().getLevel() < 18)
			{
				htmltext = "inventor_maru_q0269_02.htm";
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "inventor_maru_q0269_01.htm";
		else if(count > 0)
		{
			st.giveItems(ADENA_ID, count * 50 + 2044 * (count / 20), true);
			st.takeItems(ENERGY_ORES, -1);
			htmltext = "inventor_maru_q0269_06.htm";
		}
		else
			htmltext = "inventor_maru_q0269_05.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() != STARTED)
			return null;
		if(Rnd.chance(60))
		{
			st.giveItems(ENERGY_ORES, 1, false);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}