package l2open.gameserver.serverpackets;

public class ExNavitAdventTimeChange extends L2GameServerPacket
{
	private final int _acting;
	private final int _time;

	public ExNavitAdventTimeChange(int time, boolean acting)
	{
		_time = Math.min(240000, time);
		_acting = (acting ? 1 : 0);
	}

	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(getClient().isLindvior() ? 0xE5 : 0xE1);
		writeC(_acting);
		writeD(_time);
	}

	@Override
	public String getType()
	{
		return "[S] FE:E1 ExNavitAdventTimeChange";
	}
}