package l2open.gameserver.model.items;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.items.listeners.PaperdollListener;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2EtcItem.EtcItemType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.sql.ResultSet;
import java.util.logging.Level;

public class PetInventory extends Inventory
{
	private final L2PetInstance _owner;

	public PetInventory(L2PetInstance owner)
	{
		_owner = owner;
	}

	@Override
	public L2PetInstance getOwner()
	{
		return _owner;
	}

	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.PET;
	}

	@Override
	public ItemLocation getEquipLocation()
	{
		return ItemLocation.PET_PAPERDOLL;
	}

	@Override
	public L2ItemInstance addItem(int id, long count)
	{
		L2ItemInstance newItem = ItemTemplates.getInstance().createItem(id);
		newItem.setCount(count);
		return addItem(newItem);
	}

	@Override
	public L2ItemInstance addItem(L2ItemInstance newItem)
	{
		return addItem(newItem, true, true, false);
	}

	@Override
	public L2ItemInstance addItem(L2ItemInstance newItem, boolean dbUpdate, boolean log)
	{
		return addItem(newItem, dbUpdate, log, false);
	}

	@Override
	public L2ItemInstance addItem(L2ItemInstance newItem, boolean dbUpdate, boolean log, boolean noLazy)
	{
		L2Character owner = getOwner();
		if(owner == null || newItem == null)
			return null;

		if(newItem.isHerb() && !owner.getPlayer().isGM())
		{
			Util.handleIllegalPlayerAction(owner.getPlayer(), "tried to pickup herb into inventory", "Inventory[179]", 1);
			return null;
		}

		if(newItem.getCount() < 0)
		{
			_log.warning("AddItem: count < 0 owner:" + owner.getName());
			Thread.dumpStack();
			return null;
		}

		L2ItemInstance result = newItem;
		boolean stackableFound = false;

		Log.logItem(owner.getName() + "|ADDPET|" + result.getItemId() + "|" + result.getCount() + "|" + result.getObjectId(), "", "items-detail");
		if(owner.getPlayer().can_private_log)
			Log.addMy(owner.getName() + "|ADDPET|" + result.getItemId() + "|" + result.getCount() + "|" + result.getObjectId(), "items", owner.getPlayer().getName());

		// If stackable, search item in inventory in order to add to current quantity
		if(newItem.isStackable())
		{
			int itemId = newItem.getItemId();
			L2ItemInstance old = getItemByItemId(itemId);
			if(old != null)
			{
				// add new item quantity to existing stack
				old.setCount(old.getCount() + newItem.getCount());

				Log.logItem(owner.getName() + "|JOINPET|"+old.getCount()+"|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + result.getObjectId(), "", "items-detail");
				if(owner.getPlayer().can_private_log)
					Log.addMy(owner.getName() + "|JOINPET|"+old.getCount()+"|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + result.getObjectId(), "items", owner.getPlayer().getName());

				newItem.setCount(0);
				newItem.setOwnerId(0);
				newItem.setLocation(ItemLocation.VOID);
				PlayerData.getInstance().removeFromDb(newItem, true);
				newItem.deleteMe();

				stackableFound = true;

				sendModifyItem(old);

				// update old item in inventory and destroy new item
				old.updateDatabase(false, true, true);

				result = old;
			}
		}

		// If item hasn't be found in inventory
		if(!stackableFound)
		{
			// Add item in inventory
			if(getItemByObjectId(newItem.getObjectId()) == null)
			{
				getItemsList().add(newItem);
				if(!newItem.isEquipable() && newItem.getItem() instanceof L2EtcItem && !newItem.isStackable() && (newItem.getStatFuncs(false) != null || newItem.getItem().getAttachedSkills() != null))
				{
					if(_listenedItems == null)
						_listenedItems = new GCSArray<L2ItemInstance>();
					_listenedItems.add(newItem);
					for(PaperdollListener listener : _paperdollListeners)
						listener.notifyEquipped(-1, newItem, true);
					getOwner().updateStats();
				}
			}
			else
				Log.add("Inventory|" + owner.getName() + "|add double link to item in inventory list!|" + newItem.getItemId() + "|" + newItem.getObjectId(), "items");

			if(newItem.getOwnerId() != owner.getObjectId() || dbUpdate)
			{
				newItem.setOwnerId(owner.getObjectId());
				newItem.setLocation(getBaseLocation(), findSlot(0));
				sendNewItem(newItem);
			}

			if(newItem.getItem().isQuest())
				_questSlots++;
			else
				_oldSlots++;

			newItem.updateDatabase();
		}

		refreshWeight();
		return result;
	}
}