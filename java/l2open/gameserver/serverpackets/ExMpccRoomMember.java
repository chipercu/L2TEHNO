package l2open.gameserver.serverpackets;

public class ExMpccRoomMember extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0xA0 : 0x9F);
		// TODO ...
	}
}