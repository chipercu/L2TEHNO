package l2open.gameserver.serverpackets;

public class ExCloseRaidSocket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xB8);
		// TODO dx[dddd]
	}
}
