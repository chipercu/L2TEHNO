package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.vehicle.L2Ship;

public class VehicleStart extends L2GameServerPacket
{
	private L2Ship _boat;

	private int _state;

	public VehicleStart(L2Ship boat, int state)
	{
		_boat = boat;
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xC0);
		writeD(_boat.getObjectId());
		writeD(_state);
	}
}