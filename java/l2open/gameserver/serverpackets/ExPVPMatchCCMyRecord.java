package l2open.gameserver.serverpackets;

public class ExPVPMatchCCMyRecord extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x8A);
		writeD(0); // unk
	}
}