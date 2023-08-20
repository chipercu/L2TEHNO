package l2open.gameserver.serverpackets;

/**
 * Format: (chd) ddd
 * d: time left
 * d: blue points
 * d: red points
 */
public class ExCubeGameChangePoints extends L2GameServerPacket
{
	int _timeLeft;
	int _bluePoints;
	int _redPoints;

	public ExCubeGameChangePoints(int timeLeft, int bluePoints, int redPoints)
	{
		_timeLeft = timeLeft;
		_bluePoints = bluePoints;
		_redPoints = redPoints;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x99 : 0x98);
		writeD(0x02);

		writeD(_timeLeft);
		writeD(_bluePoints);
		writeD(_redPoints);
	}
}