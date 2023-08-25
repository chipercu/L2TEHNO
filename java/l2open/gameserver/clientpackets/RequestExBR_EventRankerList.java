package l2open.gameserver.clientpackets;

import l2open.gameserver.serverpackets.ExBR_LoadEventTopRankers;

public class RequestExBR_EventRankerList extends L2GameClientPacket
{
	private int _eventId;
	private int _day;
	private int _ranking;

	/**
	 * format: ddd
	 */
	@Override
	protected void readImpl()
	{
		_eventId = readD();
		_day = readD(); // 0 - current, 1 - previous
		_ranking = readD();
	}
	
	@Override
	protected void runImpl()
	{
		// TODO count, bestScore, myScore
		int count = 0;
		int bestScore = 0;
		int myScore = 0;
		getClient().sendPacket(new ExBR_LoadEventTopRankers(_eventId, _day, count, bestScore, myScore));
	}
}