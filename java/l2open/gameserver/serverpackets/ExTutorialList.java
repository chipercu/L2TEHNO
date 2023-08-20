package l2open.gameserver.serverpackets;

public class ExTutorialList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x6C : 0x6B);
		writeB(new byte[128]);
	}
}