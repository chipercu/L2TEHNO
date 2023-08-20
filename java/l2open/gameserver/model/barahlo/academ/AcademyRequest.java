package l2open.gameserver.model.barahlo.academ;

import l2open.gameserver.model.barahlo.academ.dao.AcademyRequestDAO;

public class AcademyRequest
{
	private int time;
	private int clanId;
	private int seats;
	private long price;
	private int item;

	public AcademyRequest(int t, int c, int s, long p, int i)
	{
		time = t;
		clanId = c;
		seats = s;
		price = p;
		item = i;
		AcademyStorage.getInstance().get().add(this);
		AcademyStorage.getInstance().updateList();
	}

	public int getTime()
	{
		return time;
	}

	public int getClanId()
	{
		return clanId;
	}

	public long getPrice()
	{
		return price;
	}

	public int getItem()
	{
		return item;
	}

	public int getSeats()
	{
		return seats;
	}

	public void updateSeats()
	{
		seats++;
		AcademyRequestDAO.getInstance().update(this);
	}

	public void reduceSeats()
	{
		seats--;
		AcademyRequestDAO.getInstance().update(this);
	}
}
