package l2open.gameserver.serverpackets;

public class ExAutoSoulShot extends L2GameServerPacket
{
	private final int _itemId;
	private final boolean _type;

	public ExAutoSoulShot(int itemId, boolean type)
	{
		_itemId = itemId;
		_type = type;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x0c);

		writeD(_itemId);
		writeD(_type ? 1 : 0);
	}
}