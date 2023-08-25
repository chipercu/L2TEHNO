package l2open.gameserver.serverpackets;

public class ExSetMpccRouting extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x37);
		// TODO d
	}
}