package l2open.gameserver.serverpackets;

public class ExNavitAdventPointInfoPacket extends L2GameServerPacket
{
	private int _points = 0;

	public ExNavitAdventPointInfoPacket(int points)
	{
		_points = points;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(getClient().isLindvior() ? 0xE3 : 0xDF);
		writeD(_points); // 72 = 1%
	}

	@Override
	public String getType()
	{
		return "[S] FE:DF ExNavitAdventPointInfoPacket";
	}
}