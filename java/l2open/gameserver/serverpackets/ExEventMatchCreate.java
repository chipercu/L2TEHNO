package l2open.gameserver.serverpackets;

public class ExEventMatchCreate extends L2GameServerPacket
{
	private int _game_id;

	public ExEventMatchCreate(int game_id)
	{
		_game_id = game_id;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x1D);
		writeD(_game_id);
	}
}