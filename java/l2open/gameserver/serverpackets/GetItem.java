package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.Location;

/**
 * 0000: 17  1a 95 20 48  9b da 12 40  44 17 02 00  03 f0 fc ff  98 f1 ff ff                                     .....
 * format  ddddd
 */
public class GetItem extends L2GameServerPacket
{
	private int _playerId, _itemObjId;
	private Location _loc;

	public GetItem(L2ItemInstance item, int playerId)
	{
		_itemObjId = item.getObjectId();
		_loc = item.getLoc();
		_playerId = playerId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x17);
		writeD(_playerId);
		writeD(_itemObjId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}