package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Character;
import l2open.util.Location;
import l2open.util.Util;

/**
 * 0000: 01  7a 73 10 4c  b2 0b 00 00  a3 fc 00 00  e8 f1 ff    .zs.L...........
 * 0010: ff  bd 0b 00 00  b3 fc 00 00  e8 f1 ff ff             .............
 *
 * ddddddd
 */
public class CharMoveToLocation extends L2GameServerPacket
{
	private int _objectId, _client_z_shift, _client_target_z_shift;
	private int _dx;
	private int _dy;
	private int _dz;
	private int _x;
	private int _y;
	private int _z;
	private boolean isPlayer = false;

	public CharMoveToLocation(L2Character cha, int target_z, boolean firstMove)
	{
		if(cha.isPlayer())
		{
			isPlayer = true;
			if(ConfigValue.DebugCharMoveToLocationTrace)
				Util.test();
		}
		_objectId = cha.getObjectId();

		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();

		if(cha.getDestination() != null)
		{
			_dx = cha.getDestination().x;
			_dy = cha.getDestination().y;
			_dz = firstMove && cha.isInWater() && _dz < target_z ? target_z : cha.getDestination().z;
		}
		else
		{
			_dx = Integer.MIN_VALUE;
			_dy = Integer.MIN_VALUE;
			_dz = Integer.MIN_VALUE;
		}

		if(cha.isFlying())
			_client_z_shift = _client_target_z_shift = 0;
		else if(cha.isInWater())
		{
			_client_z_shift = -ConfigValue.GeoWaterZ;
			_client_target_z_shift = -ConfigValue.GeoWaterZ-10;
		}
		else
			_client_z_shift = _client_target_z_shift = ConfigValue.ClientZShift;
	}

	public CharMoveToLocation(int objectId, Location loc, Location dloc)
	{
		_objectId = objectId;
		_x = loc.x;
		_y = loc.y;
		_z = loc.z;
		_dx = dloc.x;
		_dy = dloc.y;
		_dz = dloc.z;
	}

	public CharMoveToLocation(int objectId, int x, int y, int z, int dx, int dy, int dz)
	{
		_objectId = objectId;
		_x = x;
		_y = y;
		_z = z;
		_dx = dx;
		_dy = dy;
		_dz = dz;
	}

	@Override
	protected final void writeImpl()
	{
		if(_dx == Integer.MIN_VALUE && _dy == Integer.MIN_VALUE && _dz == Integer.MIN_VALUE)
			return;
		if(isPlayer && ConfigValue.DebugMovePackets)
		{
			_log.info("=========================================================================================");
			_log.info("Server:-> CharMove: _dx="+_dx+" _dy="+_dy+" _dz="+_dz+" _x="+_x+" _y="+_y+" _z="+_z+" _client_z_shift="+_client_z_shift+" _client_target_z_shift="+_client_target_z_shift);
		}
		writeC(0x2f);

		writeD(_objectId);

		writeD(_dx);
		writeD(_dy);
		writeD(_dz + _client_target_z_shift);

		writeD(_x);
		writeD(_y);
		writeD(_z + _client_z_shift);
	}

	@Override
	public String getType()
	{
		return super.getType()+"["+_x+", "+_y+", "+_z+"]["+(new Location(_x,_y,_z).distance(new Location(_dx,_dy,_dz)))+"]["+_dx+", "+_dy+", "+_dz+"]";
	}
}