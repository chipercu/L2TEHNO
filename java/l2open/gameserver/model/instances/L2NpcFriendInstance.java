package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Events;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.Warehouse.WarehouseType;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;
import java.util.logging.Logger;

public final class L2NpcFriendInstance extends L2MerchantInstance
{
	private static Logger _log = Logger.getLogger(L2NpcFriendInstance.class.getName());

	public L2NpcFriendInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private long _lastSocialAction;

	/**
	 * this is called when a player interacts with this NPC
	 * @param player
	 */
	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(this != player.getTarget())
		{
			player.setTarget(this);
			if(isAutoAttackable(player))
				player.sendPacket(makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
			player.sendActionFailed();
			return;
		}

		player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));

		if(Events.onAction(player, this, shift))
			return;

		if(isAutoAttackable(player))
		{
			player.getAI().Attack(this, false, shift);
			return;
		}

		if(!isInRange(player, 100+addDist))
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, 100);
			player.sendActionFailed();
			return;
		}

		if(!ConfigValue.AltKarmaPlayerCanShop && player.getKarma() > 0 && !player.isGM())
		{
			player.sendActionFailed();
			return;
		}

		//С NPC нельзя разговаривать мертвым и сидя
		if(!ConfigValue.AllowTalkWhileSitting && player.isSitting() || player.isAlikeDead())
			return;

		if(System.currentTimeMillis() - _lastSocialAction > 10000)
			broadcastPacket2(new SocialAction(getObjectId(), 2));

		_lastSocialAction = System.currentTimeMillis();

		player.sendActionFailed();

		String filename = "";

		if(getNpcId() >= 31370 && getNpcId() <= 31376 && player.getVarka() > 0 || getNpcId() >= 31377 && getNpcId() < 31384 && player.getKetra() > 0)
		{
			filename = "data/html/npc_friend/" + getNpcId() + "-nofriend.htm";
			showChatWindow(player, filename);
			return;
		}

		switch(getNpcId())
		{
			case 31370:
			case 31371:
			case 31373:
			case 31377:
			case 31378:
			case 31380:
			case 31553:
			case 31554:
				filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31372:
				if(player.getKetra() > 2)
					filename = "data/html/npc_friend/" + getNpcId() + "-bufflist.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31379:
				if(player.getVarka() > 2)
					filename = "data/html/npc_friend/" + getNpcId() + "-bufflist.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31374:
				if(player.getKetra() > 1)
					filename = "data/html/npc_friend/" + getNpcId() + "-warehouse.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31381:
				if(player.getVarka() > 1)
					filename = "data/html/npc_friend/" + getNpcId() + "-warehouse.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31375:
				if(player.getKetra() == 3 || player.getKetra() == 4)
					filename = "data/html/npc_friend/" + getNpcId() + "-special1.htm";
				else if(player.getKetra() == 5)
					filename = "data/html/npc_friend/" + getNpcId() + "-special2.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31382:
				if(player.getVarka() == 3 || player.getVarka() == 4)
					filename = "data/html/npc_friend/" + getNpcId() + "-special1.htm";
				else if(player.getVarka() == 5)
					filename = "data/html/npc_friend/" + getNpcId() + "-special2.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31376:
				if(player.getKetra() == 4)
					filename = "data/html/npc_friend/" + getNpcId() + "-normal.htm";
				else if(player.getKetra() == 5)
					filename = "data/html/npc_friend/" + getNpcId() + "-special.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31383:
				if(player.getVarka() == 4)
					filename = "data/html/npc_friend/" + getNpcId() + "-normal.htm";
				else if(player.getVarka() == 5)
					filename = "data/html/npc_friend/" + getNpcId() + "-special.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31555:
				if(player.getRam() == 1)
					filename = "data/html/npc_friend/" + getNpcId() + "-special1.htm";
				else if(player.getRam() == 2)
					filename = "data/html/npc_friend/" + getNpcId() + "-special2.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
				break;
			case 31556:
				if(player.getRam() == 2)
					filename = "data/html/npc_friend/" + getNpcId() + "-bufflist.htm";
				else
					filename = "data/html/npc_friend/" + getNpcId() + ".htm";
		}

		showChatWindow(player, filename);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if(actualCommand.equalsIgnoreCase("Buff"))
		{
			if(st.countTokens() < 1)
				return;
			int val = Integer.parseInt(st.nextToken());
			int item = 0;

			switch(getNpcId())
			{
				case 31372:
					item = 7186;
					break;
				case 31379:
					item = 7187;
					break;
				case 31556:
					item = 7251;
					break;
			}

			int skill = 0;
			int level = 0;
			long count = 0;

			switch(val)
			{
				case 1:
					skill = 4359;
					level = 2;
					count = 2;
					break;
				case 2:
					skill = 4360;
					level = 2;
					count = 2;
					break;
				case 3:
					skill = 4345;
					level = 3;
					count = 3;
					break;
				case 4:
					skill = 4355;
					level = 2;
					count = 3;
					break;
				case 5:
					skill = 4352;
					level = 1;
					count = 3;
					break;
				case 6:
					skill = 4354;
					level = 3;
					count = 3;
					break;
				case 7:
					skill = 4356;
					level = 1;
					count = 6;
					break;
				case 8:
					skill = 4357;
					level = 2;
					count = 6;
					break;
			}

			if(skill != 0 && player.getInventory().getItemByItemId(item) != null && item > 0 && player.getInventory().getItemByItemId(item).getCount() >= count)
			{
				if(player.getInventory().destroyItemByItemId(item, count, true) == null)
					_log.info("L2NpcFriendInstance[274]: Item not found!!!");
				player.doCast(SkillTable.getInstance().getInfo(skill, level), player, true);
			}
			else
				showChatWindow(player, "data/html/npc_friend/" + getNpcId() + "-havenotitems.htm");
		}
		else if(command.startsWith("Chat"))
		{
			int val = Integer.parseInt(command.substring(5));
			String fname = "";
			fname = "data/html/npc_friend/" + getNpcId() + "-" + val + ".htm";
			if(!fname.equals(""))
				showChatWindow(player, fname);
		}
		else if(command.startsWith("Buy"))
		{
			int val = Integer.parseInt(command.substring(4));
			showShopWindow(player, val, false);
		}
		else if(actualCommand.equalsIgnoreCase("Sell"))
			showShopWindow(player);
		else if(command.startsWith("WithdrawP"))
		{
			int val = Integer.parseInt(command.substring(10));
			if(val == 9)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/npc_friend/personal.htm");
				html.replace("%npcname%", getName());
				player.sendPacket(html);
			}
			else
				showRetrieveWindow(player, val);
		}
		else if(command.equals("DepositP"))
			showDepositWindow(player);
		else
			super.onBypassFeedback(player, command);
	}

	private void showDepositWindow(L2Player player)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return;
		player.tempInventoryDisable();
		player.sendPacket(new WareHouseDepositList(player, WarehouseType.PRIVATE));
		player.sendActionFailed();
	}

	private void showRetrieveWindow(L2Player player, int val)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return;
		player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.PRIVATE, ItemClass.values()[val]));
	}
}