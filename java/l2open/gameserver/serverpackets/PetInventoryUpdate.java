package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.GArray;

public class PetInventoryUpdate extends L2GameServerPacket
{
	private GArray<L2ItemInstance> _items;

	public PetInventoryUpdate()
	{
		_items = new GArray<L2ItemInstance>();
	}

	public PetInventoryUpdate(GArray<L2ItemInstance> items)
	{
		_items = items;
	}

	public PetInventoryUpdate addNewItem(L2ItemInstance item)
	{
		item.setLastChange(L2ItemInstance.ADDED);
		_items.add(item);
		return this;
	}

	public PetInventoryUpdate addModifiedItem(L2ItemInstance item)
	{
		item.setLastChange(L2ItemInstance.MODIFIED);
		_items.add(item);
		return this;
	}

	public PetInventoryUpdate addRemovedItem(L2ItemInstance item)
	{
		item.setLastChange(L2ItemInstance.REMOVED);
		_items.add(item);
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb4);
		writeH(_items.size());
		for(L2ItemInstance temp : _items)
		{
			writeH(temp.getLastChange());
			writeItemInfo(temp);
		}
	}
}