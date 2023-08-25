package l2open.gameserver.serverpackets;

public class ExResartResponse extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xC8);
	}
}
