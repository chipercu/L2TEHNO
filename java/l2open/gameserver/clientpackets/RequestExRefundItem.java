package l2open.gameserver.clientpackets;

import org.apache.commons.lang3.ArrayUtils;

import l2open.config.ConfigValue;
import l2open.gameserver.TradeController;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExBuySellList;
import l2open.util.GArray;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestExRefundItem extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private int[] _ids;

	/**
	 * format: d dx[d]
	 */
	@Override
	public void readImpl()
	{
		_listId = readD();
		_count = readD();

		if(_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_ids = new int[_count];
		for(int i = 0; i < _count; i++)
		{
			_ids[i] = readD();
			if(ArrayUtils.indexOf(_ids, _ids[i]) < i)
			{
				_count = 0;
				break;
			}
		}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null || _count == 0)
			return;

		ConcurrentLinkedQueue<L2ItemInstance> list = activeChar.getInventory().getRefundItemsList();

		if(list == null || list.isEmpty())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2NpcInstance npc = activeChar.getLastNpc();

		boolean isValidMerchant = npc instanceof L2ClanHallManagerInstance || npc instanceof L2MerchantInstance || npc instanceof L2MercManagerInstance || npc instanceof L2CastleChamberlainInstance || npc instanceof L2NpcFriendInstance;
		if(!activeChar.isGM() && (npc == null || !isValidMerchant || !activeChar.isInRange(npc.getLoc(), npc.INTERACTION_DISTANCE+npc.BYPASS_DISTANCE_ADD)))
		{
			activeChar.sendActionFailed();
			return;
		}

		GArray<L2ItemInstance> toreturn = new GArray<L2ItemInstance>(_ids.length);
		long price = 0, weight = 0;

		for(int itemId : _ids)
			for(L2ItemInstance item : list)
				if(item.getObjectId() == itemId)
				{
					price += ConfigValue.SellItemOneAdena ? item.getCount() : item.getCount() * item.getReferencePrice() / ConfigValue.SellItemDiv;
					weight += item.getCount() * item.getItem().getWeight();
					toreturn.add(item);
				}
		price -= price*ConfigValue.SellITaxPer/100;

		if(toreturn.isEmpty())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getAdena() < price)
		{
			activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateWeight(weight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateCapacity(toreturn))
		{
			sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateWeight(weight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			activeChar.sendActionFailed();
			return;
		}

		activeChar.reduceAdena(price, true);

		for(L2ItemInstance itm : toreturn)
		{
			list.remove(itm);
			activeChar.getInventory().addItem(itm);
		}

		double taxRate = 0;
		Castle castle = null;
		if(npc != null)
		{
			castle = npc.getCastle(activeChar);
			if(castle != null)
				taxRate = castle.getTaxRate();
		}

		activeChar.sendPacket(/*new ExRefundList(activeChar), */new ExBuySellList(TradeController.getInstance().getBuyList(_listId), activeChar, taxRate).done());
		activeChar.updateStats();
	}
}