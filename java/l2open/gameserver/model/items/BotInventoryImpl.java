package l2open.gameserver.model.items;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.BotImpl;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.templates.L2Armor;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2EtcItem.EtcItemType;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GCSArray;
import l2open.util.Log;

public class BotInventoryImpl extends PcInventory
{
	public BotInventoryImpl(BotImpl owner)
	{
		super(owner);
	}

	@Override
	public void restore()
	{}

	@Override
	public void updateDatabase(boolean commit)
	{}

	@Override
	public void updateDatabase(ConcurrentLinkedQueue<L2ItemInstance> items, boolean commit)
	{}

	@Override
	public void removeItemFromInventory(L2ItemInstance item, boolean clearCount, boolean AllowRemoveAttributes, boolean send_update)
	{}

	@Override
	public L2ItemInstance destroyItem(L2ItemInstance item, long count, boolean toLog)
	{
		return item;
	}

	@Override
	public void unEquipItemInBodySlotAndNotify(int slot, L2ItemInstance item, boolean sendMesseg)
	{
		L2Player cha = getOwner().getPlayer();
		if(cha == null)
			return;

		L2ItemInstance weapon = cha.getActiveWeaponInstance();

		L2ItemInstance[] unequipped = unEquipItemInBodySlotAndRecord(slot, item);
		if(unequipped == null || unequipped.length == 0)
			return;

		for(L2ItemInstance uneq : unequipped)
		{
			if(uneq == null || uneq.isWear())
				continue;

			if(weapon != null && uneq == weapon)
			{
				uneq.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
				uneq.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
				cha.abortAttack(true, true);
			}
		}

		if(item != null)
			cha.validateItemExpertisePenalties(false, item.getItem() instanceof L2Armor, item.getItem() instanceof L2Weapon);
		cha.broadcastUserInfo(true);
	}

	@Override
	public L2ItemInstance addAdena(long amount)
	{
		L2ItemInstance _adena = addItem(57, amount);
		return _adena;
	}

	@Override
	public L2ItemInstance dropItem(L2ItemInstance oldItem, long count, boolean AllowRemoveAttributes, boolean noLazy)
	{
		if(getOwner() == null)
			return null;

		if(getOwner().isPlayer() && ((L2Player) getOwner()).getPlayerAccess() != null && ((L2Player) getOwner()).getPlayerAccess().BlockInventory)
			return null;

		if(count <= 0)
			return null;

		if(oldItem == null)
			return null;

		Log.LogItem(getOwner(), null, Log.Drop, oldItem, count);

		if(oldItem.getCount() <= count || oldItem.getCount() <= 1)
		{

			removeItemFromInventory(oldItem, false, AllowRemoveAttributes, true);
			refreshWeight();
			return oldItem;
		}
		oldItem.setCount(oldItem.getCount() - count);
		sendModifyItem(oldItem);
		L2ItemInstance newItem = ItemTemplates.getInstance().createItem(oldItem.getItemId());
		newItem.setCount(count);
		refreshWeight();
		return newItem;
	}

	@Override
	public synchronized void deleteMe()
	{
		for(L2ItemInstance inst : getItemsList())
			inst.deleteMe();

		getItemsList().clear();
		getRefundItemsList().clear();
		if(_listenedItems != null)
			_listenedItems.clear(); // !!!???
		if(_paperdollListeners != null)
			_paperdollListeners.clear();
	}

	@Override
	public L2ItemInstance addItem(L2ItemInstance newItem, boolean dbUpdate, boolean log, boolean noLazy)
	{
		L2Character owner = getOwner();
		if(owner == null || newItem == null)
			return null;

		if(newItem.isHerb() && !owner.getPlayer().isGM())
			return null;

		if(newItem.getCount() < 0)
		{
			Thread.dumpStack();
			return null;
		}

		L2ItemInstance result = newItem;
		boolean stackableFound = false;

		// If stackable, search item in inventory in order to add to current quantity
		if(newItem.isStackable())
		{
			L2ItemInstance old = getItemByItemId(newItem.getItemId());
			if(old != null)
			{
				// add new item quantity to existing stack
				old.setCount(old.getCount() + newItem.getCount());

				newItem.setCount(0);
				newItem.setOwnerId(0);
				newItem.setLocation(ItemLocation.VOID);
				newItem.deleteMe();

				stackableFound = true;

				sendModifyItem(old);

				result = old;
			}
		}
		else if(ConfigValue.TalismanSumLife && newItem.getItem().isTalisman() && owner.getPlayer().getVarB("TalismanSumLife", false) && !newItem.isTemporalItem())
		{
			L2ItemInstance old = getItemByItemId(newItem.getItemId());
			if(old != null)
			{
				old.setLifeTimeRemaining(owner.getPlayer(), old.getLifeTimeRemaining() + newItem.getLifeTimeRemaining());

				newItem.setCount(0);
				newItem.setOwnerId(0);
				newItem.setLocation(ItemLocation.VOID);
				newItem.deleteMe();

				stackableFound = true;

				sendModifyItem(old);

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
				if(!newItem.isEquipable() && newItem.getItem() instanceof L2EtcItem && !newItem.isStackable() && newItem.getItem().getItemType() != EtcItemType.SCROLL && (newItem.getStatFuncs(false) != null || newItem.getItem().getAttachedSkills() != null))
				{
					if(_listenedItems == null)
						_listenedItems = new GCSArray<>();
					_listenedItems.add(newItem);
					refreshListenersEquipped(-1, newItem);
				}
			}

			if(newItem.getOwnerId() != owner.getPlayer().getObjectId() || dbUpdate)
			{
				newItem.setOwnerId(owner.getPlayer().getObjectId());
				newItem.setLocation(getBaseLocation(), findSlot(0), false);
				sendNewItem(newItem);
			}

			if(newItem.getItem().isQuest())
				_questSlots++;
			else
				_oldSlots++;
		}

		if(dbUpdate && result.isCursed() && owner.isPlayer())
			CursedWeaponsManager.getInstance().checkPlayer((L2Player) owner, result);

		refreshWeight();
		return result;
	}

	@Override
	public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item, boolean isUpdateStat, boolean is_remove)
	{
		L2ItemInstance old = null;
		try
		{
			old = _paperdoll[slot];
		}
		catch(Exception e)
		{
			_log.info("Error: setPaperdollItem: item=" + item.getItemId() + " slot=" + slot);
			e.printStackTrace();
		}
		if(old != item)
		{
			if(old != null)
			{
				_paperdoll[slot] = null;
				old.setLocation(getBaseLocation(), findSlot(0), !is_remove);
				if(isUpdateStat)
					sendModifyItem(old);
				long mask = 0;
				for(int i = 0; i < PAPERDOLL_MAX; i++)
				{
					L2ItemInstance pi = _paperdoll[i];
					if(pi != null)
						mask |= pi.getItem().getItemMask();
				}
				_wearedMask = mask;
				refreshListenersUnequipped(slot, old);
				old.shadowNotify(false);
			}
			if(item != null)
			{
				_paperdoll[slot] = item;
				item.setLocation(getEquipLocation(), slot);
				if(isUpdateStat)
					sendModifyItem(item);
				_wearedMask |= item.getItem().getItemMask();
				refreshListenersEquipped(slot, item);

				item.shadowNotify(true);
			}
		}

		return old;
	}

	@Override
	public BotImpl getOwner()
	{
		return (BotImpl) _owner;
	}
}
