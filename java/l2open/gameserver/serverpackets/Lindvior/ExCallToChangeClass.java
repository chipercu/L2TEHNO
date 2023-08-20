package l2open.gameserver.serverpackets;

/**
 * @author Darvin
 * @data 08.06.2012
 */
public class ExCallToChangeClass extends L2GameServerPacket
{
	private final int classId;
	private final boolean showMsg;
	
	public ExCallToChangeClass(int classId, boolean showMsg)
	{
		this.classId = classId;
		this.showMsg = showMsg;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xFE);
		writeD(classId); // New Class Id
		writeD(showMsg ? 0x01 : 0x00); // Show Message
	}
}
