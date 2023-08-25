package l2open.gameserver.serverpackets;

public class ExDominionChannelSet extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x97 : 0x96);
		writeD(0); // unk
	}
}