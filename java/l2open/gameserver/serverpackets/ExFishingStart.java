package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Character;
import l2open.util.Location;

/**
 * Format (ch)ddddd
 */
public class ExFishingStart extends L2GameServerPacket
{
	private int _charObjId;
	private Location _loc;
	private int _fishType;
	private boolean _isNightLure;

	public ExFishingStart(L2Character character, int fishType, Location loc, boolean isNightLure)
	{
		_charObjId = character.getObjectId();
		_fishType = fishType;
		_loc = loc;
		_isNightLure = isNightLure;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x1e);
		writeD(_charObjId);
		writeD(_fishType); // fish type
		writeD(_loc.x); // x poisson
		writeD(_loc.y); // y poisson
		writeD(_loc.z); // z poisson
		writeC(_isNightLure ? 0x01 : 0x00); // 0 = day lure  1 = night lure
		writeC(0x00);
	}
}