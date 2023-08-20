package l2open.gameserver.serverpackets;

public class ExEventMatchGMTest extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x07);
		// just trigger
	}
}