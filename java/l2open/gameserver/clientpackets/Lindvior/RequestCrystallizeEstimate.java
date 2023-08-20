package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.ExGetCrystalizingEstimation;

public class RequestCrystallizeEstimate extends L2GameClientPacket
{
	private int _objectId;
	private long _count;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readQ();
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}
		if(activeChar == null || activeChar.is_block)
			return;

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM, Msg.ActionFail);
			return;
		}

		/*if(activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}*/

		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!item.canBeCrystallized(activeChar, true))
		{
			activeChar.sendPacket(Msg.THIS_ITEM_CANNOT_BE_CRYSTALIZED);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		activeChar.sendPacket(new ExGetCrystalizingEstimation(item));
	}
}