package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;

public class GMViewItemList extends L2GameServerPacket
{
	private L2ItemInstance[] _items;
	private L2Player _player;

	public GMViewItemList(L2Player cha)
	{
		_items = cha.getInventory().getItems();
		_player = cha;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9a);
		writeS(_player.getName());
		writeD(_player.getInventoryLimit()); //c4?
		writeH(1); // show window ??

		writeH(_items.length);
		for(L2ItemInstance temp : _items)
			writeItemInfo(temp);
	}
}