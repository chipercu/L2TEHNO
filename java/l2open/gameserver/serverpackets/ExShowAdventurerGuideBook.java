package l2open.gameserver.serverpackets;

/**
 * Format: ch (trigger)
 */
public class ExShowAdventurerGuideBook extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x38);
	}
}