package quests._331_ArrowForVengeance;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * Рейты применены путем увеличения шанса/количества квестовго дропа
 */
public class _331_ArrowForVengeance extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int HARPY_FEATHER = 1452;
	private static final int MEDUSA_VENOM = 1453;
	private static final int WYRMS_TOOTH = 1454;

	public _331_ArrowForVengeance()
	{
		super(false);
		addStartNpc(30125);

		addKillId(new int[] { 20145, 20158, 20176 });

		addQuestItem(new int[] { HARPY_FEATHER, MEDUSA_VENOM, WYRMS_TOOTH });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("beltkem_q0331_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("beltkem_q0331_06.htm"))
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
		int cond = st.getInt("cond");
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 32)
			{
				htmltext = "beltkem_q0331_02.htm";
				return htmltext;
			}
			htmltext = "beltkem_q0331_01.htm";
			st.exitCurrentQuest(true);
		}
		else if(cond == 1)
			if(st.getQuestItemsCount(HARPY_FEATHER) + st.getQuestItemsCount(MEDUSA_VENOM) + st.getQuestItemsCount(WYRMS_TOOTH) > 0)
			{
				st.giveItems(ADENA_ID, 80 * st.getQuestItemsCount(HARPY_FEATHER) + 90 * st.getQuestItemsCount(MEDUSA_VENOM) + 100 * st.getQuestItemsCount(WYRMS_TOOTH), false);
				st.takeItems(HARPY_FEATHER, -1);
				st.takeItems(MEDUSA_VENOM, -1);
				st.takeItems(WYRMS_TOOTH, -1);
				htmltext = "beltkem_q0331_05.htm";
			}
			else
				htmltext = "beltkem_q0331_04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") > 0)
			switch(npc.getNpcId())
			{
				case 20145:
					st.rollAndGive(HARPY_FEATHER, 1, 33);
					break;
				case 20158:
					st.rollAndGive(MEDUSA_VENOM, 1, 33);
					break;
				case 20176:
					st.rollAndGive(WYRMS_TOOTH, 1, 33);
					break;
			}
		return null;
	}
}