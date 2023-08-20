package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.model.items.PetInventory;
import l2open.util.Log;

import java.util.logging.Logger;

public class RequestGetItemFromPet extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestGetItemFromPet.class.getName());

	private int _objectId;
	private long _amount;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	public void readImpl()
	{
		_objectId = readD();
		_amount = readQ();
		_unknown = readD(); // = 0 for most trades
	}

	@Override
	public void runImpl()
	{
		if(_amount < 1)
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2PetInstance pet = (L2PetInstance) activeChar.getPet();
		if(pet == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		PetInventory petInventory = pet.getInventory();
		PcInventory playerInventory = activeChar.getInventory();

		L2ItemInstance petItem = petInventory.getItemByObjectId(_objectId);

		if(petItem == null)
		{
			_log.warning(activeChar.getName() + " requested item obj_id: " + _objectId + " from pet, but its not there.");
			return;
		}

		if(pet.getControlItem() == null || activeChar.getInventory().getItemByObjectId(pet.getControlItem().getObjectId()) == null)
			return;

		if(petItem.isEquipped())
		{
			activeChar.sendActionFailed();
			return;
		}

		long finalLoad = petItem.getItem().getWeight() * _amount;

		if(!activeChar.getInventory().validateWeight(finalLoad))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}

		L2ItemInstance item = petInventory.dropItem(_objectId, _amount, false, true);
		item.setCustomFlags(item.getCustomFlags() & ~L2ItemInstance.FLAG_PET_EQUIPPED, true);
		playerInventory.addItem(item);

		pet.sendItemList();
		pet.broadcastPetInfo();
		activeChar.updateStats();

		Log.LogItem(activeChar, activeChar.getPet(), Log.GetItemFromPet, petItem);
	}
}