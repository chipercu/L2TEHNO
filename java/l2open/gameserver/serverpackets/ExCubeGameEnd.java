package l2open.gameserver.serverpackets;

/**
 * Format: (chd) ddd
 * d: winner team
 */
public class ExCubeGameEnd extends L2GameServerPacket
{
	boolean _isRedTeamWin;
	boolean _isClear = false;

	public ExCubeGameEnd(boolean isRedTeamWin)
	{
		_isRedTeamWin = isRedTeamWin;
	}

	public ExCubeGameEnd(boolean isRedTeamWin, boolean isClear)
	{
		_isRedTeamWin = isRedTeamWin;
		_isClear = isClear;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x99 : 0x98);
		writeD(_isClear ? 0x00 : 0x01);
		writeD(_isRedTeamWin ? 0x01 : 0x00);
	}
}