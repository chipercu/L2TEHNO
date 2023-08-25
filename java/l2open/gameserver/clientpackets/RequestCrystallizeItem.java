package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Log;

public class RequestCrystallizeItem extends L2GameClientPacket
{
	//Format: cdd

	private int _objectId;
	@SuppressWarnings("unused")
	private long _count;

	@Override
	public void readImpl()
	{
		_objectId = readD();
		_count = readQ(); //FIXME: count??
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null || activeChar.is_block)
			return;

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM, Msg.ActionFail);
			return;
		}

		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);

		if(item == null || !item.canBeCrystallized(activeChar, true))
		{
			activeChar.sendActionFailed();
			return;
		}

		crystallize(activeChar, item);
	}

	public static void crystallize(L2Player activeChar, L2ItemInstance item)
	{
		activeChar.getInventory().destroyItem(item, 1, true);

		// add crystals
		int crystalAmount = item.getItem().getCrystalCount(item.getRealEnchantLevel());
		int crystalId = item.getItem().getCrystalType().cry;

		L2ItemInstance crystalls = activeChar.getInventory().addItem(crystalId, crystalAmount);

		activeChar.sendPacket(Msg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED, new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1).addItemName(crystalId).addNumber(crystalAmount));

		Log.LogItem(activeChar, Log.CrystalizeItem, item);
		Log.LogItem(activeChar, Log.Sys_GetItem, crystalls);
	}
}