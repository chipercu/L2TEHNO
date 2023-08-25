package l2open.gameserver.model.items.listeners;

import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.GArray;

/**
 * Recorder of alterations in inventory
 */
public final class ChangeRecorder implements PaperdollListener
{
	private final GArray<L2ItemInstance> _changed;

	/**
	 * Constructor<?> of the ChangeRecorder
	 * @param inventory inventory to watch
	 */
	public ChangeRecorder(Inventory inventory)
	{
		_changed = new GArray<L2ItemInstance>();
		inventory.addPaperdollListener(this);
	}

	/**
	 * Add alteration in inventory when item equipped
	 */
	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(!_changed.contains(item))
			_changed.add(item);
	}

	/**
	 * Add alteration in inventory when item unequipped
	 */
	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(!_changed.contains(item))
			_changed.add(item);
	}

	/**
	 * Returns alterations in inventory
	 * @return L2ItemInstance[] : array of alterated items
	 */
	public L2ItemInstance[] getChangedItems()
	{
		return _changed.toArray(new L2ItemInstance[_changed.size()]);
	}
}
