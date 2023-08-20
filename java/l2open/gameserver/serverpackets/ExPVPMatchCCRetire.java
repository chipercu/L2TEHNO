package l2open.gameserver.serverpackets;

public class ExPVPMatchCCRetire extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x8B);
		// just trigger
	}
}