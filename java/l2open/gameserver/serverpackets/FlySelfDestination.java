package l2open.gameserver.serverpackets;

public class FlySelfDestination extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x43);
		// TODO dddd
	}
}