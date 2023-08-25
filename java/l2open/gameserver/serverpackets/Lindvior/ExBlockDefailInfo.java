package l2open.gameserver.serverpackets;

/**
 * @author Smo
 */
public class ExBlockDefailInfo extends L2GameServerPacket
{
	private final String _blockName;
	private final String _blockMemo;
	
	public ExBlockDefailInfo(String name, String memo)
	{
		_blockName = name;
		_blockMemo = memo;
		
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xEF);
		writeS(_blockName);
		writeS(_blockMemo);
	}
}
