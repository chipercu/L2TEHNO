package l2open.gameserver.serverpackets;

public class ExGoodsInventoryChangedNotiPacket extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExGoodsInventoryChangedNotiPacket();

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x111);
		// just a trigger
	}
}
