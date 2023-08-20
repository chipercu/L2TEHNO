package l2open.gameserver.serverpackets;

public class ExMpccRoomInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x9C : 0x9B);
		// TODO ...
	}
}