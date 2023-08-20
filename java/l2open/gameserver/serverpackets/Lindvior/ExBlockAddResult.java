package l2open.gameserver.serverpackets;

//import l2open.gameserver.dao.CharacterDAO;

/**
 * @author Smo
 */
public class ExBlockAddResult extends L2GameServerPacket
{
	private final String _blockName;
	
	public ExBlockAddResult(String name)
	{
		_blockName = name;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xED);
		writeD(0x01);
		writeS(_blockName);
	}
}
