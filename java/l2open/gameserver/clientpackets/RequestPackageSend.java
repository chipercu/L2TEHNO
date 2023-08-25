package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.PcFreight;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.model.items.Warehouse;
import l2open.util.Util;

import java.util.HashMap;

/**
 * Format: cddb, b - array of (dd)
 * @author SYS
 */
public class RequestPackageSend extends L2GameClientPacket
{
	private int _objectID;
	private HashMap<Integer, Long> _items;

	private static int _FREIGHT_FEE = 1000;

	@Override
	public void readImpl()
	{
		_objectID = readD();
		int itemsCount = readD();
		if(itemsCount * 12 > _buf.remaining() || itemsCount > Short.MAX_VALUE || itemsCount <= 0)
		{
			_items = null;
			return;
		}
		_items = new HashMap<Integer, Long>(itemsCount + 1, 0.999f);
		for(int i = 0; i < itemsCount; i++)
		{
			int obj_id = readD(); // this is some id sent in PackageSendableList
			long itemQuantity = readQ();
			if(obj_id < 1 || itemQuantity < 1)
			{
				_items = null;
				return;
			}
			_items.put(obj_id, itemQuantity);
		}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _items == null || !activeChar.getPlayerAccess().UseWarehouse || activeChar.is_block)
			return;

		PcInventory inventory = activeChar.getInventory();
		long adenaDeposit = 0;
		int adenaObjId;
		L2ItemInstance adena = inventory.getItemByItemId(57);
		if(adena != null)
			adenaObjId = adena.getObjectId();
		else
			adenaObjId = -1;
		for(Integer itemObjectId : _items.keySet())
		{
			L2ItemInstance item = inventory.getItemByObjectId(itemObjectId);
			if(item == null || item.isEquipped())
				return;

			if(_items.get(itemObjectId) < 0)
				return;

			if(itemObjectId == adenaObjId)
				adenaDeposit = _items.get(itemObjectId);
		}

		L2NpcInstance freighter = activeChar.getLastNpc();
		if(freighter == null || !activeChar.isInRange(freighter.getLoc(), freighter.INTERACTION_DISTANCE+freighter.BYPASS_DISTANCE_ADD))
		{
			activeChar.sendPacket(Msg.YOU_FAILED_AT_SENDING_THE_PACKAGE_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_WAREHOUSE);
			return;
		}

		String tradeBan = activeChar.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			activeChar.sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
			return;
		}

		int fee = _items.size() * _FREIGHT_FEE;

		if(fee + adenaDeposit > activeChar.getAdena())
		{
			activeChar.sendPacket(Msg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
			return;
		}

		Warehouse warehouse = new PcFreight(_objectID);

		// Item Max Limit Check
		if(_items.size() + warehouse.listItems(ItemClass.ALL).length > activeChar.getFreightLimit())
		{
			activeChar.sendPacket(Msg.THE_CAPACITY_OF_THE_WAREHOUSE_HAS_BEEN_EXCEEDED);
			return;
		}

		// Transfer the items from activeChar's Inventory Instance to destChar's Freight Instance
		for(Integer itemObjectId : _items.keySet())
		{
			L2ItemInstance found = inventory.getItemByObjectId(itemObjectId);
			if(found == null || !found._is_premium)
				continue;

			warehouse.addItem(inventory.dropItem(found, _items.get(itemObjectId), false), null);
		}

		activeChar.reduceAdena(fee, true);
		activeChar.updateStats();

		// Delete destination L2Player used for freight
		activeChar.sendPacket(Msg.THE_TRANSACTION_IS_COMPLETE);
	}
}