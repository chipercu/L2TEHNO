package l2open.gameserver.serverpackets;

/**
 * Даный пакет показывает менюшку для ввода серийника. Можно что-то придумать :)
 * Format: (ch)
 */
public class ShowPCCafeCouponShowUI extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x44);
	}
}