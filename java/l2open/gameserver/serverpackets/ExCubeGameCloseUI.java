package l2open.gameserver.serverpackets;

/**
 * Format: (chd)
 */
public class ExCubeGameCloseUI extends L2GameServerPacket
{
	int _seconds;

	public ExCubeGameCloseUI()
	{}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x98 : 0x97);
		writeD(0xffffffff);
	}
}