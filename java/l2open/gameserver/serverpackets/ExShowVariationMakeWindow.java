package l2open.gameserver.serverpackets;

/**
 * Открывает окно аугмента, название от фонаря.
 */
public class ExShowVariationMakeWindow extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x51);
	}
}