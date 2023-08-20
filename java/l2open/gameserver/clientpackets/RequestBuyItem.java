package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.TradeController;
import l2open.gameserver.TradeController.NpcTradeList;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExBuySellList;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Log;
import l2open.util.SafeMath;
import l2open.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * format:		cddb, b - array of (dd)
 */
public class RequestBuyItem extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestBuyItem.class.getName());

	private int _listId;
	private int _count;
	private long[] _items; // count*2

	@Override
	public void readImpl()
	{
		_listId = readD();
		_count = readD();
		if(_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count <= 0)
		{
			_items = null;
			return;
		}
		_items = new long[_count * 2];
		for(int i = 0; i < _count; i++)
		{
			_items[i * 2 + 0] = readD();
			_items[i * 2 + 1] = readQ();
			if(_items[i * 2 + 0] < 1 || _items[i * 2 + 1] < 1)
			{
				_items = null;
				break;
			}
		}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if(_items == null || _count == 0)
			return;

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!ConfigValue.AltKarmaPlayerCanShop && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		// Проверяем, не подменили ли id
		if(activeChar.getBuyListId() != _listId)
		{
			Util.handleIllegalPlayerAction(activeChar, "RequestBuyItem[100]", "Tried to buy from buylist: " + _listId, 1);
			return;
		}

		L2NpcInstance npc = activeChar.getLastNpc();

		if("buy".equalsIgnoreCase(activeChar.getLastBbsOperaion()))
			activeChar.setLastBbsOperaion(null);
		else
		{
			boolean isValidMerchant = npc instanceof L2ClanHallManagerInstance || npc instanceof L2MerchantInstance || npc instanceof L2MercManagerInstance || npc instanceof L2CastleChamberlainInstance || npc instanceof L2NpcFriendInstance;
			if(!activeChar.isGM() && (npc == null || !isValidMerchant || !activeChar.isInRange(npc.getLoc(), npc.INTERACTION_DISTANCE+npc.BYPASS_DISTANCE_ADD)))
			{
				activeChar.sendActionFailed();
				return;
			}
		}

		L2NpcInstance merchant = null;
		if(npc != null && (npc instanceof L2MerchantInstance || npc instanceof L2ClanHallManagerInstance)) //TODO расширить список?
			merchant = npc;

		NpcTradeList list = TradeController.getInstance().getBuyList(_listId);
		if(list == null)
		{
			Log.add("tried to buy from non-exist list " + _listId, "errors", activeChar);
			activeChar.sendActionFailed();
			return;
		}

		List<L2ItemInstance> items = new ArrayList<L2ItemInstance>(_count);
		for(int i = 0; i < _count; i++)
		{
			int itemId = (int) _items[i * 2 + 0];
			long cnt = _items[i * 2 + 1];
			if(cnt <= 0)
			{
				activeChar.sendActionFailed();
				return;
			}

			TradeItem ic = getItemByItemId(itemId, list);
			//_log.info("TradeItem: itemId="+itemId+" getCurrentValue="+ic.getCurrentValue()+" isCountLimited="+ic.isCountLimited()+" cnt="+cnt+" getLastRechargeTime="+ic.getLastRechargeTime()+" getRechargeTime="+ic.getRechargeTime());
			if(ic.getCurrentValue() < cnt && ic.getCurrentValue() != -1 && ic.getRechargeTime() > 0)
				continue;

			L2ItemInstance inst = ItemTemplates.getInstance().createItem(itemId);
			if(inst == null)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(!inst.isStackable() && cnt != 1)
			{
				activeChar.sendActionFailed();
				return;
			}

			inst.setCount(cnt);
			items.add(inst);
		}

		long finalLoad = 0;
		int finalCount = activeChar.getInventory().getSize();
		int needsSpace = 2;
		int weight = 0;
		long currentMoney = activeChar.getAdena();

		int itemId;
		long cnt, price, tax = 0, totalCost = 0, subTotal = 0;
		double taxRate = 0;

		Castle castle = null;
		if(merchant != null)
		{
			castle = merchant.getCastle(activeChar);
			if(castle != null)
				taxRate = castle.getTaxRate();
		}

		for(int i = 0; i < items.size(); i++)
		{
			itemId = items.get(i).getItemId();
			cnt = items.get(i).getCount();
			needsSpace = 2;
			if(items.get(i).getItem().isStackable())
			{
				needsSpace = 1;
				if(activeChar.getInventory().getItemByItemId(itemId) != null)
					needsSpace = 0;
			}

			TradeItem ti = getItemByItemId(itemId, list);
			price = ti == null ? 0 : ti.getOwnersPrice();
			if(itemId >= 3960 && itemId <= 4921)
				price *= ConfigValue.RateSiegeGuardsPrice;

			if(price == 0 && !activeChar.getPlayerAccess().UseGMShop)
			{
				Util.handleIllegalPlayerAction(activeChar, "RequestBuyItem[191]", "Tried to buy zero price item, list " + _listId + " item " + itemId, 0);

				for(L2ItemInstance item : items)
					item.deleteMe();

				activeChar.sendMessage("Error: zero-price item! Please notify GM.");

				activeChar.sendActionFailed();
				return;
			}

			weight = items.get(i).getItem().getWeight();
			if(price < 0)
			{
				_log.warning("ERROR, no price found. Wrong buylist?");

				for(L2ItemInstance item : items)
					item.deleteMe();

				activeChar.sendActionFailed();
				return;
			}

			try
			{
				if(cnt < 0)
					throw new ArithmeticException("cnt < 0");
				subTotal = SafeMath.safeAddLong(subTotal, SafeMath.safeMulLong(cnt, price)); // Before tax

				tax = SafeMath.safeMulLong(subTotal, taxRate);
				totalCost = SafeMath.safeAddLong(subTotal, tax);
				if(totalCost < 0)
					throw new ArithmeticException("213: Tried to purchase negative " + totalCost + " adena worth of goods.");

				finalLoad = SafeMath.safeAddLong(finalLoad, SafeMath.safeMulLong(cnt, weight));
				if(finalLoad < 0)
					throw new ArithmeticException("254: Tried to purchase negative " + finalLoad + " adena worth of goods.");
			}
			catch(ArithmeticException e)
			{
				Util.handleIllegalPlayerAction(activeChar, "RequestBuyItem[157]", "merchant: " + merchant + ": " + e.getMessage(), 1);

				for(L2ItemInstance item : items)
					item.deleteMe();

				sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED, Msg.ActionFail);
				return;
			}

			if(needsSpace == 2)
				finalCount += cnt;
			else if(needsSpace == 1)
				finalCount += 1;
		}

		if(totalCost > currentMoney || subTotal < 0 || (currentMoney <= 0 && !activeChar.getPlayerAccess().UseGMShop))
		{
			for(L2ItemInstance item : items)
				item.deleteMe();

			sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, Msg.ActionFail);
			return;
		}

		if(!activeChar.getInventory().validateWeight(finalLoad))
		{
			for(L2ItemInstance item : items)
				item.deleteMe();

			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT, Msg.ActionFail);
			return;
		}

		if(!activeChar.getInventory().validateCapacity(finalCount))
		{
			for(L2ItemInstance item : items)
				item.deleteMe();

			sendPacket(Msg.YOUR_INVENTORY_IS_FULL, Msg.ActionFail);
			return;
		}

		// Для магазинов с ограниченным количеством товара число продаваемых предметов уменьшаем после всех проверок
		for (int i = 0; i < items.size(); i++)
		{
			itemId = items.get(i).getItemId();
			cnt = items.get(i).getCount();
			TradeItem ic = getItemByItemId(itemId, list);
			if (ic.getCurrentValue() < cnt && ic.getCurrentValue() != -1 && ic.isCountLimited())
			{
				activeChar.sendPacket(Msg.INCORRECT_ITEM_COUNT);
				activeChar.sendActionFailed();
				return;
			}
		}
		for(int i = 0; i < items.size(); i++)
		{
			itemId = items.get(i).getItemId();
			cnt = items.get(i).getCount();
			TradeItem ic = getItemByItemId(itemId, list);

			if(ic != null && ic.isCountLimited())
			{
				if(ic.getLastRechargeTime() + ic.getRechargeTime() <= System.currentTimeMillis() / 60000)
				{
					ic.setLastRechargeTime((int) (System.currentTimeMillis() / 60000));
					ic.setCurrentValue(ic.getCount());
				}
				ic.setCurrentValue((int) (ic.getCurrentValue() - cnt));
			}
		}

		L2ItemInstance dell_adena = activeChar.getInventory().destroyItemByItemId(57, totalCost, true, false);
		for(L2ItemInstance item : items)
		{
			Log.LogItem(activeChar, merchant, Log.BuyItem, item);
			activeChar.getInventory().addItem(item, true, true, false, false);
		}

		// Add tax to castle treasury if not owned by npc clan
		if(castle != null && castle.getOwnerId() > 0 && activeChar.getReflection().getId() == 0)
		{
			castle.addToTreasury(tax, true, false);
			Log.add(castle.getName() + "|" + tax + "|BuyItem", "treasury");
		}

		activeChar.sendPacket(new InventoryUpdate(items).addItem(dell_adena));
		sendPacket(new ExBuySellList(list, activeChar, taxRate).done());

		activeChar.updateStats();
	}

	private static final TradeItem getItemByItemId(int itemId, NpcTradeList list)
	{
		for(TradeItem ti : list.getItems())
			if(ti.getItemId() == itemId)
				return ti;
		return null;
	}
}