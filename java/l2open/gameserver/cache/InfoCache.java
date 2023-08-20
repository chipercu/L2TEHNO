package l2open.gameserver.cache;

import javolution.util.FastMap;

public abstract class InfoCache
{
	private static final FastMap<Integer, String> _droplistCache = new FastMap<Integer, String>().setShared(true);

	public static void addToDroplistCache(final int id, final String list)
	{
		_droplistCache.put(id, list);
	}

	public static String getFromDroplistCache(final int id)
	{
		return _droplistCache.get(id);
	}

	public static void unload()
	{
		_droplistCache.clear();
	}
}