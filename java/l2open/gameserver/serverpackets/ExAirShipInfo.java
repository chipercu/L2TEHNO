package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.util.Location;

public class ExAirShipInfo extends L2GameServerPacket
{
	private int _objId, _speed1, _speed2, _fuel, _driverObjId, _ownerObjId;
	private Location _loc;
	private int _c_a=0;
	private int _c_b=0;
	private int _c_c=0;
	private int _c_d=0;
	private int _max_fuel=0;

	public ExAirShipInfo(L2AirShip ship)
	{
		_objId = ship.getObjectId();
		_loc = ship.getLoc();
		_speed1 = ship.getRunSpeed();
		_speed2 = ship.getRotationSpeed();
		_fuel = ship.getFuel();
		_driverObjId = ship.getDriver() == null ? 0 : ship.getDriver().getObjectId();
		_ownerObjId = ship.getOwner() == null ? 0 : ship.getOwner().getClanId();
		if(_ownerObjId > 0)
		{
			_c_a=0x016E;
			_c_b=0x006b;
			_c_c=0x015C;
			_c_d=0x0069;
			_max_fuel=L2AirShip.MAX_FUEL;
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x60);

		writeD(_objId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
		writeD(_driverObjId); // object id of player who control ship
		writeD(_speed1);
		writeD(_speed2);

		// clan airship related info
		writeD(_ownerObjId); // clan-owner object id?
		writeD(_c_a); // 366
		writeD(_ownerObjId); // 0
		writeD(_c_b); // 107
		writeD(_c_c); // 348
		writeD(_ownerObjId); // 0
		writeD(_c_d); // 105
		writeD(_fuel); // current fuel
		writeD(_max_fuel); // max fuel
	}
}