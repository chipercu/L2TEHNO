package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Character;
import l2open.util.Location;
import l2open.util.Util;

/**
 * format   dddddd		(player id, target id, distance, startx, starty, startz)<p>
 */
public class ValidateLocation extends L2GameServerPacket
{
	private int _chaObjId;
	private int _x;
	private int _y;
	private int _z;
	private int _h;
	private boolean isPlayer = false;

	public ValidateLocation(L2Character cha)
	{
		if(ConfigValue.DebugValidateLocationTrace && cha.isPlayer())
			Util.test();
		_chaObjId = cha.getObjectId();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_h = cha.getHeading();
		if(cha.isPlayer())
			isPlayer = true;
		if(ConfigValue.DebugValidateLocation)
			_log.info("["+isPlayer+"]["+_x+", "+_y+", "+_z+", "+_h+"]");
	}

	@Override
	protected final void writeImpl()
	{
		if(isPlayer && ConfigValue.DebugMovePackets && _chaObjId == 268485875)
		{
			_log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			_log.info("Server:-> ValidateLocation: _x="+_x+" _y="+_y+" _z="+_z);
		}
		writeC(0x79);

		writeD(_chaObjId);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_h);
	}

	@Override
	public String getType()
	{
		return super.getType()+"["+isPlayer+"]["+_x+", "+_y+", "+_z+", "+_h+"]";
	}
}