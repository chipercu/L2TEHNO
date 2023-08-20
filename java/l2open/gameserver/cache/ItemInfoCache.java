package l2open.gameserver.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.ExRpItemLink.ItemInfo;

import java.util.concurrent.ConcurrentHashMap;

public class ItemInfoCache
{
	private static final ConcurrentHashMap<Integer, ItemInfo> _items = new ConcurrentHashMap<Integer, ItemInfo>();

	private static final ItemInfoCache _instance = new ItemInfoCache();
	private Cache cache;

	public static final ItemInfoCache getInstance()
	{
		return _instance;
	}

	private ItemInfoCache()
	{
		//cache = CacheManager.getInstance().getCache(getClass().getName());
	}

	public void put(L2ItemInstance item)
	{
		//cache.put(new Element(Integer.valueOf(item.getObjectId()), new ItemInfo(item)));
		_items.put(item.getObjectId(), new ItemInfo(item));
	}

	public ItemInfo get(int objectId)
	{
		ItemInfo info = _items.get(objectId);
		L2Player player = null;
		if(info != null)
		{
			player = L2ObjectsStorage.getPlayer(info.getOwnerId());

			L2ItemInstance item = null;

			if(player != null)
				item = player.getInventory().getItemByObjectId(objectId);

			if(item != null && item.getItemId() == info.getItemId())
				_items.put(item.getObjectId(), info = new ItemInfo(item));
		}
		/*Element element = cache.get(Integer.valueOf(objectId));

		
		if(element != null)
			info = (ItemInfo)element.getObjectValue();

		L2Player player = null;

		if(info != null)
		{
			player = L2ObjectsStorage.getPlayer(info.getOwnerId());

			L2ItemInstance item = null;

			if(player != null)
				item = player.getInventory().getItemByObjectId(objectId);

			if(item != null && item.getItemId() == info.getItemId())
				cache.put(new Element(Integer.valueOf(item.getObjectId()), info = new ItemInfo(item)));
		}*/
		return info;
	}
}