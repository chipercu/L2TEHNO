package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.util.Location;

public class ExMoveToTargetInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id, target_id, _dist;
	private Location _loc;

	public ExMoveToTargetInAirShip(L2Player cha, L2AirShip boat, int targetId, int dist, Location origin)
	{
		char_id = cha.getObjectId();
		boat_id = boat.getObjectId();
		target_id = targetId;
		_dist = dist;
		_loc = origin;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x72 : 0x71);

		writeD(char_id); // ID:%d
		writeD(target_id); // TargetID:%d
		writeD(_dist); //Dist:%d		
		writeD(_loc.y); //OriginX:%d
		writeD(_loc.z); //OriginY:%d
		writeD(_loc.h); //OriginZ:%d
		writeD(boat_id); //AirShipID:%d
	}
}