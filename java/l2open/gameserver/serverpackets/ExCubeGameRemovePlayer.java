package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * Format: (chd) ddd
 * d: always -1
 * d: player team
 * d: player object id
 */
public class ExCubeGameRemovePlayer extends L2GameServerPacket
{
	L2Player _player;
	boolean _isRedTeam;

	public ExCubeGameRemovePlayer(L2Player player, boolean isRedTeam)
	{
		_player = player;
		_isRedTeam = isRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x98 : 0x97);
		writeD(0x02);

		writeD(0xffffffff);

		writeD(_isRedTeam ? 0x01 : 0x00);
		writeD(_player.getObjectId());
	}
}