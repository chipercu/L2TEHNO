package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import org.napile.primitive.maps.IntObjectMap;

public class ExReceiveShowPostFriend extends L2GameServerPacket
{
	private IntObjectMap<String> _list;

	public ExReceiveShowPostFriend(L2Player player)
	{
		_list = player.getPostFriends();
	}

	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(getClient().isLindvior() ? 0xD4 : 0xD3);
		writeD(_list.size());
		for(String t : _list.values())
			writeS(t);
	}
}