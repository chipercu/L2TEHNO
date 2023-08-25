package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * format: d
 */
public class ChairSit extends L2GameServerPacket
{
	private L2Player _activeChar;
	private int _staticObjectId;

	public ChairSit(L2Player player, int staticObjectId)
	{
		_activeChar = player;
		_staticObjectId = staticObjectId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xed);
		writeD(_activeChar.getObjectId());
		writeD(_staticObjectId);
	}
}