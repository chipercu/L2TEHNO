package quests._309_ForAGoodCause;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._239_WontYouJoinUs._239_WontYouJoinUs;
import quests._308_ReedFieldMaintenance._308_ReedFieldMaintenance;

import java.io.File;

public class _309_ForAGoodCause extends Quest implements ScriptFile
{
	private static final int Atra = 32647;

	private static final int MucrokianHide = 14873;
	private static final int FallenMucrokianHide = 14874;

	private static final int MucrokianFanatic = 22650;
	private static final int MucrokianAscetic = 22651;
	private static final int MucrokianSavior = 22652;
	private static final int MucrokianPreacher = 22653;
	private static final int ContaminatedMucrokian = 22654;
	private static final int ChangedMucrokian = 22655;

	private static final int[] MoiraiRecipes = {
			15777,
			15780,
			15783,
			15786,
			15789,
			15790,
			15812,
			15813,
			15814
	};
	private static final int[] Moiraimaterials = {
			15647,
			15650,
			15653,
			15656,
			15659,
			15692,
			15772,
			15773,
			15774
	};
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _309_ForAGoodCause()
	{
		super(false);
		addStartNpc(Atra);
		addQuestItem(MucrokianHide, FallenMucrokianHide);
		addKillId(MucrokianFanatic, MucrokianAscetic, MucrokianSavior, MucrokianPreacher, ContaminatedMucrokian, ChangedMucrokian);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32647-05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("32646-14.htm"))
			st.exitCurrentQuest(true);
		else if(event.equalsIgnoreCase("moirairec"))
		{
			if(st.getQuestItemsCount(MucrokianHide) >= 180)
			{
				st.takeItems(MucrokianHide, 180);
				st.giveItems(MoiraiRecipes[Rnd.get(MoiraiRecipes.length - 1)], 1);
				return null;
			}
			else
				htmltext = "32646-14.htm";
		}
		else if(event.equalsIgnoreCase("moiraimat"))
		{
			if(st.getQuestItemsCount(MucrokianHide) >= 100)
			{
				st.takeItems(MucrokianHide, 100);
				st.giveItems(Moiraimaterials[Rnd.get(Moiraimaterials.length - 1)], 1);
				return null;
			}
			else
				htmltext = "32646-14.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();

		if(npcId == Atra)
			if(id == CREATED)
			{
				QuestState qs1 = st.getPlayer().getQuestState(_308_ReedFieldMaintenance.class);
				if(qs1 != null && qs1.isStarted())
					return "32647-17.htm"; // нельзя брать оба квеста сразу
				if(st.getPlayer().getLevel() < 82)
					return "32647-00.htm";
				return "32647-01.htm";
			}
			else if(cond == 1)
			{
				long fallen = st.takeAllItems(FallenMucrokianHide);
				if(fallen > 0)
					st.giveItems(MucrokianHide, fallen * 2);

				if(st.getQuestItemsCount(MucrokianHide) == 0)
					return "32647-06.htm"; // нечего менять
				else if(!st.getPlayer().isQuestCompleted(_239_WontYouJoinUs.class))
					return "32647-a1.htm"; // обычные цены
				else
					return "32647-a2.htm"; // со скидкой
			}

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.rollAndGive(npc.getNpcId() == ContaminatedMucrokian ? FallenMucrokianHide : MucrokianHide, 1, 60);
		return null;
	}
}