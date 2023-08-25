package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestSaveInventoryOrder extends L2GameClientPacket
{
	// format: (ch)db, b - array of (dd)
	int[][] _items;

	@Override
	public void readImpl()
	{
		int size = readD();
		if(size > 125)
			size = 125;
		if(!checkReadArray(size, 8) || size == 0)
		{
			_items = null;
			return;
		}
		_items = new int[size][2];
		for(int i = 0; i < size; i++)
		{
			_items[i][0] = readD(); // item id
			_items[i][1] = readD(); // slot
		}
	}

	@Override
	public void runImpl()
	{
		if(_items == null)
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.getInventory().sort(_items);
	}
}