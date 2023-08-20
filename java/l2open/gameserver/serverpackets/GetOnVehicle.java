package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2Ship;
import l2open.util.Location;

public class GetOnVehicle extends L2GameServerPacket
{
	private int char_obj_id, boat_obj_id;
	private int _tx;
	private int _ty;
	private int _tz;

	public GetOnVehicle(L2Player cha, L2Ship boat, Location loc)
	{
		char_obj_id = cha.getObjectId();
		boat_obj_id = boat.getObjectId();
		_tx = loc.x;
		_ty = loc.y;
		_tz = loc.z;
	}

	public GetOnVehicle(L2Player cha, L2Ship boat, int x, int y, int z)
	{
		char_obj_id = cha.getObjectId();
		boat_obj_id = boat.getObjectId();
		_tx = x;
		_ty = y;
		_tz = z;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6e);
		writeD(char_obj_id);
		writeD(boat_obj_id);
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}
}