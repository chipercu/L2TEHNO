package l2open.gameserver.serverpackets;

public class ExInitializeSeed extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeD(0xB5);
		// TODO dx[dddd]
	}
}
