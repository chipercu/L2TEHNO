package l2open.gameserver.serverpackets;

public class ExSay2FailPacket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xE8);
		// just a trigger
	}
}
