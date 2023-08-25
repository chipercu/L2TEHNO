package l2open.gameserver.serverpackets;

public class ExPVPMatchCCRecord extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x89);
		// TODO dd[Sd]
	}
}