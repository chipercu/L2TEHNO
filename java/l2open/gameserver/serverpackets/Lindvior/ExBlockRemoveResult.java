package l2open.gameserver.serverpackets;

/**
 * @author Smo
 */
public class ExBlockRemoveResult extends L2GameServerPacket
{
	private final String _blockName;

	public ExBlockRemoveResult(String name)
	{
		_blockName = name;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xEE);
		writeD(0x01);
		writeS(_blockName);
	}
}
