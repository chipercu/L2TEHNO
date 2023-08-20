package l2open.gameserver.serverpackets;

public class CharacterCreateSuccess extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x0f);
		writeD(0x01);
	}
}