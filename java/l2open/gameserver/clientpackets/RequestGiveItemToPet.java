package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.model.items.PetInventory;
import l2open.gameserver.tables.PetDataTable;
import l2open.util.*;

public class RequestGiveItemToPet extends L2GameClientPacket
{
	private int _objectId;
	private long _amount;

	@Override
	public void readImpl()
	{
		_objectId = readD();
		_amount = readQ();
	}

	@Override
	public void runImpl()
	{
		if(_amount < 1)
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		L2PetInstance pet = (L2PetInstance) activeChar.getPet();
		if(pet == null || pet.isDead())
		{
			sendPacket(Msg.CANNOT_GIVE_ITEMS_TO_A_DEAD_PET);
			return;
		}

		if(pet.getControlItem() == null || activeChar.getInventory().getItemByObjectId(pet.getControlItem().getObjectId()) == null)
			return;

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(_objectId == pet.getControlItemObjId())
		{
			activeChar.sendActionFailed();
			return;
		}

		PetInventory petInventory = pet.getInventory();
		PcInventory playerInventory = activeChar.getInventory();

		L2ItemInstance playerItem = playerInventory.getItemByObjectId(_objectId);
		if(playerItem == null || playerItem.getObjectId() == pet.getControlItemObjId() || PetDataTable.isPetControlItem(playerItem) || (ConfigValue.OnlySendPetItem != null && ConfigValue.OnlySendPetItem[0] > 0 && !Util.contains_int(ConfigValue.OnlySendPetItem, playerItem.getItemId())))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(pet.getInventory().getTotalWeight() + playerItem.getItem().getWeight() * _amount >= pet.getMaxLoad())
		{
			activeChar.sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
			return;
		}

		if(!playerItem.canBeDropped(activeChar, false))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(_amount >= playerItem.getCount())
		{
			playerInventory.dropItem(_objectId, playerItem.getCount(), false, true);
			playerItem.setCustomFlags(playerItem.getCustomFlags() | L2ItemInstance.FLAG_PET_EQUIPPED, true);
			petInventory.addItem(playerItem);
		}
		else
		{
			L2ItemInstance newPetItem = playerInventory.dropItem(_objectId, _amount, false, true);
			petInventory.addItem(newPetItem);
		}

		pet.sendItemList();
		pet.broadcastPetInfo();
		activeChar.updateStats();

		Log.LogItem(activeChar, pet, Log.GiveItemToPet, playerItem);
	}
}