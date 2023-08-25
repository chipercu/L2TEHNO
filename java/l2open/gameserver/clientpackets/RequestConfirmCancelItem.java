package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExPutItemResultForVariationCancel;

public class RequestConfirmCancelItem extends L2GameClientPacket
{
	// format: (ch)d
	int _itemId;

	@Override
	public void readImpl()
	{
		_itemId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemId);

		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!item.isAugmented())
		{
			activeChar.sendPacket(Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}

		activeChar.sendPacket(new ExPutItemResultForVariationCancel(item, RequestRefineCancel.getRemovalPrice(item.getItem())));
	}
}