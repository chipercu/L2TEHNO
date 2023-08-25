package l2open.gameserver.serverpackets;

public class ExDissmissMpccRoom extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x9E : 0x9D);
		// just trigger
	}
}