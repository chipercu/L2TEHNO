package l2open.gameserver.model.items;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.ClassType;
import l2open.gameserver.model.base.PlayerClass;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.items.listeners.ArmorSetListener;
import l2open.gameserver.model.items.listeners.BowListener;
import l2open.gameserver.model.items.listeners.BraceletListener;
import l2open.gameserver.model.items.listeners.ChangeRecorder;
import l2open.gameserver.model.items.listeners.ItemAugmentationListener;
import l2open.gameserver.model.items.listeners.ItemEnchantListener;
import l2open.gameserver.model.items.listeners.ItemSkillsListener;
import l2open.gameserver.model.items.listeners.PaperdollListener;
import l2open.gameserver.model.items.listeners.StatsListener;
import l2open.gameserver.serverpackets.ExBR_AgathionEnergyInfoPacket;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.serverpackets.PetInventoryUpdate;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Armor;
import l2open.gameserver.templates.L2Armor.ArmorType;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2EtcItem.EtcItemType;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Log;
import l2open.util.Util;

public abstract class Inventory
{
	protected static final Logger _log = Logger.getLogger(Inventory.class.getName());

	public static final byte PAPERDOLL_UNDER = 0;
	public static final byte PAPERDOLL_REAR = 1;
	public static final byte PAPERDOLL_LEAR = 2;
	public static final byte PAPERDOLL_NECK = 3;
	public static final byte PAPERDOLL_RFINGER = 4;
	public static final byte PAPERDOLL_LFINGER = 5;
	public static final byte PAPERDOLL_HEAD = 6;
	public static final byte PAPERDOLL_RHAND = 7;
	public static final byte PAPERDOLL_LHAND = 8;
	public static final byte PAPERDOLL_GLOVES = 9;
	public static final byte PAPERDOLL_CHEST = 10;
	public static final byte PAPERDOLL_LEGS = 11;
	public static final byte PAPERDOLL_FEET = 12;
	public static final byte PAPERDOLL_BACK = 13;
	public static final byte PAPERDOLL_LRHAND = 14;
	public static final byte PAPERDOLL_HAIR = 15;
	public static final byte PAPERDOLL_DHAIR = 16;
	public static final byte PAPERDOLL_RBRACELET = 17;
	public static final byte PAPERDOLL_LBRACELET = 18;
	public static final byte PAPERDOLL_DECO1 = 19;
	public static final byte PAPERDOLL_DECO2 = 20;
	public static final byte PAPERDOLL_DECO3 = 21;
	public static final byte PAPERDOLL_DECO4 = 22;
	public static final byte PAPERDOLL_DECO5 = 23;
	public static final byte PAPERDOLL_DECO6 = 24;
	public static final byte PAPERDOLL_BELT = 25;

	public static final byte PAPERDOLL_MAX = 26;

	protected final L2ItemInstance[] _paperdoll;

	protected final CopyOnWriteArrayList<PaperdollListener> _paperdollListeners;

	protected GCSArray<L2ItemInstance> _listenedItems;

	// protected to be accessed from child classes only
	protected final ConcurrentLinkedQueue<L2ItemInstance> _items;
	protected final ConcurrentLinkedQueue<L2ItemInstance> _refundItems;

	private int _totalWeight;

	private boolean _refreshingListeners;

	// used to quickly check for using of items of special type
	protected long _wearedMask;

	public int _questSlots = 0;
	public int _oldSlots = 0;

	// Castle circlets, WARNING: position == castle.id !
	public static final Integer[] _castleCirclets = { 0, // no castle - no circlet.. :)
			6838, // Circlet of Gludio
			6835, // Circlet of Dion
			6839, // Circlet of Giran
			6837, // Circlet of Oren
			6840, // Circlet of Aden
			6834, // Circlet of Innadril
			6836, // Circlet of Goddard
			8182, // Circlet of Rune
			8183, // Circlet of Schuttgart
	};

	protected Inventory()
	{
		_paperdoll = new L2ItemInstance[PAPERDOLL_MAX];
		_items = new ConcurrentLinkedQueue<L2ItemInstance>();
		_refundItems = new ConcurrentLinkedQueue<L2ItemInstance>();
		_paperdollListeners = new CopyOnWriteArrayList<PaperdollListener>();
		addPaperdollListener(new BraceletListener(this));
		addPaperdollListener(new BowListener(this));
		addPaperdollListener(new ArmorSetListener(this));
		addPaperdollListener(new StatsListener(this));
		addPaperdollListener(new ItemSkillsListener(this));
		addPaperdollListener(new ItemAugmentationListener(this));
		addPaperdollListener(new ItemEnchantListener(this));
	}

	public abstract L2Character getOwner();

	public abstract ItemLocation getBaseLocation();

	public abstract ItemLocation getEquipLocation();

	public int getOwnerId()
	{
		L2Character owner = getOwner();
		return owner == null ? 0 : owner.getObjectId();
	}

	public ChangeRecorder newRecorder()
	{
		return new ChangeRecorder(this);
	}

	public int getSize()
	{
		return _oldSlots;
	}

	public L2ItemInstance[] getItems()
	{
		return getItemsList().toArray(new L2ItemInstance[getItemsList().size()]);
	}

	public ConcurrentLinkedQueue<L2ItemInstance> getItemsList()
	{
		synchronized (_items)
		{
			return _items;
		}
	}

	public ConcurrentLinkedQueue<L2ItemInstance> getRefundItemsList()
	{
		synchronized (_refundItems)
		{
			return _refundItems;
		}
	}

	public L2ItemInstance addItem(int id, long count)
	{
		L2ItemInstance newItem = ItemTemplates.getInstance().createItem(id);
		newItem.setCount(count);
		return addItem(newItem);
	}

	public L2ItemInstance addItem(int id, long count, boolean dbUpdate, boolean log, boolean noLazy, boolean send_update)
	{
		L2ItemInstance newItem = ItemTemplates.getInstance().createItem(id);
		newItem.setCount(count);
		return addItem(newItem, dbUpdate, log, noLazy, send_update);
	}

	public L2ItemInstance addItem(L2ItemInstance newItem)
	{
		return addItem(newItem, true, true, false, true);
	}

	public L2ItemInstance addItem(L2ItemInstance newItem, boolean dbUpdate, boolean log)
	{
		return addItem(newItem, dbUpdate, log, false, true);
	}

	public L2ItemInstance addItem(L2ItemInstance newItem, boolean dbUpdate, boolean log, boolean noLazy)
	{
		return addItem(newItem, dbUpdate, log, noLazy, true);
	}

	public L2ItemInstance addItem(L2ItemInstance newItem, boolean dbUpdate, boolean log, boolean noLazy, boolean send_update)
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

		Log.logItem(owner.getName() + "|ADD|" + result.getItemId() + "|" + result.getCount() + "|" + result.getObjectId(), "", "items-detail");
		if(getOwner().getPlayer().can_private_log)
			Log.addMy(owner.getName() + "|ADD|" + result.getItemId() + "|" + result.getCount() + "|" + result.getObjectId(), "items", getOwner().getName());

		//if(log)
		//	Log.add("Inventory|" + owner.getName() + "|Get item|" + result.getItemId() + "|" + result.getCount() + "|" + result.getObjectId(), "items");

