package l2open.gameserver.taskmanager;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.items.L2ItemInstance;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ItemsAutoDestroy
{
	private static ItemsAutoDestroy _instance;
	private ConcurrentLinkedQueue<L2ItemInstance> _items = null;
	private ConcurrentLinkedQueue<L2ItemInstance> _herbs = null;

	private ItemsAutoDestroy()
	{
		_herbs = new ConcurrentLinkedQueue<L2ItemInstance>();
		if(ConfigValue.AutoDestroyDroppedItemAfter > 0 || ConfigValue.AutoDestroyPlayerDroppedItemAfter > 0)
		{
			_items = new ConcurrentLinkedQueue<L2ItemInstance>();
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckItemsForDestroy(), 60000, 60000);
		}
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckHerbsForDestroy(), 1000, 1000);
	}

	public static ItemsAutoDestroy getInstance()
	{
		if(_instance == null)
			_instance = new ItemsAutoDestroy();
		return _instance;
	}

	public void addItem(L2ItemInstance item)
	{
		item.setDropTime(System.currentTimeMillis());
		if(ConfigValue.AutoDestroyDroppedItemAfter > 0)
			_items.add(item);
	}

	public void addItemPlayer(L2ItemInstance item)
	{
		item.setDropTime(System.currentTimeMillis());
		item.setDropPlayer(true);
		if(ConfigValue.AutoDestroyPlayerDroppedItemAfter > 0)
			_items.add(item);
	}

	public void addHerb(L2ItemInstance herb)
	{
		herb.setDropTime(System.currentTimeMillis());
		_herbs.add(herb);
	}

	public class CheckItemsForDestroy extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			try
			{
				long curtime = System.currentTimeMillis();
				for(L2ItemInstance item : _items)
					if(item == null || item.getDropTime() == 0 || item.getLocation() != L2ItemInstance.ItemLocation.VOID)
						_items.remove(item);
					else if(item.getDropTime() + (item.isDropPlayer() ? ConfigValue.AutoDestroyPlayerDroppedItemAfter * 1000L : ConfigValue.AutoDestroyDroppedItemAfter * 1000L) < curtime)
					{
						item.deleteMe();
						_items.remove(item);
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public class CheckHerbsForDestroy extends l2open.common.RunnableImpl
	{
		static final long _sleep = 60000;

		@Override
		public void runImpl()
		{
			try
			{
				long curtime = System.currentTimeMillis();
				for(L2ItemInstance item : _herbs)
					if(item == null || item.getDropTime() == 0 || item.getLocation() != L2ItemInstance.ItemLocation.VOID)
						_herbs.remove(item);
					else if(item.getDropTime() + _sleep < curtime)
					{
						item.deleteMe();
						_herbs.remove(item);
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}