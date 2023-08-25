package l2open.gameserver.model.items;

import l2open.gameserver.model.L2Player;
import l2open.util.GArray;

import java.util.logging.Logger;

public class ClanWarehousePool
{
	private class ClanWarehouseWork
	{
		private L2Player activeChar;
		private int[] items;
		private long[] counts;
		public boolean complete;

		public ClanWarehouseWork(L2Player _activeChar, int[] _items, long[] _counts)
		{
			activeChar = _activeChar;
			items = _items;
			counts = _counts;
			complete = false;
		}

		public synchronized void RunWork()
		{
			Warehouse warehouse2 = null;
			warehouse2 = activeChar.getClan().getWarehouse();

			L2ItemInstance TransferItem;
			for(int i = 0; i < items.length; i++)
			{
				if(counts[i] < 0)
				{
					_log.warning("Warning char:" + activeChar.getName() + " get Item from ClanWarhouse count < 0: objid=" + items[i]);
					return;
				}
				if((TransferItem = warehouse2.takeItemByObj(items[i], counts[i])) == null)
				{
					_log.warning("Warning char:" + activeChar.getName() + " get null Item from ClanWarhouse: objid=" + items[i]);
					continue;
				}
				activeChar.getInventory().addItem(TransferItem);
			}

			activeChar.sendChanges();

			complete = true;
		}
	}

	static final Logger _log = Logger.getLogger(ClanWarehousePool.class.getName());

	private static ClanWarehousePool _instance;
	private GArray<ClanWarehouseWork> _works;
	private boolean inWork;

	public static ClanWarehousePool getInstance()
	{
		if(_instance == null)
			_instance = new ClanWarehousePool();
		return _instance;
	}

	public ClanWarehousePool()
	{
		_works = new GArray<ClanWarehouseWork>();
		inWork = false;
	}

	public void AddWork(L2Player _activeChar, int[] _items, long[] _counts)
	{
		ClanWarehouseWork cww = new ClanWarehouseWork(_activeChar, _items, _counts);
		_works.add(cww);
		RunWorks();
	}

	private void RunWorks()
	{
		if(inWork)
			return;

		inWork = true;
		try
		{
			if(_works.size() > 0)
			{
				ClanWarehouseWork cww = _works.get(0);
				if(!cww.complete)
					cww.RunWork();
				_works.remove(0);
			}
		}
		catch(Exception e)
		{
			_log.warning("Error ClanWarehousePool: " + e);
		}
		inWork = false;

		if(!_works.isEmpty())
			RunWorks();
	}
}