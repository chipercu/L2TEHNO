package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Object;
import l2open.util.Location;

/**
 * format  dddd
 *
 * sample
 * 0000: 3a  69 08 10 48  02 c1 00 00  f7 56 00 00  89 ea ff    :i..H.....V.....
 * 0010: ff  0c b2 d8 61                                     ....a
 */
public class TeleportToLocation extends L2GameServerPacket
{
	private int _targetId;
	private int _x;
	private int _y;
	private int _z;
	private int _h;
	private int _valid = 0;

	public TeleportToLocation(L2Object cha, Location loc, int valid)
	{
		_targetId = cha.getObjectId();
		_x = loc.x;
		_y = loc.y;
		_z = loc.z;
		_h = loc.h;
		_valid = valid;
	}

	public TeleportToLocation(L2Object cha, int x, int y, int z, int valid)
	{
		_targetId = cha.getObjectId();
		_x = x;
		_y = y;
		_z = z;
		_h = cha.getHeading();
		_valid = valid;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x22);
		writeD(_targetId);
		writeD(_x);
		writeD(_y);
		writeD(_z + ConfigValue.ClientZShift);
		writeD(_valid); //IsValidation
		writeD(_h);
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x22);
		writeD(_targetId);
		writeD(_x);
		writeD(_y);
		writeD(_z + ConfigValue.ClientZShift);
		writeD(_valid); //IsValidation
		writeD(_h);
		writeD(0); // ??? 0
		getClient().sendPacket(new ExTeleportToLocationActivate());
		return true;
	}
}