package l2open.gameserver.serverpackets;

public class ExNotifyPremiumItem extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExNotifyPremiumItem();

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x86 : 0x85);
		// just trigger
	}
}