package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.util.Location;

public class ExGetOnAirShip extends L2GameServerPacket
{
	private int _char_id, _boat_id;

	private int _tx;
	private int _ty;
	private int _tz;

	public ExGetOnAirShip(L2Player cha, L2AirShip boat, Location loc)
	{
		_char_id = cha.getObjectId();
		_boat_id = boat.getObjectId();
		_tx = loc.x;
		_ty = loc.y;
		_tz = loc.z;
	}

	public ExGetOnAirShip(L2Player cha, L2AirShip boat, int x, int y, int z)
	{
		_char_id = cha.getObjectId();
		_boat_id = boat.getObjectId();
		_tx = x;
		_ty = y;
		_tz = z;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x64 : 0x63);
		writeD(_char_id);
		writeD(_boat_id);
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}
}