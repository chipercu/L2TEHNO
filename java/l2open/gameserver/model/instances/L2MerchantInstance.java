package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.TradeController;
import l2open.gameserver.TradeController.NpcTradeList;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.Warehouse;
import l2open.gameserver.model.items.Warehouse.WarehouseType;
import l2open.gameserver.serverpackets.ExBuySellList;
import l2open.gameserver.serverpackets.ExGetPremiumItemList;
import l2open.gameserver.serverpackets.PackageToList;
import l2open.gameserver.serverpackets.ShopPreviewList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.WareHouseWithdrawList;
import l2open.gameserver.templates.L2NpcTemplate;

import java.io.File;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class L2MerchantInstance extends L2NpcInstance
{
	private static Logger _log = Logger.getLogger(L2MerchantInstance.class.getName());

	public L2MerchantInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		String temp = "data/html/merchant/" + pom + ".htm";
		File mainText = new File(temp);
		if(mainText.exists())
			return temp;

		temp = "data/html/teleporter/" + pom + ".htm";
		mainText = new File(temp);
		if(mainText.exists())
			return temp;

		temp = "data/html/petmanager/" + pom + ".htm";
		mainText = new File(temp);
		if(mainText.exists())
			return temp;

		temp = "data/html/default/" + pom + ".htm";
		mainText = new File(temp);
		if(mainText.exists())
			return temp;

		return "data/html/teleporter/" + pom + ".htm";
	}

	private void showWearWindow(L2Player player, int val)
	{
		if(!player.getPlayerAccess().UseShop)
			return;

		player.tempInventoryDisable();
		NpcTradeList list = TradeController.getInstance().getBuyList(val);

		if(list != null)
		{
			ShopPreviewList bl = new ShopPreviewList(list, player.getAdena(), player.expertiseIndex);
			player.sendPacket(bl);
		}
		else
		{
			_log.warning("no buylist with id:" + val);
			player.sendActionFailed();
		}
	}

	protected void showShopWindow(L2Player player, int listId, boolean tax)
	{
		if(!player.getPlayerAccess().UseShop)
			return;

		double taxRate = 0;

		if(tax)
		{
			Castle castle = getCastle(player);
			if(castle != null)
				taxRate = castle.getTaxRate();
		}

		player.tempInventoryDisable();
		NpcTradeList list = TradeController.getInstance().getBuyList(listId);
		if(list == null || list.getNpcId() == getNpcId())
			player.sendPacket(new ExBuySellList(list, player, taxRate));
		else
		{
			_log.warning("[L2MerchantInstance] possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
			_log.warning("buylist id:" + listId + " / list_npc = " + list.getNpcId() + " / npc = " + getNpcId());
		}
	}

	protected void showShopWindow(L2Player player)
	{
		showShopWindow(player, 0, false);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if(actualCommand.equalsIgnoreCase("Buy") || actualCommand.equalsIgnoreCase("Sell"))
		{
			int val = 0;
			if(st.countTokens() > 0)
				val = Integer.parseInt(st.nextToken());
			showShopWindow(player, val, true);
		}
		else if(actualCommand.equalsIgnoreCase("Wear") && ConfigValue.WearTestEnabled)
		{
			if(st.countTokens() < 1)
				return;
			int val = Integer.parseInt(st.nextToken());
			showWearWindow(player, val);
		}
		else if(actualCommand.equalsIgnoreCase("Multisell"))
		{
			if(st.countTokens() < 1)
				return;
			int val = Integer.parseInt(st.nextToken());
			Castle castle = getCastle(player);
			L2Multisell.getInstance().SeparateAndSend(val, player, castle != null ? castle.getTaxRate() : 0);
		}
		else if(actualCommand.equalsIgnoreCase("ReceivePremium"))
		{
			if(player.getPremiumItemList().isEmpty())
			{
				player.sendPacket(Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
				return;
			}
			player.sendPacket(new ExGetPremiumItemList(player));
		}
		else if(actualCommand.equalsIgnoreCase("deposit_items"))
			player.sendPacket(new PackageToList(player));
		else if(actualCommand.equalsIgnoreCase("withdraw_items"))
			showFreightWindow(player);
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public Castle getCastle(L2Player player)
	{
		if(getReflection().getId() < 0)
		{
			if(ConfigValue.NoCastleTaxInOffshore)
				return null;
			String var = player.getVar("backCoords");
			if(var != null && !var.isEmpty())
			{
				String[] loc = var.split(",");
				return TownManager.getInstance().getClosestTown(Integer.parseInt(loc[0]), Integer.parseInt(loc[1])).getCastle();
			}
			return TownManager.getInstance().getClosestTown(this).getCastle();
		}
		return super.getCastle(player);
	}

	public static void showFreightWindow(L2Player player)
	{
		if(!canShowWarehouseWithdrawList(player))
		{
			player.sendActionFailed();
			return;
		}
		player.setUsingWarehouseType(WarehouseType.FREIGHT);
		player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.FREIGHT, ItemClass.ALL));
	}
	public static boolean canShowWarehouseWithdrawList(L2Player player)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return false;
		Warehouse warehouse = player.getFreight();

		if(warehouse == null)
		{
			player.sendPacket(new SystemMessage(SystemMessage.NO_PACKAGES_HAVE_ARRIVED));
			return false;
		}

		return true;
	}

	@Override
	public void MENU_SELECTED(L2Player talker, int ask, int reply)
	{
		L2Player c0 = null;
		if(ask == 709)
		{
			if(reply == 1)
			{
				c0 = Pledge_GetLeader(talker);
				if(IsNullCreature(c0) == 0)
				{
					if(HaveMemo(c0, 709) == 1 && GetMemoState(c0, 709) == 4)
						ShowPage(talker, "scroll_seller_rouke_q0709_04.htm");
				}
			}
			if(reply == 2)
			{
				c0 = Pledge_GetLeader(talker);
				if(IsNullCreature(c0) == 0)
				{
					if(HaveMemo(c0, 709) == 1 && GetMemoState(c0, 709) == 4)
					{
						SetMemoState(c0, 709, 5);
						ShowPage(talker, "scroll_seller_rouke_q0709_05.htm");
						SetFlagJournal(c0, 709, 4);
						ShowQuestMark(c0, 709);
						SoundEffect(c0, "ItemSound.quest_middle");
					}
				}
				else
					ShowPage(talker, "scroll_seller_rouke_q0709_06.htm");
			}
			if(reply == 3)
			{
				c0 = Pledge_GetLeader(talker);
				if(IsNullCreature(c0) == 0)
				{
					if(HaveMemo(c0, 709) == 1 && GetMemoState(c0, 709) % 10 == 5)
					{
						int i1 = GetMemoState(c0, 709);
						if(OwnItemCount(talker, 13849) >= 100)
						{
							SetMemoState(c0, 709, i1 + 4);
							ShowPage(talker, "scroll_seller_rouke_q0709_09.htm");
							DeleteItem1(talker, 13849, OwnItemCount(talker, 13849));
						}
						else
							ShowPage(talker, "scroll_seller_rouke_q0709_10.htm");
					}
				}
				else
					ShowPage(talker, "scroll_seller_rouke_q0709_11.htm");
			}
		}
		else if(ask == 710)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,710) == 1 && GetMemoState(talker,710) == 4 && IsMyLord(talker) == 1)
				{
					SetMemoState(talker,710,5);
					ShowPage(talker,"wharf_manager_felton_q0710_02.htm");
					SetFlagJournal(talker,710,4);
					ShowQuestMark(talker,710);
					SoundEffect(talker,"ItemSound.quest_middle");
				}
			}
		}
		super.MENU_SELECTED(talker, ask, reply);
	}
}