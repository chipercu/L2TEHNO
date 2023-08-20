package l2open.gameserver.serverpackets;

public class ExRestartClient extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x48);
	}
}
