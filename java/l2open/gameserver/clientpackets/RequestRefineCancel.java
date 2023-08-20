package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExVariationCancelResult;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Item;

public final class RequestRefineCancel extends L2GameClientPacket
{
	//format: (ch)d
	private int _targetItemObjId;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(System.currentTimeMillis() - activeChar.getLastRequestRefineCancelPacket() < ConfigValue.RequestRefineCancelPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestRefineCancelPacket();
		L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);

		// cannot remove augmentation from a not augmented item
		if(targetItem == null || !targetItem.isAugmented() || (targetItem.getItem().isPvP() && !ConfigValue.AltAugmentPvPItem))
		{
			activeChar.sendPacket(new ExVariationCancelResult(0), Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}

		// get the price
		int price = getRemovalPrice(targetItem.getItem());

		if(price < 0)
		{
			_log.info("RequestRefineCancel(40): AdenaCount=-1 item="+targetItem.getItemId());
			activeChar.sendPacket(new ExVariationCancelResult(0));
		}

		// try to reduce the players adena
		if(activeChar.getAdena() < price)
		{
			activeChar.sendPacket(new ExVariationCancelResult(0), Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		activeChar.reduceAdena(price, true);

		// cancel boni
		targetItem.getAugmentation().removeBoni(activeChar, true);

		// remove the augmentation
		PlayerData.getInstance().removeAugmentation(targetItem);

		// send inventory update
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);

		// send system message
		SystemMessage sm = new SystemMessage(SystemMessage.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
		sm.addItemName(targetItem.getItemId());
		activeChar.sendPacket(new ExVariationCancelResult(1), iu, sm);

		activeChar.broadcastUserInfo(true);
	}

	public static int getRemovalPrice(L2Item item)
	{
		switch(item.getItemGrade().cry)
		{
			case L2Item.CRYSTAL_C:
				if(item.getCrystalCount() < 1720)
					return ConfigValue.RequestRefineCancel_C1;
				else if(item.getCrystalCount() < 2452)
					return ConfigValue.RequestRefineCancel_C2;
				else
					return ConfigValue.RequestRefineCancel_C3;
			case L2Item.CRYSTAL_B:
				if(item.getCrystalCount() < 1746)
					return ConfigValue.RequestRefineCancel_B1;
				else
					return ConfigValue.RequestRefineCancel_B2;
			case L2Item.CRYSTAL_A:
				if(item.getCrystalCount() < 2160)
					return ConfigValue.RequestRefineCancel_A1;
				else if(item.getCrystalCount() < 2824 || item.getName().contains("Mardil's Fan"))
					return ConfigValue.RequestRefineCancel_A2;
				else
					return ConfigValue.RequestRefineCancel_A3;
			case L2Item.CRYSTAL_S:
				if(item.getName().contains("Tateossian") || item.getCrystalCount() == 2052) // S low + jewel low
					return ConfigValue.RequestRefineCancel_S1;
				else if(item.getName().contains("Dynasty") || item.getName().contains("Moirai") || item.getName().contains("Icarus") || item.getName().contains("Butcher Blades") || item.getName().contains("Blades of Delusion") || item.getName().contains("Blood Brother")) // Dynasty\Moirai\Icarus
					return ConfigValue.RequestRefineCancel_S2;
				else if(item.getName().contains("Vesper") || item.getName().contains("Claw of Destruction") || item.getName().contains("Hellblade"))
					return ConfigValue.RequestRefineCancel_S3;
				else if(item.getName().contains("Vorpal") || item.getCrystalCount() == 8233)
					return ConfigValue.RequestRefineCancel_S4;
				else if(item.getName().contains("Elegia") || item.getCrystalCount() == 11421)
					return ConfigValue.RequestRefineCancel_S5;
				else if(item.getCrystalCount() == 9872)
					return ConfigValue.RequestRefineCancel_S6;
				else
					return ConfigValue.RequestRefineCancelOld_s;
				// any other item type is not augmentable
			default:
				return ConfigValue.RequestRefineCancelOldGrade;
		}
	}
}