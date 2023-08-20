package l2open.gameserver.serverpackets;

public class ExDuelStart extends L2GameServerPacket
{
	int _duelType;

	public ExDuelStart(int duelType)
	{
		_duelType = duelType;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x4F : 0x4e);
		writeD(_duelType); // неизвестный, возможно тип дуэли.
	}
}