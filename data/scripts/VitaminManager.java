package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.Calendar;

public class VitaminManager extends Functions implements ScriptFile
{
	private static final int PetCoupon = 13273;
	private static final int SpecialPetCoupon = 14065;

	private static final int WeaselNeck = 13017;
	private static final int PrincNeck = 13018;
	private static final int BeastNeck = 13019;
	private static final int FoxNeck = 13020;

	private static final int KnightNeck = 13548;
	private static final int SpiritNeck = 13549;
	private static final int OwlNeck = 13550;
	private static final int TurtleNeck = 13551;

	private static final int RcPaper = 15279;
	private static final int RcPresent = 15278;

	public void giveWeasel()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1);
			addItem(player, WeaselNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void givePrinc()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1);
			addItem(player, PrincNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void giveBeast()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1);
			addItem(player, BeastNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void giveFox()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1);
			addItem(player, FoxNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void giveKnight()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1);
			addItem(player, KnightNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void giveSpirit()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1);
			addItem(player, SpiritNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void giveOwl()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1);
			addItem(player, OwlNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void giveTurtle()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String htmltext;
		if(getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1);
			addItem(player, TurtleNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "data/html/default/" + htmltext);
	}

	public void changePaper()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(getItemCount(player, RcPaper) > 0)
		{
			if(player.getVarInt("VitaminManagerChangePaper", 0) == 0)
			{
				removeItem(player, RcPaper, 1);
				addItem(player, RcPresent, 1);
				Calendar reDo = Calendar.getInstance();
				reDo.set(Calendar.MINUTE, 30);
				if(reDo.get(Calendar.HOUR_OF_DAY) >= 6)
					reDo.add(Calendar.DATE, 1);
				reDo.set(Calendar.HOUR_OF_DAY, 6);
				player.setVar("VitaminManagerChangePaper", 1, reDo.getTimeInMillis());
			}
			else
			{
				npc.showChatWindow(player, "data/html/default/e_premium_manager015.htm");
			}
		}
		else
		{
			npc.showChatWindow(player, "data/html/default/e_premium_manager014.htm");
		}
	}

	public void onLoad()
	{
		_log.info("Loaded Service: VitaminManager...");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}