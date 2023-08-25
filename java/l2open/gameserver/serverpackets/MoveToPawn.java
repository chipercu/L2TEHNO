package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;

import java.util.logging.Logger;

/**
 * format   dddddd		(player id, target id, distance, startx, starty, startz)<p>
 */
public class MoveToPawn extends L2GameServerPacket
{
	private static Logger _log = Logger.getLogger(MoveToPawn.class.getName());
	private int _chaId, _targetId, _distance;

	private int _x, _y, _z, _tx, _ty, _tz;
	private L2Character _cha;
	private L2Character _target;

	public MoveToPawn(L2Character cha, int target_id, int t_x, int t_y, int t_z, int distance)
	{
		_chaId = cha.getObjectId();
		_targetId = target_id;

		if(_chaId == _targetId)
		{
			_log.warning("Try pawn to yourself!");
			Thread.dumpStack();
			_chaId = 0;
			return;
		}

		_distance = distance;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = t_x;
		_ty = t_y;
		_tz = t_z;
	}

	public MoveToPawn(L2Character cha, L2Character target, int distance)
	{
		_chaId = cha.getObjectId();
		_targetId = target.getObjectId();
		
		_cha = cha;
		_target = target;

		if(_chaId == _targetId)
		{
			_log.warning("Try pawn to yourself!");
			Thread.dumpStack();
			_chaId = 0;
			return;
		}

		_distance = distance;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();

		//_tx = cha._move_data._x_destination;
		//_ty = cha._move_data._y_destination;
		//_tz = cha._move_data._z_destination;

		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		if(_chaId == 0)
			return;

		if(ConfigValue.DebugMovePackets && _chaId == 268485875)
		{
			_log.info("=========================================================================================");
			_log.info("Server:-> MoveToPawn: ["+_x+", "+_y+", "+_z+"]["+_distance+"]["+_tx+", "+_ty+", "+_tz+"]");
		}
		writeC(0x72);

		writeD(_chaId);
		writeD(_targetId);
		writeD(_distance);

		writeD(_cha.getX());
		writeD(_cha.getY());
		writeD(_cha.getZ());

		/*l2open.util.Location dest = _cha.getIntersectionPoint(_cha);
		writeD(dest.x);
		writeD(dest.y);
		writeD(dest.z);*/

		writeD(_target.getX());
		writeD(_target.getY());
		writeD(_target.getZ());
	}
	/*@Override
	protected final void writeImpl()
	{
		if(_chaId == 0)
			return;

		writeC(0x72);

		writeD(_chaId);
		writeD(_targetId);
		writeD(_distance);

		writeD(_x);
		writeD(_y);
		writeD(_z);

		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}*/

	@Override
	public String getType()
	{
		return super.getType()+"["+_x+", "+_y+", "+_z+"]["+_distance+"]["+_tx+", "+_ty+", "+_tz+"]";
	}
}