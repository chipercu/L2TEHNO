package l2open.gameserver.serverpackets;

public class ExUISetting extends L2GameServerPacket
{
	private final byte data[];

	public ExUISetting(byte[] _data)
	{
		data = _data;
	}

	@Override
	protected void writeImpl()
	{
		if(data == null)
			return;
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x71 : 0x70);
		writeD(data.length);
		writeB(data);
	}
}
