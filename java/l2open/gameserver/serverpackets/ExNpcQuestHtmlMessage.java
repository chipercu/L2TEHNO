package l2open.gameserver.serverpackets;

public class ExNpcQuestHtmlMessage extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x8D);
		// TODO dSd (UserID:%d)
	}
}