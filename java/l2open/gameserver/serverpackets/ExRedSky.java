package l2open.gameserver.serverpackets;

public class ExRedSky extends L2GameServerPacket
{
	private int _duration;

	public ExRedSky(int duration)
	{
		_duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x41); // sub id
		writeD(_duration);
	}
}