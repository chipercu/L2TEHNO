package l2open.gameserver.serverpackets;

public class ExOlympiadMatchEnd extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x2D);
	}
}