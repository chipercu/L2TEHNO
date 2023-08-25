package l2open.gameserver.serverpackets;

public class ExShowVariationCancelWindow extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x52);
	}
}