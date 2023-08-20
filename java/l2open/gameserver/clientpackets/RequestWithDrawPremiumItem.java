package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.PremiumItem;
import l2open.gameserver.serverpackets.ExGetPremiumItemList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.xml.ItemTemplates;

public final class RequestWithDrawPremiumItem extends L2GameClientPacket
{
	private int _itemNum;
	private int _charId;
	private long _itemcount;

	@Override
	protected void readImpl()
	{
		_itemNum = readD();
		_charId = readD();
		_itemcount = readQ();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;
		if(_itemcount <= 0)
			return;

		if(activeChar.getObjectId() != _charId)
			return;
		if(activeChar.getPremiumItemList().isEmpty())
			return;
		if(activeChar.getWeightPenalty() >= 3 || activeChar.getInventoryLimit() * 0.8 <= activeChar.getInventory().getSize())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT);
			return;
		}
		//if(activeChar.isProcessingRequest())
		//{
			//activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
		//	return;
		//}

		PremiumItem _item = activeChar.getPremiumItemList().get(_itemNum);
		if(_item == null)
			return;
		boolean stackable = ItemTemplates.getInstance().getTemplate(_item.getItemId()).isStackable();
		if(_item.getCount() < _itemcount)
			return;
		if(!stackable)
			for(int i = 0; i < _itemcount; i++)
				addItem(activeChar, _item.getItemId(), 1);
		else
			addItem(activeChar, _item.getItemId(), _itemcount);
		if(_itemcount < _item.getCount())
		{
			activeChar.getPremiumItemList().get(_itemNum).updateCount(_item.getCount() - _itemcount);
			PlayerData.getInstance().updatePremiumItem(activeChar, _itemNum, _item.getCount() - _itemcount);
		}
		else
		{
			activeChar.getPremiumItemList().remove(_itemNum);
			PlayerData.getInstance().deletePremiumItem(activeChar, _itemNum);
		}

		if(activeChar.getPremiumItemList().isEmpty())
			activeChar.sendPacket(Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
		else
			activeChar.sendPacket(new ExGetPremiumItemList(activeChar));
	}

	private void addItem(L2Player player, int itemId, long count)
	{
		player.getInventory().addItem(itemId, count);
		player.sendPacket(SystemMessage.obtainItems(itemId, count, 0));
	}
}