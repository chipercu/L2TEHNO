package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.util.Location;

public class ExStopMoveAirShip extends L2GameServerPacket
{
	private int boat_id;
	private Location _loc;

	/*
	 * структура пакета не точна, это лишь предположения 
	 */
	public ExStopMoveAirShip(L2AirShip boat)
	{
		boat_id = boat.getObjectId();
		_loc = boat.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x66);
		writeD(boat_id);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}