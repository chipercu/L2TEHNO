package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import java.util.List;

/**
 * Format: (chd) ddd[dS]d[dS]
 * d: unknown
 * d: always -1
 * d: blue players number
 * [
 * 		d: player object id
 * 		S: player name
 * ]
 * d: blue players number
 * [
 * 		d: player object id
 * 		S: player name
 * ]
 */
public class ExCubeGameTeamList extends L2GameServerPacket
{
	List<L2Player> _bluePlayers;
	List<L2Player> _redPlayers;
	int _roomNumber;

	public ExCubeGameTeamList(List<L2Player> redPlayers, List<L2Player> bluePlayers, int roomNumber)
	{
		_redPlayers = redPlayers;
		_bluePlayers = bluePlayers;
		_roomNumber = roomNumber - 1;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x98 : 0x97);
		writeD(0x00);

		writeD(_roomNumber);
		writeD(0xffffffff);

		writeD(_bluePlayers.size());
		for(L2Player player : _bluePlayers)
		{
			writeD(player.getObjectId());
			writeS(player.getName());
		}
		writeD(_redPlayers.size());
		for(L2Player player : _redPlayers)
		{
			writeD(player.getObjectId());
			writeS(player.getName());
		}
	}
}