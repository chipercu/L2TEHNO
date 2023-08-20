package l2open.gameserver.model.items;

import l2open.gameserver.model.items.listeners.PaperdollListener;

import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
public class PcInventoryDummy extends PcInventory
{
	public static final PcInventoryDummy instance = new PcInventoryDummy();
	static final L2ItemInstance[] noitems = new L2ItemInstance[0];
	static final ConcurrentLinkedQueue<L2ItemInstance> noitems_list = new ConcurrentLinkedQueue<L2ItemInstance>();

	public PcInventoryDummy()
	{
		super(null);
	}

	@Override
	public L2ItemInstance[] getItems()
	{
		return noitems;
	}

	@Override
	public ConcurrentLinkedQueue<L2ItemInstance> getItemsList()
	{
		synchronized (noitems_list)
		{
			return noitems_list;
		}
	}

	@Override
	public L2ItemInstance addAdena(long adena)
	{
		return null;
	}

	@Override
	public synchronized void deleteMe()
	{}

	@Override
	public void updateDatabase(boolean commit)
	{}

	@Override
	public L2ItemInstance destroyItem(int objectId, long count, boolean toLog)
	{
		return null;
	}

	@Override
	public L2ItemInstance destroyItem(L2ItemInstance item, long count, boolean toLog)
	{
		return null;
	}

	@Override
	public L2ItemInstance dropItem(int objectId, long count, boolean allowRemoveAttributes)
	{
		return null;
	}

	@Override
	public L2ItemInstance dropItem(L2ItemInstance item, long count, boolean allowRemoveAttributes)
	{
		return null;
	}

	@Override
	public void restore()
	{}

	@Override
	public L2ItemInstance destroyItemByItemId(int itemId, long count, boolean toLog)
	{
		return null;
	}

	@Override
	public boolean validateCapacity(int slots)
	{
		return false;
	}

	@Override
	public int slotsLeft()
	{
		return 0;
	}

	@Override
	public boolean validateWeight(long weight)
	{
		return false;
	}

	@Override
	public L2ItemInstance addItem(int id, long count)
	{
		return null;
	}

	@Override
	public L2ItemInstance getPaperdollItem(int slot)
	{
		return null;
	}

	@Override
	public int getPaperdollItemId(int slot, boolean is_visual_id, boolean user_info)
	{
		return 0;
	}

	@Override
	public int getPaperdollObjectId(int slot)
	{
		return 0;
	}

	@Override
	public synchronized void addPaperdollListener(PaperdollListener listener)
	{}

	@Override
	public synchronized void removePaperdollListener(PaperdollListener listener)
	{}

	@Override
	public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item)
	{
		return null;
	}

	@Override
	public void unEquipItemInBodySlotAndNotify(int slot, L2ItemInstance item, boolean sendMesseg)
	{}

	@Override
	public L2ItemInstance unEquipItemInSlot(int pdollSlot)
	{
		return null;
	}

	@Override
	public void unEquipItemInBodySlot(int slot, L2ItemInstance item)
	{}

	@Override
	public synchronized void equipItem(L2ItemInstance item, boolean checks)
	{}

	@Override
	public L2ItemInstance findEquippedLure()
	{
		return null;
	}

	@Override
	public void validateItems()
	{}

	@Override
	public void sort(int[][] order)
	{}
}