		// If stackable, search item in inventory in order to add to current quantity
		if(newItem.isStackable())
		{
			L2ItemInstance old = getItemByItemId(newItem.getItemId());
			if(old != null)
			{
				// add new item quantity to existing stack
				old.setCount(old.getCount() + newItem.getCount());

				Log.logItem(owner.getName() + "|JOIN|" + old.getCount() + "|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + result.getObjectId(), "", "items-detail");
				if(getOwner().getPlayer().can_private_log)
					Log.addMy(owner.getName() + "|JOIN|" + old.getCount() + "|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + result.getObjectId(), "items", getOwner().getName());
				// reset new item to null
				//if(log)
				//	Log.add("Inventory|" + owner.getName() + "|join item from-to|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + old.getObjectId(), "items");

				newItem.setCount(0);
				newItem.setOwnerId(0);
				newItem.setLocation(ItemLocation.VOID);
				PlayerData.getInstance().removeFromDb(newItem, true);
				newItem.deleteMe();

				stackableFound = true;

				if(send_update)
					sendModifyItem(old);
				else
					old.setLastChange(L2ItemInstance.MODIFIED);

				// update old item in inventory and destroy new item
				old.updateDatabase(false, true, noLazy);

				result = old;
			}
			if(result.getItemId() == 4356 && result.getCount() >= ConfigValue.Warning4356Count)
				Util.handleIllegalPlayerAction(owner.getPlayer(), "[4356] add="+newItem.getCount()+" all="+result.getCount(), "Inventory[264]", 0);
		}
		else if(ConfigValue.TalismanSumLife && newItem.getItem().isTalisman() && owner.getPlayer().getVarB("TalismanSumLife", false) && !newItem.isTemporalItem())
		{
			L2ItemInstance old = getItemByItemId(newItem.getItemId());
			if(old != null)
			{
				old.setLifeTimeRemaining(owner.getPlayer(), old.getLifeTimeRemaining()+newItem.getLifeTimeRemaining());

				Log.logItem(owner.getName() + "|JOIN_T|" + old.getCount() + "|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + result.getObjectId(), "", "items-detail");
				if(getOwner().getPlayer().can_private_log)
					Log.addMy(owner.getName() + "|JOIN_T|" + old.getCount() + "|" + result.getItemId() + "|" + newItem.getObjectId() + "|" + result.getObjectId(), "items", getOwner().getName());

				newItem.setCount(0);
				newItem.setOwnerId(0);
				newItem.setLocation(ItemLocation.VOID);
				PlayerData.getInstance().removeFromDb(newItem, true);
				newItem.deleteMe();

				stackableFound = true;

				if(send_update)
					sendModifyItem(old);
				else
					old.setLastChange(L2ItemInstance.MODIFIED);

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
				if(!newItem.isEquipable() && newItem.getItem() instanceof L2EtcItem && !newItem.isStackable() && newItem.getItem().getItemType() != EtcItemType.SCROLL && (newItem.getStatFuncs(false) != null || newItem.getItem().getAttachedSkills() != null))
				{
					if(_listenedItems == null)
						_listenedItems = new GCSArray<L2ItemInstance>();
					_listenedItems.add(newItem);
					refreshListenersEquipped(-1, newItem);
				}
			}
			else if(log)
				Log.add("Inventory|" + owner.getName() + "|add double link to item in inventory list!|" + newItem.getItemId() + "|" + newItem.getObjectId(), "items");

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

			// If database wanted to be updated, update item
			if(dbUpdate)
				newItem.updateDatabase();
		}

		if(dbUpdate && result.isCursed() && owner.isPlayer())
			CursedWeaponsManager.getInstance().checkPlayer((L2Player) owner, result);

		refreshWeight();
		return result;
	}

	public void restoreCursedWeapon()
	{
		L2Character owner = getOwner();
		if(owner == null || !owner.isPlayer())
			return;

		for(L2ItemInstance i : getItemsList())
			if(i.isCursed())
			{
				CursedWeaponsManager.getInstance().checkPlayer((L2Player) owner, i);
				_log.info("Restored CursedWeapon [" + i + "] for: " + owner);
				break;
			}
	}

	/**
	 * Находит и возвращает пустой слот в инвентаре. Вызывается с параметром 0, рекурсивно вызывает себя увеличивая номер слота пока не найдет свободный.
	 */
	public int findSlot(int slot)
	{
		for(L2ItemInstance i : _items)
		{
			if(i.isEquipped() || i.getItem().getType2() == L2Item.TYPE2_QUEST) // игнорируем надетое и квестовые вещи
				continue;
			if(i.getEquipSlot() == slot) // слот занят?
				return findSlot(++slot); // пробуем следующий
		}
		return slot; // слот не занят, возвращаем
	}

	public L2ItemInstance getPaperdollItem(int slot)
	{
		return _paperdoll[slot];
	}

	public L2ItemInstance[] getPaperdollItems()
	{
		return _paperdoll;
	}

	public int getPaperdollItemId(int slot, boolean is_visual_id, boolean user_info)
	{
		if(user_info && getOwner().isPlayer() && getOwner().getPlayer()._paperdoll_test != null && getOwner().getPlayer()._paperdoll_test[slot] > 0)
			return getOwner().getPlayer()._paperdoll_test[slot];
		else if(getOwner().isPlayer() && getOwner().getPlayer().getEventMaster() != null)
			return getOwner().getPlayer().getEventMaster().getPaperdollItemId(getOwner().getPlayer(), slot, is_visual_id);
		
		L2ItemInstance item = _paperdoll[slot];
		if(item != null)
			return is_visual_id && item.getVisualItemId() > 0 ? item.getVisualItemId() : item.getItemId();
		else if(slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_DHAIR];
			if(item != null)
				return is_visual_id && item.getVisualItemId() > 0 ? item.getVisualItemId() : item.getItemId();
		}
		else if(slot == PAPERDOLL_RHAND && getOwner().isPlayer())
		{
			L2Player player = getOwner().getPlayer();
			if(player.getVehicle() == null || !player.getVehicle().isAirShip())
				return 0;
			L2AirShip airship = (L2AirShip) player.getVehicle();
			if(airship.getDriver() == player)
				return 13556; // Затычка на отображение штурвала - Airship Helm
		}
		return 0;
	}

	public int getPaperdollObjectId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if(item != null)
			return item.getObjectId();
		else if(slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_DHAIR];
			if(item != null)
				return item.getObjectId();
		}
		return 0;
	}

	public synchronized void addPaperdollListener(PaperdollListener listener)
	{
		_paperdollListeners.add(listener);
	}

	public synchronized void removePaperdollListener(PaperdollListener listener)
	{
		_paperdollListeners.remove(listener);
	}

	public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item)
	{
		return setPaperdollItem(slot, item, true, false);
	}

	public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item, boolean isUpdateStat, boolean is_remove)
	{
		/*if(getOwner().isPlayer())
		{
			_log.info("setPaperdollItem: slot="+slot+" item="+item);
			Util.test();
		}*/
		L2ItemInstance old = null;
		try
		{
			old = _paperdoll[slot];
		}
		catch(Exception e)
		{
			_log.info("Error: setPaperdollItem: item="+item.getItemId()+" slot="+slot);
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
				old.updateDatabase();
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
				item.updateDatabase();
				refreshListenersEquipped(slot, item);

				item.shadowNotify(true);
			}
		}

		return old;
	}

	public long getWearedMask()
	{
		return _wearedMask;
	}

	public void unEquipItem(L2ItemInstance item)
	{
		if(item.isEquipped())
			unEquipItemInBodySlot(item.getBodyPart(), item);
	}

	/**
	 * Снимает предмет, и все зависимые от него, и возвращает отличия.
	 */
	public L2ItemInstance[] unEquipItemInBodySlotAndRecord(int slot, L2ItemInstance item)
	{
		ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInBodySlot(slot, item);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}

	/**
	 * Снимет вещь, записывает это в базу, шлет все нужные пакеты и перепроверяет пенальти
	 * @param slot L2Item.SLOT_
	 * @param item
	 */
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

			if(sendMesseg)
				cha.sendDisarmMessage(uneq);

			if(weapon != null && uneq == weapon)
			{
				uneq.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
				uneq.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
				cha.abortAttack(true, true);
			}

			if(uneq.getItem().getAgathionEnergy() > 0)
				getOwner().sendPacket(new ExBR_AgathionEnergyInfoPacket(1, uneq));
		}

		if(item != null)
			cha.validateItemExpertisePenalties(false, item.getItem() instanceof L2Armor, item.getItem() instanceof L2Weapon);
		cha.broadcastUserInfo(true);
	}

	public L2ItemInstance unEquipItemInSlot(int pdollSlot)
	{
		if(pdollSlot == PAPERDOLL_RBRACELET)
		{
			setPaperdollItem(PAPERDOLL_DECO1, null);
			setPaperdollItem(PAPERDOLL_DECO2, null);
			setPaperdollItem(PAPERDOLL_DECO3, null);
			setPaperdollItem(PAPERDOLL_DECO4, null);
			setPaperdollItem(PAPERDOLL_DECO5, null);
			setPaperdollItem(PAPERDOLL_DECO6, null);
		}
		return setPaperdollItem(pdollSlot, null);
	}

	/**
	 * Unequips item in slot (i.e. equips with default value)
	 *
	 * @param slot : int designating the slot
	 */
	public void unEquipItemInBodySlot(int slot, L2ItemInstance item)
	{
		unEquipItemInBodySlot(slot, item, true, false);
	}

	public void unEquipItemInBodySlot(int slot, L2ItemInstance item, boolean isUpdateStat)
	{
		unEquipItemInBodySlot(slot, item, isUpdateStat, false);
	}

	public void unEquipItemInBodySlot(int slot, L2ItemInstance item, boolean isUpdateStat, boolean is_remove)
	{
		byte pdollSlot = -1;
		switch(slot)
		{
			case L2Item.SLOT_NECK:
				pdollSlot = PAPERDOLL_NECK;
				break;
			case L2Item.SLOT_L_EAR:
				pdollSlot = PAPERDOLL_LEAR;
				break;
			case L2Item.SLOT_R_EAR:
				pdollSlot = PAPERDOLL_REAR;
				break;
			case L2Item.SLOT_L_FINGER:
				pdollSlot = PAPERDOLL_LFINGER;
				break;
			case L2Item.SLOT_R_FINGER:
				pdollSlot = PAPERDOLL_RFINGER;
				break;
			case L2Item.SLOT_HAIR:
				pdollSlot = PAPERDOLL_HAIR;
				break;
			case L2Item.SLOT_DHAIR:
				pdollSlot = PAPERDOLL_DHAIR;
				break;
			case L2Item.SLOT_HAIRALL:
				setPaperdollItem(PAPERDOLL_HAIR, null, isUpdateStat, is_remove);
				setPaperdollItem(PAPERDOLL_DHAIR, null, isUpdateStat, is_remove); // This should be the same as in DHAIR
				pdollSlot = PAPERDOLL_HAIR;
				break;
			case L2Item.SLOT_HEAD:
				pdollSlot = PAPERDOLL_HEAD;
				break;
			case L2Item.SLOT_R_HAND:
				pdollSlot = PAPERDOLL_RHAND;
				break;
			case L2Item.SLOT_L_HAND:
				pdollSlot = PAPERDOLL_LHAND;
				break;
			case L2Item.SLOT_GLOVES:
				pdollSlot = PAPERDOLL_GLOVES;
				break;
			case L2Item.SLOT_LEGS:
				pdollSlot = PAPERDOLL_LEGS;
				break;
			case L2Item.SLOT_CHEST:
			case L2Item.SLOT_FULL_ARMOR:
			case L2Item.SLOT_FORMAL_WEAR:
				pdollSlot = PAPERDOLL_CHEST;
				break;
			case L2Item.SLOT_BACK:
				pdollSlot = PAPERDOLL_BACK;
				break;
			case L2Item.SLOT_FEET:
				pdollSlot = PAPERDOLL_FEET;
				break;
			case L2Item.SLOT_UNDERWEAR:
				pdollSlot = PAPERDOLL_UNDER;
				break;
			case L2Item.SLOT_BELT:
				pdollSlot = PAPERDOLL_BELT;
				break;
			case L2Item.SLOT_LR_HAND:
				setPaperdollItem(PAPERDOLL_LHAND, null, isUpdateStat, is_remove);
				setPaperdollItem(PAPERDOLL_RHAND, null, isUpdateStat, is_remove); // this should be the same as in LRHAND
				pdollSlot = PAPERDOLL_RHAND;
				break;
			case L2Item.SLOT_L_BRACELET:
				pdollSlot = PAPERDOLL_LBRACELET;
				break;
			case L2Item.SLOT_R_BRACELET:
				pdollSlot = PAPERDOLL_RBRACELET;

				// При снятии правого браслета, снимаем и талисманы тоже
				setPaperdollItem(PAPERDOLL_DECO1, null);
				setPaperdollItem(PAPERDOLL_DECO2, null);
				setPaperdollItem(PAPERDOLL_DECO3, null);
				setPaperdollItem(PAPERDOLL_DECO4, null);
				setPaperdollItem(PAPERDOLL_DECO5, null);
				setPaperdollItem(PAPERDOLL_DECO6, null);
				break;
			case L2Item.SLOT_DECO:
				if(item == null)
					return;
				else if(getPaperdollObjectId(PAPERDOLL_DECO1) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO1;
				else if(getPaperdollObjectId(PAPERDOLL_DECO2) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO2;
				else if(getPaperdollObjectId(PAPERDOLL_DECO3) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO3;
				else if(getPaperdollObjectId(PAPERDOLL_DECO4) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO4;
				else if(getPaperdollObjectId(PAPERDOLL_DECO5) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO5;
				else if(getPaperdollObjectId(PAPERDOLL_DECO6) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO6;
				break;
			default:
				String name = getOwner() == null ? "null" : getOwner().getPlayer().getName();
				_log.warning("Requested invalid body slot: " + slot + ", Item: " + item + ", owner: '" + name + "'");
				Thread.dumpStack();
		}
		if(item != null && (slot == L2Item.SLOT_LR_HAND || slot == L2Item.SLOT_R_HAND) && getOwner().isPlayer() && item.getItem().addKarma() > 0)
				((L2Player) getOwner()).decreaseKarma(item.getItem().addKarma());
		if(pdollSlot >= 0)
			setPaperdollItem(pdollSlot, null, isUpdateStat, is_remove);
	}

	/**
	 * Одевает предмет
	 * @param item
	 * @param checks false при восстановлении инвентаря
	 */
	public synchronized void equipItem(L2ItemInstance item, boolean checks)
	{
		int targetSlot = item.getBodyPart();

		/*if(getOwner().isPlayer())
		{
			_log.info("equipItem: checks="+checks+" item="+item);
			Util.test();
		}*/
		if(getOwnerId() != item.getOwnerId() || (item.getLocation() != ItemLocation.INVENTORY && item.getLocation() != ItemLocation.PAPERDOLL && item.getLocation() != ItemLocation.PET && item.getLocation() != ItemLocation.PET_PAPERDOLL))
			return;

		if(checks)
		{
			L2Character owner = getOwner();
			if(owner.isPlayer() && owner.getName() != null)
			{
				SystemMessage msg = checkConditions(item);
				if(msg != null)
				{
					owner.sendPacket(msg);
					return;
				}
			}
		}

		double hp = getOwner().getCurrentHp();
		double mp = getOwner().getCurrentMp();
		double cp = getOwner().getCurrentCp();

		switch(targetSlot)
		{
			case L2Item.SLOT_LR_HAND:
			{
				setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, item);
				break;
			}

			case L2Item.SLOT_L_HAND:
			{
				L2ItemInstance slot = getPaperdollItem(PAPERDOLL_RHAND);

				L2Item oldItem = slot == null ? null : slot.getItem();
				L2Item newItem = item.getItem();

				if(oldItem != null && newItem.getItemType() == EtcItemType.ARROW && oldItem.getItemType() == WeaponType.BOW && oldItem.getCrystalType() != newItem.getCrystalType())
					return;
				if(oldItem != null && newItem.getItemType() == EtcItemType.BOLT && oldItem.getItemType() == WeaponType.CROSSBOW && oldItem.getCrystalType() != newItem.getCrystalType())
					return;

				if(newItem.getItemType() != EtcItemType.ARROW && newItem.getItemType() != EtcItemType.BOLT && newItem.getItemType() != EtcItemType.BAIT)
				{
					if(oldItem != null && oldItem.getBodyPart() == L2Item.SLOT_LR_HAND)
					{
						setPaperdollItem(PAPERDOLL_RHAND, null);
						setPaperdollItem(PAPERDOLL_LHAND, null);
					}
					else
						setPaperdollItem(PAPERDOLL_LHAND, null);
					setPaperdollItem(PAPERDOLL_LHAND, item);
				}
				else if(oldItem != null && (newItem.getItemType() == EtcItemType.ARROW && oldItem.getItemType() == WeaponType.BOW || newItem.getItemType() == EtcItemType.BOLT && oldItem.getItemType() == WeaponType.CROSSBOW || newItem.getItemType() == EtcItemType.BAIT && oldItem.getItemType() == WeaponType.ROD))
				{
					setPaperdollItem(PAPERDOLL_LHAND, item);
					if(newItem.getItemType() == EtcItemType.BAIT && getOwner().isPlayer())
					{
						L2Player owner = (L2Player) getOwner();
						owner.setVar("LastLure", String.valueOf(item.getObjectId()));
					}
				}
				break;
			}

			case L2Item.SLOT_R_HAND:
			{
				setPaperdollItem(PAPERDOLL_RHAND, item);
				break;
			}
			case L2Item.SLOT_L_EAR:
			case L2Item.SLOT_R_EAR:
			case L2Item.SLOT_L_EAR | L2Item.SLOT_R_EAR:
			{
				if(_paperdoll[PAPERDOLL_REAR] == null)
				{
					item.setBodyPart(L2Item.SLOT_R_EAR);
					setPaperdollItem(PAPERDOLL_REAR, item);
				}
				else if(_paperdoll[PAPERDOLL_LEAR] == null)
				{
					item.setBodyPart(L2Item.SLOT_L_EAR);
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
				else
				{
					item.setBodyPart(L2Item.SLOT_R_EAR);
					setPaperdollItem(PAPERDOLL_REAR, null);
					setPaperdollItem(PAPERDOLL_REAR, item);
				}
				break;
			}
			case L2Item.SLOT_L_FINGER:
			case L2Item.SLOT_R_FINGER:
			case L2Item.SLOT_L_FINGER | L2Item.SLOT_R_FINGER:
			{
				if(_paperdoll[PAPERDOLL_RFINGER] == null)
				{
					item.setBodyPart(L2Item.SLOT_R_FINGER);
					setPaperdollItem(PAPERDOLL_RFINGER, item);
				}
				else if(_paperdoll[PAPERDOLL_LFINGER] == null)
				{
					item.setBodyPart(L2Item.SLOT_L_FINGER);
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
				else
				{
					item.setBodyPart(L2Item.SLOT_R_FINGER);
					setPaperdollItem(PAPERDOLL_RFINGER, null);
					setPaperdollItem(PAPERDOLL_RFINGER, item);
				}
				break;
			}
			case L2Item.SLOT_NECK:
				setPaperdollItem(PAPERDOLL_NECK, item);
				break;
			case L2Item.SLOT_FULL_ARMOR:
				setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			case L2Item.SLOT_CHEST:
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			case L2Item.SLOT_LEGS:
			{
				// handle full armor
				L2ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
				if(chest != null && chest.getBodyPart() == L2Item.SLOT_FULL_ARMOR)
					setPaperdollItem(PAPERDOLL_CHEST, null);

				if(isWearEquipped())
					setPaperdollItem(PAPERDOLL_CHEST, null);

				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_LEGS, item);
				break;
			}
			case L2Item.SLOT_FEET:
				if(isWearEquipped())
					setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_FEET, item);
				break;
			case L2Item.SLOT_GLOVES:
				if(isWearEquipped())
					setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_GLOVES, item);
				break;
			case L2Item.SLOT_HEAD:
				if(isWearEquipped())
					setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_HEAD, item);
				break;
			case L2Item.SLOT_HAIR:
				L2ItemInstance slot = getPaperdollItem(PAPERDOLL_DHAIR);
				if(slot != null && slot.getBodyPart() == L2Item.SLOT_HAIRALL)
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_DHAIR, null);
				}
				setPaperdollItem(PAPERDOLL_HAIR, item);
				break;
			case L2Item.SLOT_DHAIR:
				L2ItemInstance slot2 = getPaperdollItem(PAPERDOLL_DHAIR);
				if(slot2 != null && slot2.getBodyPart() == L2Item.SLOT_HAIRALL)
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_DHAIR, null);
				}
				setPaperdollItem(PAPERDOLL_DHAIR, item);
				break;
			case L2Item.SLOT_HAIRALL:
				setPaperdollItem(PAPERDOLL_HAIR, null);
				setPaperdollItem(PAPERDOLL_DHAIR, null);
				setPaperdollItem(PAPERDOLL_DHAIR, item);
				break;
			case L2Item.SLOT_R_BRACELET:
				int max1 = (int) getOwner().calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
				setPaperdollItem(PAPERDOLL_RBRACELET, null);
				setPaperdollItem(PAPERDOLL_RBRACELET, item);
				int max2 = (int) getOwner().calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
				if(max1 > max2)
				{
					setPaperdollItem(PAPERDOLL_DECO1, null);
					setPaperdollItem(PAPERDOLL_DECO2, null);
					setPaperdollItem(PAPERDOLL_DECO3, null);
					setPaperdollItem(PAPERDOLL_DECO4, null);
					setPaperdollItem(PAPERDOLL_DECO5, null);
					setPaperdollItem(PAPERDOLL_DECO6, null);
				}
				break;
			case L2Item.SLOT_L_BRACELET:
				setPaperdollItem(PAPERDOLL_LBRACELET, null);
				setPaperdollItem(PAPERDOLL_LBRACELET, item);
				if(item.getItem().getAgathionEnergy() > 0)
					getOwner().sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item));
				break;
			case L2Item.SLOT_UNDERWEAR:
				setPaperdollItem(PAPERDOLL_UNDER, item);
				break;
			case L2Item.SLOT_BACK:
				setPaperdollItem(PAPERDOLL_BACK, item);
				break;
			case L2Item.SLOT_BELT:
				setPaperdollItem(PAPERDOLL_BELT, item);
				break;
			case L2Item.SLOT_DECO:
				if(_paperdoll[PAPERDOLL_DECO1] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO1, item);
					break;
				}
				if(_paperdoll[PAPERDOLL_DECO2] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO2, item);
					break;
				}
				if(_paperdoll[PAPERDOLL_DECO3] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO3, item);
					break;
				}
				if(_paperdoll[PAPERDOLL_DECO4] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO4, item);
					break;
				}
				if(_paperdoll[PAPERDOLL_DECO5] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO5, item);
					break;
				}
				if(_paperdoll[PAPERDOLL_DECO6] == null)
					setPaperdollItem(PAPERDOLL_DECO6, item);
				else
					setPaperdollItem(PAPERDOLL_DECO1, item);
				break;

			case L2Item.SLOT_FORMAL_WEAR:
				// При одевании свадебного платья руки не трогаем
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_HEAD, null);
				setPaperdollItem(PAPERDOLL_FEET, null);
				setPaperdollItem(PAPERDOLL_GLOVES, null);
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			default:
				_log.warning("unknown body slot:" + targetSlot + " for item id: " + item.getItemId());
		}

		getOwner().setCurrentHp(hp, false);
		getOwner().setCurrentMp(mp);
		getOwner().setCurrentCp(cp);
		if(getOwner().isPlayer())
		{
			if((targetSlot == L2Item.SLOT_LR_HAND || targetSlot == L2Item.SLOT_R_HAND))
			{
				if(item.getItem().addKarma() > 0)
					((L2Player) getOwner()).increaseKarma(item.getItem().addKarma());
				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
				{
					public void runImpl()
					{
						((L2Player) getOwner()).AutoShot();
					}
				}, 500);
			}
		}
	}

	public L2ItemInstance getItemByItemId(int itemId)
	{
		for(L2ItemInstance temp : getItemsList())
			if(temp.getItemId() == itemId)
				return temp;
		return null;
	}

	public L2ItemInstance getItemByItemInfo(int itemId, int enchant, int att_type, int att_value, int att_type_fa, int att_type_wa, int att_type_wi, int att_type_ea, int att_type_ho, int att_type_un)
	{
		for(L2ItemInstance temp : getItemsList())
			if(temp.getItemId() == itemId && temp.getRealEnchantLevel() == enchant && temp.getAttackElement() == att_type && temp.getAttackElementValue() == att_value && temp.getDefenceFire() == att_type_fa && temp.getDefenceWater() == att_type_wa && temp.getDefenceWind() == att_type_wi && temp.getDefenceEarth() == att_type_ea && temp.getDefenceHoly() == att_type_ho && temp.getDefenceUnholy() == att_type_un)
				return temp;
		return null;
	}

	public L2ItemInstance[] getAllItemsById(int itemId)
	{
		GArray<L2ItemInstance> ar = new GArray<L2ItemInstance>();
		for(L2ItemInstance i : getItemsList())
			if(i.getItemId() == itemId)
				ar.add(i);
		return ar.toArray(new L2ItemInstance[ar.size()]);
	}

	public int getPaperdollAugmentationId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if(item != null && item.getAugmentation() != null && item.getVisualItemId() <= 0)
			return item.getAugmentation().getAugmentationId();
		return 0;
	}

	public L2ItemInstance getItemByObjectId(Integer objectId)
	{
		for(L2ItemInstance temp : getItemsList())
			if(temp.getObjectId() == objectId)
				return temp;
		return null;
	}

	public L2ItemInstance destroyItem(int objectId, long count, boolean toLog)
	{
		L2ItemInstance item = getItemByObjectId(objectId);
		return destroyItem(item, count, toLog);
	}

	/**
	 * Destroy item from inventory and updates database
	 */
	public L2ItemInstance destroyItem(L2ItemInstance item, long count, boolean toLog)
	{
		return destroyItem(item, count, toLog, true);
	}

	public L2ItemInstance destroyItem(L2ItemInstance item, long count, boolean toLog, boolean send_update)
	{
		if(getOwner() == null || item == null)
			return null;

		if(count < 0)
		{
			_log.warning("DestroyItem(" + item.getItemId() + "): count < 0 owner:" + getOwner().getName());
			Thread.dumpStack();
			return null;
		}

		if(toLog)
		{
			Log.LogItem(getOwner(), null, Log.DeleteItem, item, count);
			Log.logItem(getOwner().getName() + "|DESTROYS|" + item.getItemId() + "|" + count + "|" + item.getObjectId(), "", "items-detail");
			if(getOwner().getPlayer().can_private_log)
				Log.addMy(getOwner().getName() + "|DESTROYS|" + item.getItemId() + "|" + count + "|" + item.getObjectId(), "items", getOwner().getName());
			//Log.logItem(getOwner().getName() + "|Destroys item|" + item.getItemId() + "|" + count + "|" + item.getObjectId(), "items");
		}

		if(item.getCount() <= count)
		{
			if(item.getCount() < count && toLog)
			{
				Log.logItem(getOwner().getName() + "|!DESTROYS|" + item.getItemId() + "|" + count + " but item count " + item.getCount() + "|" + item.getObjectId(), "", "items-detail");
				if(getOwner().getPlayer().can_private_log)
					Log.addMy(getOwner().getName() + "|!DESTROYS|" + item.getItemId() + "|" + count + " but item count " + item.getCount() + "|" + item.getObjectId(), "items", getOwner().getName());
			}
			removeItemFromInventory(item, true, true, send_update);
			// При удалении ошейника, удалить пета
			if(PetDataTable.isPetControlItem(item))
				PetDataTable.deletePet(item, getOwner());
		}
		else
		{
			item.setCount(item.getCount() - count);
			if(send_update)
				sendModifyItem(item);
			else
				item.setLastChange(L2ItemInstance.MODIFIED);
			item.updateDatabase();
		}

		refreshWeight();

		return item;
	}

	protected void sendModifyItem(L2ItemInstance item)
	{
		if(item.getItem().getAgathionEnergy() > 0)
			getOwner().sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item));

		if(getOwner().isPet())
			getOwner().getPlayer().sendPacket(new PetInventoryUpdate().addModifiedItem(item));
		else
			getOwner().sendPacket(new InventoryUpdate().addModifiedItem(item));
	}

	protected void sendRemoveItem(L2ItemInstance item)
	{
		try
		{
			if(getOwner().isPet())
				getOwner().getPlayer().sendPacket(new PetInventoryUpdate().addRemovedItem(item));
			else
				getOwner().sendPacket(new InventoryUpdate().addRemovedItem(item));
		}
		catch(Exception e)
		{}
	}

	protected void sendNewItem(L2ItemInstance item)
	{
		if(item.getItem().getAgathionEnergy() > 0)
			getOwner().sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item));

		if(getOwner().isPet())
			getOwner().getPlayer().sendPacket(new PetInventoryUpdate().addNewItem(item));
		else
			getOwner().sendPacket(new InventoryUpdate().addNewItem(item));
	}

	// we need this one cuz warehouses send itemId only
	/**
	 * Destroy item from inventory by using its <B>itemID</B> and updates
	 * database
	 *
	 * @param itemId : int pointing out the itemID of the item
	 * @param count : long designating the quantity of item to destroy
	 * @return L2ItemInstance designating the item up-to-date
	 */
	public L2ItemInstance destroyItemByItemId(int itemId, long count, boolean toLog)
	{
		L2ItemInstance item = getItemByItemId(itemId);
		L2Player player = (L2Player) getOwner();
		if(!(itemId == 57 && player.isGM()))
			Log.LogItem(getOwner(), Log.Sys_DeleteItem, item, count);
		return destroyItem(item, count, toLog);
	}

	public L2ItemInstance destroyItemByItemId(int itemId, long count, boolean toLog, boolean send_update)
	{
		L2ItemInstance item = getItemByItemId(itemId);
		L2Player player = (L2Player) getOwner();
		if(!(itemId == 57 && player.isGM()))
			Log.LogItem(getOwner(), Log.Sys_DeleteItem, item, count);
		return destroyItem(item, count, toLog, send_update);
	}

	/**
	 * Destroy item from inventory and from database.
	 *
	 * @param item : L2ItemInstance designating the item to remove from inventory
	 * @param clearCount : boolean : if true, set the item quantity to 0
	 */
	public void removeItemFromInventory(L2ItemInstance item, boolean clearCount, boolean AllowRemoveAttributes, boolean send_update)
	{
		if(getOwner() == null)
			return;

		if(getOwner().isPlayer())
		{
			L2Player player = (L2Player) getOwner();
			player.removeItemFromShortCut(item.getObjectId());
			if(item.isEquipped())
				unEquipItemInBodySlot(item.getBodyPart(), item, true, true);
		}

		getItemsList().remove(item);
		item.shadowNotify(false);
		if(!item.isEquipable() && item.getItem() instanceof L2EtcItem && !item.isStackable() && item.getItem().getItemType() != EtcItemType.SCROLL && (item.getStatFuncs(false) != null || item.getItem().getAttachedSkills() != null))
		{
			if(_listenedItems != null)
			{
				_listenedItems.remove(item);
				if(_listenedItems.isEmpty())
					_listenedItems = null;
			}
			refreshListenersUnequipped(-1, item);
		}
		if(clearCount)
			item.setCount(0);

		if(item.getItem().isQuest())
		{
			_questSlots--;
			if(_questSlots < 0)
				_questSlots = 0;
		}
		else
		{
			_oldSlots--;
			if(_oldSlots < 0)
				_oldSlots = 0;
		}

		item.setOwnerId(0);
		item.setLocation(ItemLocation.VOID);
		if(send_update)
			sendRemoveItem(item);
		else
			item.setLastChange(L2ItemInstance.REMOVED);
		item.updateDatabase(true, AllowRemoveAttributes);
		item.deleteMe();
	}

	public L2ItemInstance dropItem(int objectId, long count, boolean allowRemoveAttributes)
	{
		L2ItemInstance item = getItemByObjectId(objectId);
		if(item == null)
		{
			_log.warning("DropItem: item objectId: " + objectId + " does not exist in inventory");
			//Thread.dumpStack();
			return null;
		}
		return dropItem(item, count, allowRemoveAttributes, false);
	}

	public L2ItemInstance dropItem(int objectId, long count, boolean allowRemoveAttributes, boolean noLazy)
	{
		L2ItemInstance item = getItemByObjectId(objectId);
		if(item == null)
		{
			_log.warning("DropItem: item objectId: " + objectId + " does not exist in inventory");
			//Thread.dumpStack();
			return null;
		}
		return dropItem(item, count, allowRemoveAttributes, noLazy);
	}

	/**
	 * Drop item from inventory by using <B>object L2ItemInstance</B><BR>
	 * <BR>
	 * <U><I>Concept :</I></U><BR>
	 * item equipped are unequipped
	 * <LI>If quantity of items in inventory after drop is negative or null,
	 * change location of item</LI>
	 * <LI>Otherwise, change quantity in inventory and create new object with
	 * quantity dropped</LI>
	 *
	 * @param oldItem : L2ItemInstance designating the item to drop
	 * @param count : int designating the quantity of item to drop
	 * @return L2ItemInstance designating the item dropped
	 */
	public L2ItemInstance dropItem(L2ItemInstance oldItem, long count, boolean AllowRemoveAttributes)
	{
		return dropItem(oldItem, count, AllowRemoveAttributes, false);
	}

	public L2ItemInstance dropItem(L2ItemInstance oldItem, long count, boolean AllowRemoveAttributes, boolean noLazy)
	{
		return dropItem(oldItem, count, AllowRemoveAttributes, noLazy, true);
	}

	public L2ItemInstance dropItem(L2ItemInstance oldItem, long count, boolean AllowRemoveAttributes, boolean noLazy, boolean send_update)
	{
		if(getOwner() == null)
			return null;

		if(getOwner().isPlayer() && ((L2Player) getOwner()).getPlayerAccess() != null && ((L2Player) getOwner()).getPlayerAccess().BlockInventory)
			return null;

		if(count <= 0)
		{
			_log.warning("DropItem: count <= 0 owner:" + getOwner().getName());
			return null;
		}

		if(oldItem == null)
		{
			_log.warning("DropItem: item id does not exist in inventory");
			return null;
		}

		Log.LogItem(getOwner(), null, Log.Drop, oldItem, count);

		if(oldItem.getCount() <= count || oldItem.getCount() <= 1)
		{
			Log.logItem(getOwner().getName() + "|DELL|" + oldItem.getItemId() + "|" + count + "|" + oldItem.getObjectId(), "", "items-detail");
			if(getOwner().getPlayer().can_private_log)
				Log.addMy(getOwner().getName() + "|DELL|" + oldItem.getItemId() + "|" + count + "|" + oldItem.getObjectId(), "items", getOwner().getName());
			//Log.add("Inventory|" + getOwner().getName() + "|Drop item|" + oldItem.getItemId() + "|" + count + "|" + oldItem.getObjectId(), "items");
			removeItemFromInventory(oldItem, false, AllowRemoveAttributes, send_update);
			refreshWeight();

			// check drop pet controls items
			if(PetDataTable.isPetControlItem(oldItem))
				PetDataTable.unSummonPet(oldItem, getOwner());
			return oldItem;
		}
		oldItem.setCount(oldItem.getCount() - count);
		if(send_update)
			sendModifyItem(oldItem);
		else
			oldItem.setLastChange(L2ItemInstance.MODIFIED);
		L2ItemInstance newItem = ItemTemplates.getInstance().createItem(oldItem.getItemId());
		newItem.setCount(count);
		oldItem.updateDatabase(false, true, noLazy);
		refreshWeight();
		Log.logItem(getOwner().getName() + "|SPLIT|" + oldItem.getItemId() + "|" + oldItem.getCount() + "|" + oldItem.getObjectId() + "|" + newItem.getObjectId(), "", "items-detail");
		Log.logItem(getOwner().getName() + "|DELL2|" + newItem.getItemId() + "|" + count + "|" + newItem.getObjectId(), "", "items-detail");
		if(getOwner().getPlayer().can_private_log)
		{
			Log.addMy(getOwner().getName() + "|SPLIT|" + oldItem.getItemId() + "|" + oldItem.getCount() + "|" + oldItem.getObjectId() + "|" + newItem.getObjectId(), "items", getOwner().getName());
			Log.addMy(getOwner().getName() + "|DELL2|" + newItem.getItemId() + "|" + count + "|" + newItem.getObjectId(), "items", getOwner().getName());
		}
		//Log.add("Inventory|" + getOwner().getName() + "|Split item from-to|" + oldItem.getItemId() + "|" + oldItem.getObjectId() + "|" + newItem.getObjectId(), "items");
		//Log.add("Inventory|" + getOwner().getName() + "|Drop item|" + newItem.getItemId() + "|" + count + "|" + newItem.getObjectId(), "items");
		return newItem;
	}

	/**
	 * Refresh the weight of equipment loaded
	 */
	public void refreshWeight()
	{
		long weight = 0;

		for(L2ItemInstance element : getItemsList())
			weight += element.getItem().getWeight() * element.getCount();

		if(_totalWeight == weight)
			return;
		_totalWeight = (int) Math.min(352000000, weight);

		if(getOwner().isPlayer())
			((L2Player) getOwner()).refreshOverloaded();
	}

	public int getTotalWeight()
	{
		return _totalWeight;
	}

	private static final int[][] arrows =
	{
			//
			{ 17 }, // NG
			{ 1341, 22067 }, // D
			{ 1342, 22068 }, // C
			{ 1343, 22069 }, // B
			{ 1344, 22070 }, // A
			{ 1345, 22071 }, // S
	};

	public L2ItemInstance findArrowForBow(L2Item bow)
	{
		int[] arrowsId = arrows[bow.getCrystalType().externalOrdinal];
		L2ItemInstance ret = null;
		for(int id : arrowsId)
			if((ret = getItemByItemId(id)) != null)
				return ret;
		return null;
	}

	private static final int[][] bolts =
	{
			//
			{ 9632 }, // NG
			{ 9633, 22144 }, // D
			{ 9634, 22145 }, // C
			{ 9635, 22146 }, // B
			{ 9636, 22147 }, // A
			{ 9637, 22148 }, // S
	};

	public L2ItemInstance findArrowForCrossbow(L2Item xbow)
	{
		int[] boltsId = bolts[xbow.getCrystalType().externalOrdinal];
		L2ItemInstance ret = null;
		for(int id : boltsId)
			if((ret = getItemByItemId(id)) != null)
				return ret;
		return null;
	}

	public L2ItemInstance findEquippedLure()
	{
		L2ItemInstance res = null;
		int last_lure = 0;
		if(getOwner() != null && getOwner().isPlayer())
			try
			{
				L2Player owner = (L2Player) getOwner();
				String LastLure = owner.getVar("LastLure");
				if(LastLure != null && !LastLure.isEmpty())
					last_lure = Integer.valueOf(LastLure);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		for(L2ItemInstance temp : getItemsList())
			if(temp.getItemType() == EtcItemType.BAIT)
				if(temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == PAPERDOLL_LHAND)
					return temp;
				else if(last_lure > 0 && res == null && temp.getObjectId() == last_lure)
					res = temp;
		return res;
	}

	/**
	 * Delete item object from world
	 */
	public synchronized void deleteMe()
	{
		for(L2ItemInstance inst : getItemsList())
		{
			// пусть пока будет такая затычка...
			/*if(inst.getItem().isTerritoryFlag() && inst.getCustomType1() != 77) // 77 это эвентовый флаг
			{
				L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(inst.getItemId());
				if(flagNpc != null)
					flagNpc.returnToCastle(getOwner().getPlayer());
			}*/

			PlayerData.getInstance().updateInDb(inst);
			inst.deleteMe();
		}
		getItemsList().clear();
		getRefundItemsList().clear();
		if(_listenedItems != null)
			_listenedItems.clear(); // !!!???
		if(_paperdollListeners != null)
			_paperdollListeners.clear();
	}

	public void updateDatabase(boolean commit)
	{
		updateDatabase(getItemsList(), commit);
	}

	public void updateDatabase(ConcurrentLinkedQueue<L2ItemInstance> items, boolean commit)
	{
		if(getOwner() != null)
			for(L2ItemInstance inst : items)
				inst.updateDatabase(commit, true);
	}

	/**
	 * Функция для валидации вещей в инвентаре. Вызывается при загрузке персонажа.
	 */
	public void validateItems()
	{
		for(L2ItemInstance item : getItemsList())
		{
			if(!getOwner().isPlayer())
				continue;
			L2Player player = getOwner().getPlayer();
			// Clan Apella armor
			if(item.isClanApellaItem() && player.getPledgeClass() < L2Player.RANK_WISEMAN)
				unEquipItem(item);
			else if(item.getItem().isCloak() && item.getName().contains("Knight") && player.getPledgeClass() < L2Player.RANK_KNIGHT)
				unEquipItem(item);
			// Clan Oath Armor
			else if(item.getItemId() >= 7850 && item.getItemId() <= 7859 && player.getLvlJoinedAcademy() == 0 && ConfigValue.OnlyAcademicItem)
				unEquipItem(item);
			// Hero Weapons
			else if(item.isHeroWeapon() && getOwner().isHeroType() != 0 && !(getOwner().isHeroType() == 2 && ConfigValue.SellHeroItemForPremium))
			{
				unEquipItem(item);
				destroyItem(item, 1, false);
			}
			// Wings of Destiny Circlet
			else if(item.getItemId() == 6842 && getOwner().isHeroType() != 0 && !(getOwner().isHeroType() == 2 && ConfigValue.SellHeroItemForPremium))
				unEquipItem(item);
		}
	}

	/**
	 * Refresh all listeners
	 * дергать осторожно, если какой-то предмет дает хп/мп то текущее значение будет сброшено
	 */
	public void refreshListeners(boolean update_icon)
	{
		if(getOwner().isPlayer())
			getOwner().getPlayer().revalidatePenalties();
		setRefreshingListeners(true);
		for(int i = 0; i < _paperdoll.length; i++)
		{
			L2ItemInstance item = getPaperdollItem(i);
			if(item == null)
				continue;
			for(PaperdollListener listener : _paperdollListeners)
			{
				listener.notifyUnequipped(i, item, update_icon);
				listener.notifyEquipped(i, item, update_icon);
			}
		}
		if(_listenedItems != null)
			for(L2ItemInstance item : _listenedItems)
				for(PaperdollListener listener : _paperdollListeners)
				{
					listener.notifyUnequipped(-1, item, update_icon);
					listener.notifyEquipped(-1, item, update_icon);
				}
		setRefreshingListeners(false);
		getOwner().updateStats();
		if(getOwner().getPet() != null)
			getOwner().getPet().updateStats();
		if(getOwner().isPlayer())
			getOwner().sendPacket(new SkillList(getOwner().getPlayer()));
	}

	public void refreshListeners(L2ItemInstance item, int enchant)
	{
		refreshListeners(item, enchant, 1);
	}

	public void refreshListeners(L2ItemInstance item, int enchant, long enchant_t)
	{
		if(item == null) // Делаем проверку раньше, так как обращение к итему идет при создании свича.
			return;

		byte pdollSlot = -1;
		switch(item.getBodyPart())
		{
			case L2Item.SLOT_NECK:
				pdollSlot = PAPERDOLL_NECK;
				break;
			case L2Item.SLOT_L_EAR:
				pdollSlot = PAPERDOLL_LEAR;
				break;
			case L2Item.SLOT_R_EAR:
				pdollSlot = PAPERDOLL_REAR;
				break;
			case L2Item.SLOT_L_FINGER:
				pdollSlot = PAPERDOLL_LFINGER;
				break;
			case L2Item.SLOT_R_FINGER:
				pdollSlot = PAPERDOLL_RFINGER;
				break;
			case L2Item.SLOT_HAIR:
				pdollSlot = PAPERDOLL_HAIR;
				break;
			case L2Item.SLOT_DHAIR:
				pdollSlot = PAPERDOLL_DHAIR;
				break;
			case L2Item.SLOT_HAIRALL:
				pdollSlot = PAPERDOLL_HAIR;
				break;
			case L2Item.SLOT_HEAD:
				pdollSlot = PAPERDOLL_HEAD;
				break;
			case L2Item.SLOT_R_HAND:
				pdollSlot = PAPERDOLL_RHAND;
				break;
			case L2Item.SLOT_L_HAND:
				pdollSlot = PAPERDOLL_LHAND;
				break;
			case L2Item.SLOT_GLOVES:
				pdollSlot = PAPERDOLL_GLOVES;
				break;
			case L2Item.SLOT_LEGS:
				pdollSlot = PAPERDOLL_LEGS;
				break;
			case L2Item.SLOT_CHEST:
			case L2Item.SLOT_FULL_ARMOR:
			case L2Item.SLOT_FORMAL_WEAR:
				pdollSlot = PAPERDOLL_CHEST;
				break;
			case L2Item.SLOT_BACK:
				pdollSlot = PAPERDOLL_BACK;
				break;
			case L2Item.SLOT_FEET:
				pdollSlot = PAPERDOLL_FEET;
				break;
			case L2Item.SLOT_UNDERWEAR:
				pdollSlot = PAPERDOLL_UNDER;
				break;
			case L2Item.SLOT_BELT:
				pdollSlot = PAPERDOLL_BELT;
				break;
			case L2Item.SLOT_LR_HAND:
				pdollSlot = PAPERDOLL_RHAND;
				break;
			case L2Item.SLOT_L_BRACELET:
				pdollSlot = PAPERDOLL_LBRACELET;
				break;
			case L2Item.SLOT_R_BRACELET:
				pdollSlot = PAPERDOLL_RBRACELET;
				break;
			case L2Item.SLOT_DECO:
				if(getPaperdollObjectId(PAPERDOLL_DECO1) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO1;
				else if(getPaperdollObjectId(PAPERDOLL_DECO2) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO2;
				else if(getPaperdollObjectId(PAPERDOLL_DECO3) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO3;
				else if(getPaperdollObjectId(PAPERDOLL_DECO4) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO4;
				else if(getPaperdollObjectId(PAPERDOLL_DECO5) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO5;
				else if(getPaperdollObjectId(PAPERDOLL_DECO6) == item.getObjectId())
					pdollSlot = PAPERDOLL_DECO6;
				break;
			default:
				Thread.dumpStack();
		}
		if(getOwner().isPlayer())
			getOwner().getPlayer().revalidatePenalties();
		setRefreshingListeners(true);
		for(PaperdollListener listener : _paperdollListeners)
			listener.notifyUnequipped(pdollSlot, item, true);
		// Ракалская затычка, для ольфа...
		if(enchant >= 0)
			item.setEnchantLevel(enchant, enchant_t);
		for(PaperdollListener listener : _paperdollListeners)
			listener.notifyEquipped(pdollSlot, item, true);
		if(getOwner() == null)
			return;
		getOwner().updateStats();
		if(getOwner().getPet() != null)
			getOwner().getPet().updateStats();
		if(getOwner().isPlayer())
			getOwner().sendPacket(new SkillList(getOwner().getPlayer()));
		setRefreshingListeners(false);
		sendModifyItem(item);
	}

	public void refreshListenersUnequipped(int slot, L2ItemInstance item)
	{
		if(getOwner().isPlayer())
			getOwner().getPlayer().revalidatePenalties();
		for(PaperdollListener listener : _paperdollListeners)
			listener.notifyUnequipped(slot, item, true);
		if(getOwner() == null || !item.isEquipable())
			return;
		getOwner().updateStats();
		if(getOwner().getPet() != null)
			getOwner().getPet().updateStats();
		if(getOwner().isPlayer())
			getOwner().sendPacket(new SkillList(getOwner().getPlayer()));
	}

	public void refreshListenersEquipped(int slot, L2ItemInstance item)
	{
		if(getOwner().isPlayer())
			getOwner().getPlayer().revalidatePenalties();
		for(PaperdollListener listener : _paperdollListeners)
			listener.notifyEquipped(slot, item, true);
		getOwner().updateStats();
		if(getOwner().getPet() != null)
			getOwner().getPet().updateStats();
		if(getOwner().isPlayer())
			getOwner().sendPacket(new SkillList(getOwner().getPlayer()));
	}

	public boolean isRefreshingListeners()
	{
		return _refreshingListeners;
	}

	public void setRefreshingListeners(boolean refreshingListeners)
	{
		_refreshingListeners = refreshingListeners;
	}

	/**
	 * Вызывается из RequestSaveInventoryOrder
	 */
	public void sort(int[][] order)
	{
		L2ItemInstance _item;
		ItemLocation _itemloc;
		for(int[] element : order)
		{
			_item = getItemByObjectId(element[0]);
			if(_item == null)
				continue;
			_itemloc = _item.getLocation();
			if(_itemloc != ItemLocation.INVENTORY)
				continue;
			_item.setLocation(_itemloc, element[1]);
		}
	}

	public long getCountOf(int itemId)
	{
		long result = 0;
		for(L2ItemInstance item : getItemsList())
			if(item != null && item.getItemId() == itemId)
				result += item.getCount();
		return result;
	}

	/**
	 * Снимает все вещи, которые нельзя носить.
	 * Применяется при смене саба, захвате замка, выходе из клана.
	 */
	public void checkAllConditions()
	{
		for(L2ItemInstance item : _paperdoll)
			if(item != null && checkConditions(item) != null)
			{
				unEquipItem(item);
				getOwner().getPlayer().sendDisarmMessage(item);
			}
	}

	/**
	 * Проверяет возможность носить эту вещь.
	 */
	private SystemMessage checkConditions(L2ItemInstance item)
	{
		L2Player owner = getOwner().getPlayer();
		int itemId = item.getItemId();
		int targetSlot = item.getBodyPart();
		L2Clan ownersClan = owner.getClan();

		// Hero items
		if((item.isHeroWeapon() || item.getItemId() == 6842) && getOwner().isHeroType() != 0 && !(getOwner().isHeroType() == 2 && ConfigValue.SellHeroItemForPremium))
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		// камаэли и хеви/робы/щиты/сигилы
		if(!item.getItem().isCombatFlag() && !item.getItem().isTerritoryFlag() && !ConfigValue.KamaelEquipAllItem && owner.getRace() == Race.kamael && (item.getItemType() == ArmorType.HEAVY || item.getItemType() == ArmorType.MAGIC || item.getItemType() == ArmorType.SIGIL || item.getItemType() == WeaponType.NONE))
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		// не камаэли и рапиры/арбалеты/древние мечи
		if(owner.getRace() != Race.kamael && (item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.RAPIER || item.getItemType() == WeaponType.ANCIENTSWORD))
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		if(itemId >= 7850 && itemId <= 7859 && owner.getLvlJoinedAcademy() == 0 && ConfigValue.OnlyAcademicItem) // Clan Oath Armor
			return Msg.THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY;

		if(item.isClanApellaItem() && owner.getPledgeClass() < L2Player.RANK_WISEMAN)
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		if(item.getItemType() == WeaponType.DUALDAGGER && owner.getSkillLevel(923) <= 0)
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		if(item.getItem() instanceof L2Armor && ((L2Armor) item.getItem()).getClassType() != null)
		{
			L2Armor armor = ((L2Armor) item.getItem());
			if(!PlayerClass.values()[owner.getActiveClassId()].isOfType(armor.getClassType()) && (armor.getClassType() != ClassType.DaggerMaster || (armor.getClassType() == ClassType.DaggerMaster && owner.getClassId() != ClassId.fortuneSeeker)))
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();
		}
		else if(item.isWeapon() && ((L2Weapon) item.getItem()).getClassType() != null)
		{
			L2Weapon armor = ((L2Weapon) item.getItem());
			if(!PlayerClass.values()[owner.getActiveClassId()].isOfType(armor.getClassType()))
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();
		}

		// Замковые короны, доступные для всех членов клана
		if(Arrays.asList(_castleCirclets).contains(itemId) && (ownersClan == null || itemId != _castleCirclets[ownersClan.getHasCastle()]))
			return new SystemMessage(new CustomMessage("l2open.gameserver.model.Inventory.CircletWorn", owner).addString(CastleManager.getInstance().getCastleByIndex(Arrays.asList(_castleCirclets).indexOf(itemId)).getName()));
		
		if(item.getItem().hasCastle() > -1 && (ownersClan == null || ownersClan.getHasCastle() != item.getItem().hasCastle()))
			return new SystemMessage(new CustomMessage("ItemCastleWrong", owner).addString(CastleManager.getInstance().getCastleByIndex(item.getItem().hasCastle()).getName()));

		// Корона лидера клана, владеющего замком
		if(itemId == 6841 && (ownersClan == null || !owner.isClanLeader() || ownersClan.getHasCastle() == 0))
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		// Нельзя одевать оружие, если уже одето проклятое оружие. Проверка двумя способами, для надежности. 
		if(targetSlot == L2Item.SLOT_LR_HAND || targetSlot == L2Item.SLOT_L_HAND || targetSlot == L2Item.SLOT_R_HAND)
		{
			if(itemId != getPaperdollItemId(PAPERDOLL_RHAND, false, false) && CursedWeaponsManager.getInstance().isCursed(getPaperdollItemId(PAPERDOLL_RHAND, false, false)))
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();
			if(owner.isCursedWeaponEquipped() && itemId != owner.getCursedWeaponEquippedId())
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();
		}

		// Плащи
		if(item.getItem().isCloak())
		{
			// Can be worn by Knights or higher ranks who own castle
			if(item.getName().contains("Knight") && (owner.getPledgeClass() < L2Player.RANK_KNIGHT || owner.getCastle() == null))
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

			// Плащи для камаэлей
			if(item.getName().contains("Kamael") && owner.getRace() != Race.kamael)
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

			if(!ConfigValue.CloakUseAllow && !Util.contains(ConfigValue.CloakUseAllowList, item.getItemId()))
			{
				// Плащи можно носить только с S80 или S84 сетом
				boolean cloack_usable = false;
				for(int skill : ConfigValue.SkillsS80andS84Sets)
					if(owner.getSkillLevel(skill) > 0)
						cloack_usable = true;
				if(!cloack_usable)
					return Msg.THE_CLOAK_CANNOT_BE_EQUIPPED_BECAUSE_A_NECESSARY_ITEM_IS_NOT_EQUIPPED;
			}
		}

		if(targetSlot == L2Item.SLOT_R_BRACELET)
		{
			// Agathion Seal Bracelet - %Castle%
			if(itemId >= 9607 && itemId <= 9615 && (ownersClan == null || itemId - 9606 != ownersClan.getHasCastle()))
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();
			// Agathion Seal Bracelet - Fortress
			if(itemId == 10018 && (ownersClan == null || ownersClan.getHasFortress() == 0))
				return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();
			//TODO: Agathion Seal Bracelet - Rudolph
			//if((itemId == 10606 || itemId == 10607) && !Chrismas:))
			//{}
			//TODO: 9605/9606 - для нереализованых КХ
		}

		// Для квеста _128_PailakaSongofIceandFire
		// SpritesSword = 13034; EnhancedSpritesSword = 13035; SwordofIceandFire = 13036;
		if((itemId == 13034 || itemId == 13035 || itemId == 13036) && !owner.getReflection().getName().equalsIgnoreCase("Pailaka Song of Ice and Fire") && !owner.isGM())
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		// Для квеста _129_PailakaDevilsLegacy
		// SWORD = 13042; ENCHSWORD = 13043; LASTSWORD = 13044;
		if((itemId == 13042 || itemId == 13043 || itemId == 13044) && !owner.getReflection().getName().equalsIgnoreCase("Pailaka Devils Legacy") && !owner.isGM())
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		// Для квеста _144_PailakaInjuredDragon
		// SPEAR = 13052; ENCHSPEAR = 13053; LASTSPEAR = 13054;
		if((itemId == 13052 || itemId == 13053 || itemId == 13054) && !owner.getReflection().getName().equalsIgnoreCase("Pailaka Injured Dragon") && !owner.isGM())
			return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM();

		if(targetSlot == L2Item.SLOT_DECO)
		{
			// Нельзя одевать талисманы без правого браслета
			if(_paperdoll[PAPERDOLL_RBRACELET] == null)
				return new SystemMessage(SystemMessage.YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_THE_BRACELET).addItemName(itemId);

			// Проверяем на количество слотов и одинаковые талисманы
			int max = (int) owner.calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
			L2ItemInstance deco;
			for(int slot = Inventory.PAPERDOLL_DECO1; slot <= Inventory.PAPERDOLL_DECO6; slot++)
			{
				deco = owner.getInventory().getPaperdollItem(slot);
				if(deco != null)
				{
					if(deco == item)
						return null; // талисман уже одет и количество слотов больше нуля
					// Проверяем на количество слотов и одинаковые талисманы
					if(--max <= 0 || deco.getItemId() == itemId)
						return new SystemMessage(SystemMessage.THERE_IS_NO_SPACE_TO_WEAR_S1).addItemName(itemId);
				}
			}
		}
		return null;
	}

	private static class ItemOrderComparator implements Comparator<L2ItemInstance>
	{
		@Override
		public int compare(L2ItemInstance o1, L2ItemInstance o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			return o1.getEquipSlot() - o2.getEquipSlot();
		}
	}

	// Заблокированные предметы.
	private LockType _lockType = LockType.NONE;
	private int[] _lockItems = ArrayUtils.EMPTY_INT_ARRAY;

	public void lockItems(LockType lock, int[] items)
	{
		if(_lockType != LockType.NONE)
			return;

		_lockType = lock;
		_lockItems = items;

		L2Player player = getOwner().getPlayer();
		player.sendPacket(new ItemList(player, false));
	}

	public void unlock()
	{
		if(_lockType == LockType.NONE)
			return;

		_lockType = LockType.NONE;
		_lockItems = ArrayUtils.EMPTY_INT_ARRAY;

		L2Player player = getOwner().getPlayer();
		player.sendPacket(new ItemList(player, false));
	}

	public boolean isLockedItem(L2ItemInstance item)
	{
		switch(_lockType)
		{
			case INCLUDE:
				return Util.contains_int(_lockItems, item.getItemId());
			case EXCLUDE:
				return !Util.contains_int(_lockItems, item.getItemId());
			default:
				return false;
		}
	}

	public LockType getLockType()
	{
		return _lockType;
	}

	public int[] getLockItems()
	{
		return _lockItems;
	}

	public static ItemOrderComparator OrderComparator = new ItemOrderComparator();

	public boolean isWearEquipped()
	{
		L2ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
		if(chest != null && chest.getBodyPart() == L2Item.SLOT_FORMAL_WEAR)
			return true;
		return false;
	}

	public void restore()
	{
		PlayerData.getInstance().restoreInventory(this, getOwner());
	}
}