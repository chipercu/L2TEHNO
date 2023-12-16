package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.Location;

/**
 * 16
 * d6 6d c0 4b		player id who dropped it
 * ee cc 11 43 		object id
 * 39 00 00 00 		item id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 01 00 00 00 		show item-count 1=yes
 * 7a 00 00 00      count                                         .
 *
 * format  dddddddd    rev 377
 *         ddddddddd   rev 417
 *         dddddddQd	 Gracia Final
 */
public class DropItem extends L2GameServerPacket
{
	private Location _loc;
	private int _playerId, item_obj_id, item_id, stackable;
	private long long_count;

	/**
	 * Constructor<?> of the DropListTable server packet
	 * @param item : L2ItemInstance designating the item
	 * @param playerId : int designating the player ID who dropped the item
	 */
	public DropItem(L2ItemInstance item, int playerId)
	{
		_playerId = playerId;
		item_obj_id = item.getObjectId();
		item_id = item.getItemId();
		_loc = item.getLoc();
		stackable = item.isStackable() ? 1 : 0;
		long_count = item.getCount();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x16);
		writeD(_playerId);
		writeD(item_obj_id);
		writeD(item_id);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + ConfigValue.ClientZShift);
		writeD(stackable);
		writeQ(long_count);
		writeD(1); // unknown
	}

	@Override
	public String getType()
	{
		return super.getType(); // + "; object_id = "+_item.getObjectId();
	}
}