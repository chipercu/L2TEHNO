package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * Format: (chd) dddddd
 * d: time left
 * d: blue points
 * d: red points
 * d: team
 * d: player object id
 * d: player points
 */
public class ExCubeGameExtendedChangePoints extends L2GameServerPacket
{
	int _timeLeft;
	int _bluePoints;
	int _redPoints;
	boolean _isRedTeam;
	L2Player _player;
	int _playerPoints;

	public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, L2Player player, int playerPoints)
	{
		_timeLeft = timeLeft;
		_bluePoints = bluePoints;
		_redPoints = redPoints;
		_isRedTeam = isRedTeam;
		_player = player;
		_playerPoints = playerPoints;
	}

	@Override
	protected void writeImpl()
	{
		if(_player == null)
			return;
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x99 : 0x98);
		writeD(0x00);

		writeD(_timeLeft);
		writeD(_bluePoints);
		writeD(_redPoints);

		writeD(_isRedTeam ? 0x01 : 0x00);
		writeD(_player.getObjectId());
		writeD(_playerPoints);
	}
}