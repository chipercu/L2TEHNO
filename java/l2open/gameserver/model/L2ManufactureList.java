package l2open.gameserver.model;

import l2open.gameserver.model.base.Transaction;
import l2open.util.GArray;

/**
 * Контейнер для приватного магазина крафта.
 * 
 * @see Transaction
 * @see L2TradeList
 */
public class L2ManufactureList
{
	private GArray<L2ManufactureItem> _list;
	private boolean _confirmed;
	private String _manufactureStoreName;

	public L2ManufactureList()
	{
		_list = new GArray<L2ManufactureItem>();
		_confirmed = false;
	}

	public int size()
	{
		return _list.size();
	}

	public void setConfirmedTrade(boolean x)
	{
		_confirmed = x;
	}

	public boolean hasConfirmed()
	{
		return _confirmed;
	}

	/**
	 * @param manufactureStoreName The _manufactureStoreName to set.
	 */
	public void setStoreName(String manufactureStoreName)
	{
		_manufactureStoreName = manufactureStoreName;
	}

	/**
	 * @return Returns the _manufactureStoreName.
	 */
	public String getStoreName()
	{
		return _manufactureStoreName;
	}

	public void add(L2ManufactureItem item)
	{
		_list.add(item);
	}

	public GArray<L2ManufactureItem> getList()
	{
		return _list;
	}

	public void setList(GArray<L2ManufactureItem> list)
	{
		_list = list;
	}
}