package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * @author Bonux
**/
public class FriendStatus extends L2GameServerPacket
{
	private final int _id;
	private final String _name;
	private final boolean _login;

	public FriendStatus(L2Player friend , boolean login)
	{
		_id = friend.getObjectId();
		_name = friend.getName();
		_login = login;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x59);
		writeD(_login ? 0x01 : 0x00);
		writeS(_name);
		if(!_login)
			writeD(_id);
	}
}
