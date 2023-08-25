package l2open.gameserver.model.entity.Auction;

import java.util.Calendar;

public class Bidder
{
	private String _Name;
	private String _ClanName;
	private long _Bid;
	private Calendar _timeBid;

	public Bidder(String name, String clanName, long bid, long timeBid)
	{
		_Name = name;
		_ClanName = clanName;
		_Bid = bid;
		_timeBid = Calendar.getInstance();
		_timeBid.setTimeInMillis(timeBid);
	}

	public String getName()
	{
		return _Name;
	}

	public String getClanName()
	{
		return _ClanName;
	}

	public long getBid()
	{
		return _Bid;
	}

	public Calendar getTimeBid()
	{
		return _timeBid;
	}

	public void setTimeBid(long timeBid)
	{
		_timeBid.setTimeInMillis(timeBid);
	}

	public void setBid(long bid)
	{
		_Bid = bid;
	}

	@Override
	public String toString()
	{
		return _Name + ", clan: " + _ClanName + ", bid: " + _Bid;
	}
}