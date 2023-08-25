package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class L2FriendStatus extends L2GameServerPacket
{
	private String _charName;
	private boolean _login = false;

	public L2FriendStatus(L2Player player, boolean login)
	{
		if(player == null)
			return;
		_login = login;
		_charName = player.getName();
	}

	@Override
	protected final void writeImpl()
	{
		if(_charName == null)
			return;
		writeC(0x77);
		writeD(_login ? 1 : 0); //Logged in 1 logged off 0
		writeS(_charName);
		writeD(0); //id персонажа с базы оффа, не object_id
	}
}