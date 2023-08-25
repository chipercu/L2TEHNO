package l2open.gameserver.model.items;

import l2open.database.*;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Warehouse
{
	public static enum WarehouseType
	{
		PRIVATE(1),
		CLAN(2),
		CASTLE(3),
		FREIGHT(4);

		private final int _type;

		private WarehouseType(final int type)
		{
			_type = type;
		}

		public int getPacketValue()
		{
			return _type;
		}
	}

	private static final Logger _log = Logger.getLogger(Warehouse.class.getName());

	public abstract int getOwnerId();

	public abstract ItemLocation getLocationType();

	public L2ItemInstance[] listItems(ItemClass clss)
	{
		return PlayerData.getInstance().listItems(this, clss);
	}

	public int countItems()
	{
		return mysql.simple_get_int("COUNT(object_id)", "items", "owner_id=" + getOwnerId() + " AND loc=" + getLocationType().name());
	}

	public synchronized void addItem(int id, long count, String comment)
	{
		L2ItemInstance i = ItemTemplates.getInstance().createItem(id);
		i.setCount(count);
		addItem(i, comment);
	}

	public synchronized void addItem(L2ItemInstance newItem, String comment)
	{
		L2ItemInstance item;
		if(newItem.isStackable() && (item = findItemId(newItem.getItemId())) != null)
		{
			item.setCount(item.getCount() + newItem.getCount());
			item.updateDatabase(true, true);
			String log = getOwnerId() + "|add|" + item.getItemId() + "|" + item.getObjectId() + "|" + item.getCount() + (comment == null ? "" : "|" + comment);
			if(this instanceof ClanWarehouse)
				Log.add("ClanWarehouse|" + log, "items");
			else if(this instanceof PcWarehouse)
				Log.add("PcWarehouse|" + log, "items");
		}
		else
		{
			newItem.setOwnerId(getOwnerId());
			newItem.setLocation(getLocationType(), 0);
			newItem.updateDatabase(true, true);

			String log = getOwnerId() + "|add|" + newItem.getItemId() + "|" + newItem.getObjectId() + "|" + newItem.getCount() + (comment == null ? "" : "|" + comment);
			if(this instanceof ClanWarehouse)
				Log.add("ClanWarehouse|" + log, "items");
			else if(this instanceof PcWarehouse)
				Log.add("PcWarehouse|" + log, "items");
		}

		newItem.deleteMe();
	}

	/**
	 * Забирает вещь со склада 
	 * @param objectId
	 * @param count
	 * @return
	 */
	public synchronized L2ItemInstance takeItemByObj(int objectId, long count)
	{
		L2ItemInstance item = PlayerData.getInstance().restoreFromDb(objectId);

		if(item == null)
		{
			_log.fine("Warehouse.destroyItem: can't destroy objectId: " + objectId + ", count: " + count);
			return null;
		}

		if(item.getLocation() != ItemLocation.CLANWH && item.getLocation() != ItemLocation.WAREHOUSE && item.getLocation() != ItemLocation.FREIGHT)
		{
			item.deleteMe();
			_log.warning("WARNING get item not in WAREHOUSE via WAREHOUSE: item objid=" + item.getObjectId() + " ownerid=" + item.getOwnerId());
			return null;
		}

		if(item.getCount() <= count)
		{
			item.setLocation(ItemLocation.VOID, 0);
			item.updateDatabase(true, false);
			return item;
		}

		item.setCount(item.getCount() - count);
		item.updateDatabase(true, true);

		L2ItemInstance Newitem = ItemTemplates.getInstance().createItem(item.getItem().getItemId());
		Newitem.setCount(count);

		if(this instanceof ClanWarehouse)
			Log.add("ClanWarehouse|" + getOwnerId() + "|withdraw|" + item.getItemId() + "|" + item.getObjectId() + "|" + Newitem.getObjectId() + "|" + count, "items");
		else if(this instanceof PcWarehouse)
			Log.add("PcWarehouse|" + getOwnerId() + "|withdraw|" + item.getItemId() + "|" + item.getObjectId() + "|" + Newitem.getObjectId() + "|" + count, "items");

		return Newitem;
	}

	public synchronized void destroyItem(int itemId, long count)
	{
		L2ItemInstance item = findItemId(itemId);

		if(item == null)
		{
			_log.fine("Warehouse.destroyItem: can't destroy itemId: " + itemId + ", count: " + count);
			return;
		}

		if(item.getCount() < count)
			count = item.getCount();

		if(item.getCount() == count)
		{
			item.setCount(0);
			PlayerData.getInstance().removeFromDb(item, true);
		}
		else
		{
			item.setCount(item.getCount() - count);
			item.updateDatabase(true, true);
		}

		if(this instanceof ClanWarehouse)
			Log.add("ClanWarehouse|" + getOwnerId() + "|destroy item|" + item.getItemId() + "|" + item.getObjectId() + "|" + count + "|" + item.getCount(), "items");
		else if(this instanceof PcWarehouse)
			Log.add("PcWarehouse|" + getOwnerId() + "|destroy item|" + item.getItemId() + "|" + item.getObjectId() + "|" + count + "|" + item.getCount(), "items");
	}

	public L2ItemInstance findItemId(final int itemId)
	{
		return PlayerData.getInstance().findItemId(this, itemId);
	}

	public long countOf(final int itemId)
	{
		L2ItemInstance foundItem = findItemId(itemId);
		return foundItem == null ? 0 : foundItem.getCount();
	}

	public long getAdenaCount()
	{
		return countOf(57);
	}
}