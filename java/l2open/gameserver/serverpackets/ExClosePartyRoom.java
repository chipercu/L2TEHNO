package l2open.gameserver.serverpackets;

/**
 * Format:(ch)
 */
public class ExClosePartyRoom extends L2GameServerPacket
{
	public ExClosePartyRoom()
	{}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x09);
	}
}