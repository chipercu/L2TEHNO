package l2open.gameserver.serverpackets;

public class ExResponseShowContents extends L2GameServerPacket
{
	private final String _contents;

	public ExResponseShowContents(String contents)
	{
		_contents = contents;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xB0);
		writeS(_contents);
	}
}