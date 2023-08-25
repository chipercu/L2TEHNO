package l2open.gameserver.serverpackets;

public class ExDuelEnd extends L2GameServerPacket
{
	int _duelType;

	public ExDuelEnd(int duelType)
	{
		_duelType = duelType;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x50 : 0x4f);
		writeD(_duelType); //хз почему 0, возможно 1 для party duel. нет возможности проверить.
	}
}