package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2Ship;

public class GetOffVehicle extends L2GameServerPacket
{
	private int _x, _y, _z, char_obj_id, boat_obj_id;

	public GetOffVehicle(L2Player activeChar, L2Ship boat, int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
		char_obj_id = activeChar.getObjectId();
		boat_obj_id = boat.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6f);
		writeD(char_obj_id);
		writeD(boat_obj_id);
		writeD(_x);
		writeD(_y);
		writeD(_z);
	}
}