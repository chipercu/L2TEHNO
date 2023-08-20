package l2open.gameserver.serverpackets;

public class ExEventMatchObserver extends L2GameServerPacket
{
	private int game_id;
	private int action;
	private int unk3;
	private int x;
	private int y;
	private int z;
	private String team1_name;
	private String team2_name;

	// перед этим идет пакет 93 01 00 00 00
	public ExEventMatchObserver(int _game_id, int _action, int u3, String _team1_name, String _team2_name, int _x, int _y, int _z)
	{
		game_id = _game_id;
		action = _action; // 1 - вход в обсерв, 0 - выход из обсерва
		unk3 = u3;
		team1_name = _team1_name;
		team2_name = _team2_name;

		x = _x;
		y = _y;
		z = _z;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x0E);

		writeD(game_id);
		writeC(action);
		if(action > 0)
		{
			writeC(unk3);
			writeS(team1_name);
			writeS(team2_name);

			writeD(x);
			writeD(y);
			writeD(z);
		}
	}
}