package l2open.gameserver.serverpackets;

public class ExChangeAttributeFail extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExChangeAttributeFail();
	
	public ExChangeAttributeFail()
	{
		//
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x11B);
	}
}