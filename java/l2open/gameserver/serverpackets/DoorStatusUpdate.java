package l2open.gameserver.serverpackets;

import l2open.gameserver.model.instances.L2DoorInstance;

/**
 * 61
 * d6 6d c0 4b		door id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 00 00 00 00 		??
 *
 * format  dddd    rev 377  ID:%d X:%d Y:%d Z:%d
 *         ddddd   rev 419
 */
public class DoorStatusUpdate extends L2GameServerPacket
{
	private int obj_id, door_id, _opened, dmg, isenemy, curHp, maxHp;

	@Deprecated
	public DoorStatusUpdate(L2DoorInstance door)
	{
		obj_id = door.getObjectId();
		door_id = door.getDoorId();
		_opened = door.isOpen() ? 0 : 1;
		dmg = door.getDamage();
		isenemy = door.isEnemyOf(getClient().getActiveChar()) ? 1 : 0;
		curHp = (int) door.getCurrentHp();
		maxHp = door.getMaxHp();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4D);
		writeD(obj_id);
		writeD(_opened);
		writeD(dmg);
		writeD(isenemy);
		writeD(door_id);
		writeD(maxHp);
		writeD(curHp);
	}
}