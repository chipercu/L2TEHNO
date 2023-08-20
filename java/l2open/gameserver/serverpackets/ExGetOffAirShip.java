package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.util.Location;

public class ExGetOffAirShip extends L2GameServerPacket
{
	private boolean _canWriteImpl = false;
	private int _charObjId, _shipObjId;
	private Location _loc;

	public ExGetOffAirShip(L2Player cha, L2AirShip boat, Location loc)
	{
		if(loc == null)
			return;

		_charObjId = cha.getObjectId();
		_shipObjId = boat.getObjectId();
		_loc = loc;

		_canWriteImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!_canWriteImpl)
			return;

		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x65 : 0x64);
		writeD(_charObjId);
		writeD(_shipObjId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}