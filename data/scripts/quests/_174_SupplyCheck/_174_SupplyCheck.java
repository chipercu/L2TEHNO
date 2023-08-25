package quests._174_SupplyCheck;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

public class _174_SupplyCheck extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	int Marcela = 32173;
	int Benis = 32170; // warehouse keeper
	int Nika = 32167; // grocerer

	int WarehouseManifest = 9792;
	int GroceryStoreManifest = 9793;

	int WoodenBreastplate = 23;
	int WoodenGaiters = 2386;
	int LeatherTunic = 429;
	int LeatherStockings = 464;
	int WoodenHelmet = 43;
	int LeatherShoes = 37;
	int Gloves = 49;

	public _174_SupplyCheck()
	{
		super(false);

		addStartNpc(Marcela);
		addTalkId(new int[] {Benis, Nika});
		addQuestItem(new int[] {WarehouseManifest, GroceryStoreManifest});
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("zerstorer_morsell_q0174_04.htm"))
		{
			qs.setCond(1);
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(npcId == Marcela)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() == 1)
				{
					st.exitCurrentQuest(true);
					htmltext = "zerstorer_morsell_q0174_02.htm";
				}
				else
					htmltext = "zerstorer_morsell_q0174_01.htm";
			}
			else if(cond == 1)
				htmltext = "zerstorer_morsell_q0174_05.htm";
			else if(cond == 2)
			{
				st.setCond(3);
				st.takeItems(WarehouseManifest, -1);
				htmltext = "zerstorer_morsell_q0174_06.htm";
			}
			else if(cond == 3)
				htmltext = "zerstorer_morsell_q0174_07.htm";
			else if(cond == 4)
			{
				st.takeItems(GroceryStoreManifest, -1);
				if(st.getPlayer().getClassId().isMage() && !st.getPlayer().getClassId().equalsOrChildOf(ClassId.orcMage))
				{
					st.giveItems(LeatherTunic, 1);
					st.giveItems(LeatherStockings, 1);
				}
				else
				{
					st.giveItems(WoodenBreastplate, 1);
					st.giveItems(WoodenGaiters, 1);
				}
				st.giveItems(WoodenHelmet, 1);
				st.giveItems(LeatherShoes, 1);
				st.giveItems(Gloves, 1);
				st.giveItems(ADENA_ID, (int)(2466 * ConfigValue.RateQuestsRewardAdena), true);
				st.getPlayer().addExpAndSp((int)(5672 * ConfigValue.RateQuestsRewardExpSp), (int)(446 * ConfigValue.RateQuestsRewardExpSp), false, false);
				if(st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("ng1"))
					st.getPlayer().sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				st.exitCurrentQuest(false);
				htmltext = "zerstorer_morsell_q0174_12.htm";
			}
		}

		else if(npcId == Benis)
			if(cond == 1)
			{
				st.setCond(2);
				st.giveItems(WarehouseManifest, 1);
				htmltext = "warehouse_keeper_benis_q0174_01.htm";
			}
			else
				htmltext = "warehouse_keeper_benis_q0174_02.htm";

		else if(npcId == Nika)
			if(cond < 3)
				htmltext = "subelder_casca_q0174_01.htm";
			else if(cond == 3)
			{
				st.setCond(4);
				st.giveItems(GroceryStoreManifest, 1);
				htmltext = "trader_neagel_q0174_02.htm";
			}
			else
				htmltext = "trader_neagel_q0174_03.htm";
		return htmltext;
	}
}