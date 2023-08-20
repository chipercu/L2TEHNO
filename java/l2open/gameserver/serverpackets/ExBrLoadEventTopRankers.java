package l2open.gameserver.serverpackets;

/**
 * Halloween rank list server packet.
 * Format: (ch)ddddd
 * by OSTIN
 */
public class ExBrLoadEventTopRankers extends L2GameServerPacket
{
	private int _eventId;
	private int _day;
	private int _count;
	private int _bestScore;
	private int _myScore;
	
	public ExBrLoadEventTopRankers(int eventId, int day, int count, int bestScore, int myScore)
	{
		_eventId = eventId;
		_day = day;
		_count = count;
		_bestScore = bestScore;
		_myScore = myScore;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0xBD);
		writeD(_eventId);
		writeD(_day);
		writeD(_count);
		writeD(_bestScore);
		writeD(_myScore);
		
	}

	@Override
	public String getType()
	{
		return "[S] FE:BD ExBrLoadEventTopRankers";
	}
}
