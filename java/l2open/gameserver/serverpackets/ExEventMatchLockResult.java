package l2open.gameserver.serverpackets;

public class ExEventMatchLockResult extends L2GameServerPacket
{
	private int game_id;
	private int team_id;

	public ExEventMatchLockResult(int _game_id, int _team_id)
	{
		game_id = _game_id;
		team_id = _team_id;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x0B);

		writeD(game_id);
		writeC(team_id);
	}
}