package l2open.gameserver.serverpackets;

public class GameGuardQuery extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		getClient().setGameGuardOk(false);
		writeC(0x74);
		writeD(0x00); // ? - Меняется при каждом перезаходе.
		writeD(0x00); // ? - Меняется при каждом перезаходе.
		writeD(0x00); // ? - Меняется при каждом перезаходе.
		writeD(0x00); // ? - Меняется при каждом перезаходе.
	}
}