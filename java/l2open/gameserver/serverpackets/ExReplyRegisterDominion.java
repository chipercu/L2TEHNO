package l2open.gameserver.serverpackets;

public class ExReplyRegisterDominion extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x91);
		_log.info("WTF? ExReplyRegisterDominion");
		// TODO dddddd
	}
}