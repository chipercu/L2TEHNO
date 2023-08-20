package l2open.gameserver.serverpackets;

public class ExEventMatchScore extends L2GameServerPacket
{
	private int game_id;
	private int team1_score;
	private int team2_score;

	public ExEventMatchScore(int _game_id, int _team1_score, int _team2_score)
	{
		game_id = _game_id;
		team1_score = _team1_score;
		team2_score = _team2_score;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x10);

		writeD(game_id);
		writeD(team1_score);
		writeD(team2_score);
	}
}