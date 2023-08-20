package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Log;

/**
 * format:		cdd
 * format:		cdQ - Gracia Final
 */
public class RequestDestroyItem extends L2GameClientPacket
{
	private int _objectId;
	private long _count;

	@Override
	public void readImpl()
	{
		_objectId = readD();
		_count = readQ();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		long count = _count;

		L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);

		if(itemToRemove == null)
			return;

		if(count < 1)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
			return;
		}

		if(itemToRemove.isHeroWeapon())
		{
			activeChar.sendPacket(Msg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
			return;
		}

		if(!itemToRemove.canBeDestroyed(activeChar))
		{
			activeChar.sendPacket(Msg.THIS_ITEM_CANNOT_BE_DISCARDED);
			return;
		}

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if(activeChar.getPet() != null && activeChar.getPet().getControlItemObjId() == itemToRemove.getObjectId())
		{
			activeChar.sendPacket(Msg.THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED);
			return;
		}

		if(activeChar.isActionsDisabled() || !activeChar.canItemAction())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(_count > itemToRemove.getCount())
			count = itemToRemove.getCount();

		if(itemToRemove.isEquipped())
		{
			activeChar.getInventory().unEquipItemInSlot(itemToRemove.getEquipSlot());
			activeChar.broadcastUserInfo(true);
		}

		if(itemToRemove.canBeCrystallized(activeChar, false))
		{
			RequestCrystallizeItem.crystallize(activeChar, itemToRemove);
			return;
		}

		L2ItemInstance removedItem = activeChar.getInventory().destroyItem(_objectId, count, true);

		Log.LogItem(activeChar, Log.DeleteItem, removedItem);

		activeChar.sendChanges();
		activeChar.sendPacket(SystemMessage.removeItems(removedItem.getItemId(), count));
	}
}