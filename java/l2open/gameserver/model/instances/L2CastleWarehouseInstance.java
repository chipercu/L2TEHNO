package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.Warehouse.WarehouseType;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.WareHouseDepositList;
import l2open.gameserver.serverpackets.WareHouseWithdrawList;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Log;

public class L2CastleWarehouseInstance extends L2NpcInstance
{
	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	private static final int RECHARGE_TIME = ConfigValue.CastleSiegeDay * 24 * 60 * 60; // каждые 2 недели
	private static final int ITEM_BLOOD_ALLI = 9911; // Blood Alliance
	private static final int ITEM_BLOOD_OATH = 9910; // Blood Oath

	public L2CastleWarehouseInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private void showRetrieveWindow(L2Player player, int val)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return;

		player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.PRIVATE, ItemClass.values()[val]), Msg.ActionFail);
	}

	private void showDepositWindow(L2Player player)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return;

		player.tempInventoryDisable();
		player.sendPacket(new WareHouseDepositList(player, WarehouseType.PRIVATE), Msg.ActionFail);
	}

	private void showDepositWindowClan(L2Player player)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return;

		if(player.getClan() == null)
		{
			player.sendActionFailed();
			return;
		}

		if(player.getClan().getLevel() == 0)
		{
			player.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE, Msg.ActionFail);
			return;
		}

		player.tempInventoryDisable();

		if(!(player.isClanLeader() || (ConfigValue.AltAllowOthersWithdrawFromClanWarehouse || player.getVarB("canWhWithdraw")) && (player.getClanPrivileges() & L2Clan.CP_CL_WAREHOUSE_SEARCH) == L2Clan.CP_CL_WAREHOUSE_SEARCH))
			player.sendPacket(Msg.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);

		player.sendPacket(new WareHouseDepositList(player, WarehouseType.CLAN));
	}

	private void showWithdrawWindowClan(L2Player player, int val)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return;

		if(player.getClan() == null)
		{
			player.sendActionFailed();
			return;
		}

		if(player.getClan().getLevel() == 0)
		{
			player.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE, Msg.ActionFail);
			return;
		}

		if(/*ConfigValue.AltAllowOthersWithdrawFromClanWarehouse&&*/(player.getClanPrivileges() & L2Clan.CP_CL_WAREHOUSE_SEARCH) == L2Clan.CP_CL_WAREHOUSE_SEARCH)
		{
			player.tempInventoryDisable();
			player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.CLAN, ItemClass.values()[val]));
		}
		else
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE, Msg.ActionFail);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if((player.getClanPrivileges() & L2Clan.CP_CS_USE_FUNCTIONS) != L2Clan.CP_CS_USE_FUNCTIONS)
		{
			player.sendMessage("You don't have rights to do that.");
			return;
		}

		if(player.getEnchantScroll() != null)
		{
			Log.add("Player " + player.getName() + " trying to use enchant exploit[CastleWarehouse], ban this player!", "illegal-actions");
			player.closeNetConnection();
			return;
		}

		if(command.startsWith("WithdrawP"))
		{
			int val = Integer.parseInt(command.substring(10));
			if(val == 9)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/warehouse/personal.htm");
				html.replace("%npcname%", getName());
				player.sendPacket(html);
			}
			else
				showRetrieveWindow(player, val);
		}
		else if(command.equals("DepositP"))
			showDepositWindow(player);
		else if(command.startsWith("WithdrawC"))
		{
			int val = Integer.parseInt(command.substring(10));
			if(val == 9)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/warehouse/clan.htm");
				html.replace("%npcname%", getName());
				player.sendPacket(html);
			}
			else
				showWithdrawWindowClan(player, val);
		}
		else if(command.equals("DepositC"))
			showDepositWindowClan(player);
		else if(command.equalsIgnoreCase("CheckHonoraryItems"))
		{
			String filename;
			if(!player.isClanLeader())
				filename = "data/html/castle/warehouse/castlewarehouse-notcl.htm";
			else
				filename = "data/html/castle/warehouse/castlewarehouse-5.htm";

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile(filename);
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", getName());
			html.replace("%total_items%", "" + getAvailableItemsCount(player));
			player.sendPacket(html);
		}
		else if(command.equalsIgnoreCase("ExchangeBloodAlli"))
		{
			if(!player.isClanLeader())
			{
				String filename = "data/html/castle/warehouse/castlewarehouse-notcl.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				player.sendPacket(html);
			}
			else
			{
				if(Functions.removeItem(player, ITEM_BLOOD_ALLI, 1) == 0)
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				else
					Functions.addItem(player, ITEM_BLOOD_OATH, 30);
			}
		}
		else if(command.equalsIgnoreCase("ReciveBloodAlli"))
		{
			String filename;
			if(!player.isClanLeader())
				filename = "data/html/castle/warehouse/castlewarehouse-notcl.htm";
			else if(getAvailableItemsCount(player) > 0)
			{
				filename = "data/html/castle/warehouse/castlewarehouse-3.htm";
				Functions.addItem(player, ITEM_BLOOD_ALLI, getAvailableItemsCount(player));
				ServerVariables.set("ReciveBloodAlli_" + player.getClan().getClanId(), (int) (System.currentTimeMillis() / 1000));
			}
			else
				filename = "data/html/castle/warehouse/castlewarehouse-4.htm";

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile(filename);
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", getName());
			player.sendPacket(html);
		}
		else if(command.startsWith("Chat"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch(IndexOutOfBoundsException ioobe)
			{}
			catch(NumberFormatException nfe)
			{}
			showChatWindow(player, val);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		player.sendActionFailed();
		String filename = "data/html/castle/warehouse/castlewarehouse-no.htm";

		int condition = validateCondition(player);
		if(condition > COND_ALL_FALSE)
			if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "data/html/castle/warehouse/castlewarehouse-busy.htm"; // Busy because of siege
			else if(condition == COND_OWNER)
				if(val == 0)
					filename = "data/html/castle/warehouse/castlewarehouse.htm";
				else
					filename = "data/html/castle/warehouse/castlewarehouse-" + val + ".htm";

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	protected int validateCondition(L2Player player)
	{
		if(player.isGM())
			return COND_OWNER;
		if(getCastle() != null && getCastle().getId() > 0)
			if(player.getClan() != null)
				if(getCastle().getSiege().isInProgress() || TerritorySiege.isInProgress())
					return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
				else if(getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
					return COND_OWNER;
		return COND_ALL_FALSE;
	}

	private long getAvailableItemsCount(L2Player player)
	{
		if(player.getClan() == null)
			return 0;

		int lastRecive = ServerVariables.getInt("ReciveBloodAlli_" + player.getClan().getClanId(), 0);

		if(lastRecive == 0 || lastRecive < getCastle().getOwnDate())
			lastRecive = getCastle().getOwnDate();

		return (long) Math.floor((System.currentTimeMillis() / 1000 - lastRecive) / RECHARGE_TIME);
	}
}