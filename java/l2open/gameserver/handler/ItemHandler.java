package l2open.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class manages handlers of items
 */
public class ItemHandler
{
	private static ItemHandler _instance;

	private Map<Integer, IItemHandler> _datatable;

	/**
	 * Create ItemHandler if doesn't exist and returns ItemHandler
	 * @return ItemHandler
	 */
	public static ItemHandler getInstance()
	{
		if(_instance == null)
			_instance = new ItemHandler();
		return _instance;
	}

	/**
	 * Returns the number of elements contained in datatable
	 * @return int : Size of the datatable
	 */
	public int size()
	{
		return _datatable.size();
	}

	/**
	 * Constructor<?> of ItemHandler
	 */
	private ItemHandler()
	{
		_datatable = new TreeMap<Integer, IItemHandler>();
	}

	/**
	 * Adds handler of item type in <I>datatable</I>.<BR><BR>
	 * <B><I>Concept :</I></U><BR>
	 * This handler is put in <I>datatable</I> Map &lt;Integer ; IItemHandler &gt; for each ID corresponding to an item type
	 * (existing in classes of package itemhandlers) sets as key of the Map.
	 * @param handler (IItemHandler)
	 */
	public void registerItemHandler(IItemHandler handler)
	{
		int[] ids = handler.getItemIds(); // Get all ID corresponding to the item type of the handler
		for(int element : ids)
			_datatable.put(element, handler); // Add handler for each ID found
	}

	/**
	 * Returns the handler of the item
	 * @param itemId : int designating the itemID
	 * @return IItemHandler
	 */
	public IItemHandler getItemHandler(int itemId)
	{
		return _datatable.get(itemId);
	}

	public void clear()
	{
		_datatable.clear();
	}
}
