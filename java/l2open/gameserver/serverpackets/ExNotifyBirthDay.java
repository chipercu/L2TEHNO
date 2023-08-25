package l2open.gameserver.serverpackets;

public class ExNotifyBirthDay extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x90 : 0x8F);
	}
}