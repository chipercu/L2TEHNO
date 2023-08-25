package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2ManufactureItem;
import l2open.gameserver.model.L2ManufactureList;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.serverpackets.*;

public class RequestRecipeShopListSet extends L2GameClientPacket
{
	// format: cdb, b - array of (dd)
	private int _count;
	L2ManufactureList createList = new L2ManufactureList();

	@Override
	public void readImpl()
	{
		_count = readD();
		if(_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 0)
		{
			_count = 0;
			return;
		}
		for(int x = 0; x < _count; x++)
		{
			int id = readD();
			long cost = readQ();
			if(id < 1 || cost < 0)
			{
				_count = 0;
				return;
			}
			createList.add(new L2ManufactureItem(id, cost));
		}
		_count = createList.size();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(System.currentTimeMillis() - activeChar.getLastRequestRecipeShopListSetPacket() < ConfigValue.RequestRecipeShopListSetPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestRecipeShopListSetPacket();

		if(!activeChar.canItemAction())
		{
			L2TradeList.cancelStore(activeChar);
			return;
		}
		if(!activeChar.checksForShop(true))
		{
			L2TradeList.cancelStore(activeChar);
			return;
		}

		if(activeChar.getNoChannel() != 0)
		{
			activeChar.sendPacket(Msg.YOU_ARE_CURRENTLY_BANNED_FROM_ACTIVITIES_RELATED_TO_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP);
			L2TradeList.cancelStore(activeChar);
			return;
		}

		if(_count == 0 || activeChar.getCreateList() == null)
		{
			L2TradeList.cancelStore(activeChar);
			return;
		}

		if(_count > ConfigValue.MaxPvtManufactureSlots)
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			L2TradeList.cancelStore(activeChar);
			return;
		}

		createList.setStoreName(activeChar.getCreateList().getStoreName());
		activeChar.setCreateList(createList);

		activeChar.setPrivateStoreType(L2Player.STORE_PRIVATE_MANUFACTURE);
		activeChar.broadcastPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));
		activeChar.broadcastUserInfo(true);
		activeChar.broadcastPacket(new RecipeShopMsg(activeChar));
		activeChar.sitDown(false);

		activeChar.sendActionFailed();
	}
}