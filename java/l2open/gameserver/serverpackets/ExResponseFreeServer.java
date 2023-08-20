package l2open.gameserver.serverpackets;

public class ExResponseFreeServer extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x77);
		// just trigger
	}
}