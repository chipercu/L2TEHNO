package l2open.gameserver.serverpackets;

public class ExManageMpccRoomMember extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x9F : 0x9E);
		// TODO ...
	}
}