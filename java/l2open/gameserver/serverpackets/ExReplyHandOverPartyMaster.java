package l2open.gameserver.serverpackets;

public class ExReplyHandOverPartyMaster extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xC4);
		// just a trigger
	}
}
