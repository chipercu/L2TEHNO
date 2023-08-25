package l2open.gameserver.serverpackets;

/**
 * Format: (ch)ddddd
 */
public class ExBR_LoadEventTopRankers extends L2GameServerPacket
{
	private int _eventId;
	private int _day;
	private int _count;
	private int _bestScore;
	private int _myScore;
	
	public ExBR_LoadEventTopRankers(int eventId, int day, int count, int bestScore, int myScore)
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
		writeH(getClient().isLindvior() ? 0xBE : 0xBD);
		writeD(_eventId);
		writeD(_day);
		writeD(_count);
		writeD(_bestScore);
		writeD(_myScore);
		
	}
}