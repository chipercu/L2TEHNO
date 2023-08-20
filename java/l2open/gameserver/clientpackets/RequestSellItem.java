package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.TradeController;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.serverpackets.ExBuySellList;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.util.Log;
import l2open.util.SafeMath;
import l2open.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * packet type id 0x37
 * format:		cddb, b - array if (ddd)
 */
public class RequestSellItem extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private long[] _items; // count*3

	@Override
	public void readImpl()
	{
		_listId = readD();
		_count = readD();
		if(_count * 16 > _buf.remaining() || _count > Short.MAX_VALUE || _count <= 0)
		{
			_items = null;
			return;
		}
		_items = new long[_count * 3];
		for(int i = 0; i < _count; i++)
		{
			_items[i * 3 + 0] = readD();
			_items[i * 3 + 1] = readD();
			_items[i * 3 + 2] = readQ();
			if(_items[i * 3 + 0] < 1 || _items[i * 3 + 1] < 1 || _items[i * 3 + 2] < 1)
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

		if(activeChar == null || _items == null || _count <= 0 || activeChar.is_block)
			return;
		else if(!ConfigValue.AltKarmaPlayerCanShop && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2NpcInstance npc = activeChar.getLastNpc();

		if("sell".equalsIgnoreCase(activeChar.getLastBbsOperaion()))
			activeChar.setLastBbsOperaion(null);
		else if(ConfigValue.RequestSellItemCheckLastNpc)
		{
			boolean isValidMerchant = npc instanceof L2ClanHallManagerInstance || npc instanceof L2MerchantInstance || npc instanceof L2MercManagerInstance || npc instanceof L2CastleChamberlainInstance || npc instanceof L2NpcFriendInstance;
			if(!activeChar.isGM() && (npc == null || !isValidMerchant || !activeChar.isInRange(npc.getLoc(), npc.INTERACTION_DISTANCE+npc.BYPASS_DISTANCE_ADD)))
			{
				activeChar.sendActionFailed();
				return;
			}
		}

		List<L2ItemInstance> _list = new ArrayList<L2ItemInstance>();
		for(int i = 0; i < _count; i++)
		{
			int objectId = (int) _items[i * 3 + 0];
			int itemId = (int) _items[i * 3 + 1];
			long cnt = _items[i * 3 + 2];

			if(cnt < 0)
			{
				Util.handleIllegalPlayerAction(activeChar, "Integer overflow", "RequestSellItem[100]", 0);
				continue;
			}
			else if(cnt == 0)
				continue;

			L2ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
			if(item == null || !item.canBeTraded(activeChar) || !item.getItem().isSellable())
			{
				activeChar.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
				return;
			}

			if(item.getItemId() != itemId)
			{
				Util.handleIllegalPlayerAction(activeChar, "Fake packet", "RequestSellItem[115]", 0);
				continue;
			}

			if(item.getCount() < cnt)
			{
				Util.handleIllegalPlayerAction(activeChar, "Incorrect item count", "RequestSellItem[121]", 0);
				continue;
			}

			long price = ConfigValue.SellItemOneAdena ? cnt : item.getReferencePrice() * cnt / ConfigValue.SellItemDiv;
			price -= price*ConfigValue.SellITaxPer/100;

			L2ItemInstance add_item = activeChar.getInventory().addItem(57, price, true, true, false, false);
			//if(!_list.contains(add_item))
			//	_list.add(add_item);
			Log.LogItem(activeChar, Log.SellItem, item);

			// If player sells the enchant scroll he is using, deactivate it
			if(activeChar.getEnchantScroll() != null && item.getObjectId() == activeChar.getEnchantScroll().getObjectId())
				activeChar.setEnchantScroll(null);

			L2ItemInstance refund = activeChar.getInventory().dropItem(item, cnt, true, false, false);
			//if(!_list.contains(refund))
			//	_list.add(refund);

			refund.setLocation(ItemLocation.VOID);
			ConcurrentLinkedQueue<L2ItemInstance> refundlist = activeChar.getInventory().getRefundItemsList();
			if(refund.isStackable())
			{
				boolean found = false;
				for(L2ItemInstance ahri : refundlist)
					if(ahri.getItemId() == refund.getItemId())
					{
						ahri.setCount(SafeMath.safeAddLongOrMax(ahri.getCount(), refund.getCount()));
						found = true;
						break;
					}
				if(!found)
					refundlist.add(refund);
			}
			else
				refundlist.add(refund);

			if(refundlist.size() > 12)
				refundlist.poll();
		}

		double taxRate = 0;
		Castle castle = null;
		if(npc != null)
		{
			castle = npc.getCastle(activeChar);
			if(castle != null)
				taxRate = castle.getTaxRate();
		}

		activeChar.sendPacket(new ExBuySellList(TradeController.getInstance().getBuyList(_listId), activeChar, taxRate).done());
		activeChar.updateStats();
	}
}