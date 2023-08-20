package l2open.gameserver.model;

import l2open.util.Location;

@SuppressWarnings("serial")
public class L2TeleportLocation extends Location
{
	public L2TeleportLocation(int locX, int locY, int locZ)
	{
		super(locX, locY, locZ);
	}

	private int _teleId;
	private int _price;

	/**
	 * @param id
	 */
	public void setTeleId(int id)
	{
		_teleId = id;
	}

	/**
	 * @param price
	 */
	public void setPrice(int price)
	{
		_price = price;
	}

	/**
	 * @return
	 */
	public int getTeleId()
	{
		return _teleId;
	}

	/**
	 * @return
	 */
	public int getPrice()
	{
		return _price;
	}
}