package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * Format: (chd) ddd
 * d: always -1
 * d: player team
 * d: player object id
 */
public class ExCubeGameChangeTeam extends L2GameServerPacket
{
	L2Player _player;
	boolean _fromRedTeam;

	public ExCubeGameChangeTeam(L2Player player, boolean fromRedTeam)
	{
		_player = player;
		_fromRedTeam = fromRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x98 : 0x97);
		writeD(0x05);

		writeD(_player.getObjectId());
		writeD(_fromRedTeam ? 0x01 : 0x00);
		writeD(_fromRedTeam ? 0x00 : 0x01);
	}
}