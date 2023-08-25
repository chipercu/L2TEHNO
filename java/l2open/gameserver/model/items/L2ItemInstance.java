package l2open.gameserver.model.items;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.Stat;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Events;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.MercTicketManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.L2Augmentation;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.AugmentationData;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.ItemsAutoDestroy;
import l2open.gameserver.templates.L2Armor;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Util;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class L2ItemInstance extends L2Object
{
	private static final Logger _log = Logger.getLogger(L2ItemInstance.class.getName());

	/** Enumeration of locations for item */
	public static enum ItemLocation
	{
		VOID, // это типа проданные вещи чаром, которые можно выкупить
		INVENTORY, // просто в рюкзаке
		PAPERDOLL, // одетые
		WAREHOUSE, // личное хранилище
		CLANWH, // клан хранилище
		FREIGHT, // хз, что-то тоже с хранилищем связано, а вроде бы передача на другого чара
		LEASE, // это вещи отправленные по почте, но еще не полученные адресатом
		MONSTER, // лежат у монстров
		DUMMY, // не юзается хз нах оно у нас:)
		SELL, // Итемы с аукциона или рынка...
		PET, // у пета в инвентаре
		PET_PAPERDOLL // одето на пете
	}

	/** Item types to select */
	public static enum ItemClass
	{
		/** List all deposited items */
		ALL,
		/** Weapons, Armor, Jevels, Arrows, Baits*/
		EQUIPMENT,
		/** Soul/Spiritshot, Potions, Scrolls */
		CONSUMABLE,
		/** Common craft matherials */
		MATHERIALS,
		/** Special (item specific) craft matherials */
		PIECES,
		/** Crafting recipies */
		RECIPIES,
		/** Skill learn books */
		SPELLBOOKS,
		/** Dyes, lifestones */
		MISC,
		/** All other */
		OTHER
	}

	private L2Player _itemDropOwner;

	private L2Player _owner;

	private int _ownerId;

	public long _enchant_time=0;
	public ScheduledFuture<?> _enchant_timer;
	private final Lock lock = new ReentrantLock();

	/** Время жизни призрачных вещей **/
	ScheduledFuture<?> _itemLifeTimeTask;
	public int _lifeTimeRemaining;

	/** Quantity of the item */
	public long _count = 0;

	/** ID of the item */
	public int _itemId;
	public int _visual_item_id;

	/** Object L2Item associated to the item */
	private L2Item _itemTemplate;

	/** Location of the item */
	private ItemLocation _loc;

	/** Slot where item is stored */
	public int _loc_data;

	/** Level of enchantment of the item */
	public int _enchantLevel;
	public int _visual_enchant_level = -1;

	/** Price of the item for selling */
	private long _price_sell;

	private long _count_sell;

	/** Wear Item */
	private boolean _wear;

	/** Энергия Агатиона */
	public int _agathionEnergy;

	public L2Augmentation _augmentation = null;

	/** Custom item types (used loto, race tickets) */
	public int _type1;
	public int _type2;

	/** Item drop time for autodestroy task */
	private long _dropTime;

	private boolean _dropPlayer = false;

	/** Item drop time */
	private long _dropTimeOwner;

	public static final byte CHARGED_NONE = 0;
	public static final byte CHARGED_SOULSHOT = 1;
	public static final byte CHARGED_SPIRITSHOT = 1;
	public static final byte CHARGED_BLESSED_SPIRITSHOT = 2;

	private byte _chargedSoulshot = CHARGED_NONE;
	private byte _chargedSpiritshot = CHARGED_NONE;

	private boolean _chargedFishtshot = false;

	public static final byte UNCHANGED = 0;
	public static final byte ADDED = 1;
	public static final byte REMOVED = 3;
	public static final byte MODIFIED = 2;
	public byte _lastChange = 2; //1 ??, 2 modified, 3 removed
	public boolean _existsInDb; // if a record exists in DB.
	public boolean _storedInDb; // if DB data is up-to-date.
	/** Element (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None) */
	public byte attackAttributeElement = L2Item.ATTRIBUTE_NONE;
	public int attackAttributeValue = 0;
	public int[] defenseAttributes = new int[] { 0, 0, 0, 0, 0, 0 };
	public List<FuncTemplate> _enchantAttributeFuncTemplate = null;

	/**
	* Спецфлаги для конкретного инстанса
	*/
	public int _customFlags = 0;

	public static final int FLAG_NO_DROP = 1 << 0; // 1
	public static final int FLAG_NO_TRADE = 1 << 1; // 2
	public static final int FLAG_NO_TRANSFER = 1 << 2; // 4
	public static final int FLAG_NO_CRYSTALLIZE = 1 << 3; // 8
	public static final int FLAG_NO_ENCHANT = 1 << 4; // 16
	public static final int FLAG_NO_DESTROY = 1 << 5; // 32
	public static final int FLAG_NO_UNEQUIP = 1 << 6; // 64
	public static final int FLAG_ALWAYS_DROP_ON_DIE = 1 << 7; // 128
	public static final int FLAG_EQUIP_ON_PICKUP = 1 << 8; // 256
	public static final int FLAG_NO_RIDER_PICKUP = 1 << 9; // 512
	public static final int FLAG_PET_EQUIPPED = 1 << 10; // 1024

	public boolean _temporal;
	public boolean _is_premium = false;

	private Future<?> _lazyUpdateInDb;

	public int _is_event=-1;

	/** Task of delayed update item info in database */
	private class LazyUpdateInDb extends l2open.common.RunnableImpl
	{
		private final L2ItemInstance _item;

		public LazyUpdateInDb(L2ItemInstance item)
		{
			_item = item;
		}

		public void runImpl()
		{
			if(_item == null)
				return;
			try
			{
				PlayerData.getInstance().updateInDb(_item);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				_item.stopLazyUpdateTask(false);
			}
		}
	}

	private int _bodypart;

	/**
	 * Constructor<?> of the L2ItemInstance from the objectId and the itemId.
	 * @param objectId : int designating the ID of the object in the world
	 * @param itemId : int designating the ID of the item
	 */
	public L2ItemInstance(int objectId, int itemId)
	{
		this(objectId, ItemTemplates.getInstance().getTemplate(itemId), false);
	}

	/**
	 * Constructor<?> of the L2ItemInstance from the objetId and the description of the item given by the L2Item.
	 * @param objectId : int designating the ID of the object in the world
	 * @param item : L2Item containing informations of the item
	 */
	public L2ItemInstance(int objectId, L2Item item, boolean isTemporal)
	{
		super(objectId, false);
		if(item == null)
		{
			_log.warning("Not found template for item id: " + _itemId);
			Util.test();
			throw new IllegalArgumentException();
		}

		_itemId = item.getItemId();
		_itemTemplate = item;
		_count = 1;
		_loc = ItemLocation.VOID;
		_customFlags = item.getFlags();

		_dropTime = 0;
		_dropTimeOwner = 0;
		setItemDropOwner(null, 0);

		_is_premium = _itemTemplate._is_premium;
		_temporal = isTemporal || _itemTemplate.isTemporal();
		_lifeTimeRemaining = _temporal ? (int) (System.currentTimeMillis() / 1000) + _itemTemplate.getDurability() * 60 : _itemTemplate.getDurability();
		_bodypart = _itemTemplate.getBodyPart();

		if(item.isAttAtack() || item.isAttDef())
			setAttributeElement(item.attAtackType(), item.attAtackValue(), item.attDefType(), false);

		// Энергия пета.
		_agathionEnergy = _itemTemplate.getAgathionEnergy();
	}

	public int getBodyPart()
	{
		return _bodypart;
	}

	public void setBodyPart(int bodypart)
	{
		_bodypart = bodypart;
	}

	public void setOwnerId(int ownerId)
	{
		if(getOwnerId() != ownerId)
			_storedInDb = false;
		_owner = L2ObjectsStorage.getPlayer(ownerId);
		_ownerId = ownerId;
		startTemporalTask(_owner);
		if(ConfigValue.DetailLogItem2)
			Log.logItem((_owner == null ? "OWNER DELETE" : _owner.getName()) + "|ADD|" + getItemId() + "|" + getCount() + "|" + getObjectId(), "", "items-detail-new");
	}

	/**
	 * Returns the ownerID of the item
	 * @return int : ownerID of the item
	 */
	public int getOwnerId()
	{
		return _ownerId;
	}

	public void setLocat(ItemLocation loc, boolean can_check)
	{
		_loc = loc;
		// пусть пока будет такая затычка...
		if(can_check && loc != ItemLocation.PAPERDOLL && getItem().isTerritoryFlag() && getCustomType1() != 77) // 77 это эвентовый флаг
		{
			L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(getItemId());
			if(flagNpc != null)
				flagNpc.returnToCastle(getOwner());
		}
	}
	/**
	 * Sets the location of the item
	 * @param loc : ItemLocation (enumeration)
	 */
	public void setLocation(ItemLocation loc)
	{
		setLocation(loc, 0, false);
	}

	/**
	 * Sets the location of the item.<BR><BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc : ItemLocation (enumeration)
	 * @param loc_data : int designating the slot where the item is stored or the village for freights
	 */
	public void setLocation(ItemLocation loc, int loc_data)
	{
		setLocation(loc, loc_data, false);
	}

	public void setLocation(ItemLocation loc, int loc_data, boolean can_check)
	{
		if(loc == _loc && loc_data == _loc_data)
			return;

		if(getItemId() == 57 && loc != _loc)
			if(loc == ItemLocation.VOID)
				Stat.addAdena(-_count);
			else if(_loc == ItemLocation.VOID)
				Stat.addAdena(_count);

		setLocat(loc, can_check);
		_loc_data = loc_data;
		_storedInDb = false;
	}

	public ItemLocation getLocation()
	{
		return _loc;
	}

	public long getLongLimitedCount()
	{
		return Math.min(_count, Long.MAX_VALUE);
	}

	/**
	 * Возвращает количество предметов
	 * @return long
	 */
	public long getCount()
	{
		return _count;
	}

	/**
	 * Sets the quantity of the item.<BR><BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param count : long
	 */
	public void setCount(long count)
	{
		if(getItemId() == 57 && _loc != ItemLocation.VOID)
			Stat.addAdena(count - _count);

		if(count < 0)
			count = 0;
		if(!isStackable() && count > 1)
		{
			_count = 1;
			Log.IllegalPlayerAction(getPlayer(), "tried to stack unstackable item " + getItemId(), 0);
			return;
		}
		if(_count == count)
			return;
		_count = count;
		_storedInDb = false;
	}

	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return _itemTemplate.isEquipable();
	}

	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return _loc == ItemLocation.PAPERDOLL || _loc == ItemLocation.PET_PAPERDOLL;
	}

	/**
	 * Returns the slot where the item is stored
	 * @return int
	 */
	public int getEquipSlot()
	{
		return _loc_data;
	}

	/**
	 * Returns the characteristics of the item
	 * @return L2Item
	 */
	public L2Item getItem()
	{
		return _itemTemplate;
	}

	public int getCustomType1()
	{
		return _type1;
	}

	public int getCustomType2()
	{
		return _type2;
	}

	public void setCustomType1(int newtype)
	{
		_type1 = newtype;
	}

	public void setCustomType2(int newtype)
	{
		_type2 = newtype;
	}

	public void setDropTime(long time)
	{
		_dropTime = time;
	}

	public long getDropTime()
	{
		return _dropTime;
	}

	public void setDropPlayer(boolean player)
	{
		_dropPlayer = player;
	}

	public boolean isDropPlayer()
	{
		return _dropPlayer;
	}

	public boolean getOlympiadUse()
	{
		return _itemTemplate.isOlympiadUse();
	}

	public int getReuseDelay()
	{
		return _itemTemplate.getReuseDelay();
	}

	public long getDropTimeOwner()
	{
		return _dropTimeOwner;
	}

	public void setItemDropOwner(L2Player owner, long time)
	{
		_itemDropOwner = owner;
		_dropTimeOwner = owner != null ? System.currentTimeMillis() + time : 0;
	}

	public L2Player getItemDropOwner()
	{
		return _itemDropOwner;
	}

	public boolean isWear()
	{
		return _wear;
	}

	public void setWear(boolean newwear)
	{
		_wear = newwear;
	}

	/**
	 * Returns the type of item
	 * @return Enum
	 */
	public Enum getItemType()
	{
		return _itemTemplate.getItemType();
	}

	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public int getItemId()
	{
		return _itemId;
	}

	public int getVisualItemId()
	{
		if(ConfigValue.VisualItemOlympiadDisable && _visual_item_id > 0 && getOwner() != null && getOwner().getPlayer().isInOlympiadMode())
			return 0;
		return _visual_item_id;
	}

	/**
	 * Returns the reference price of the item
	 * @return int
	 */
	public int getReferencePrice()
	{
		return _itemTemplate.getReferencePrice();
	}

	/**
	 * Returns the price of the item for selling
	 * @return int
	 */
	public long getPriceToSell()
	{
		return _price_sell;
	}

	/**
	 * Sets the price of the item for selling
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param price : int designating the price
	 */
	public void setPriceToSell(long price)
	{
		_price_sell = price;
	}

	public void setCountToSell(long count)
	{
		_count_sell = count;
	}

	public long getCountToSell() //TODO: long
	{
		return _count_sell;
	}

	/**
	 * Returns the last change of the item
	 * @return int
	 */
	public int getLastChange()
	{
		return _lastChange;
	}

	/**
	 * Sets the last change of the item
	 * @param lastChange : int
	 */
	public void setLastChange(byte lastChange)
	{
		_lastChange = lastChange;
	}

	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return _itemTemplate.isStackable();
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(Events.onAction(player, this, shift))
			return;

		if((player.isCursedWeaponEquipped() || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped()) && CursedWeaponsManager.getInstance().isCursed(_itemId))
			return;

		int _castleId = MercTicketManager.getInstance().getTicketCastleId(_itemId);
		if(_castleId > 0)
		{
			L2Clan clan = player.getClan();
			// mercenary tickets can only be picked up by the castle owner.
			if(clan != null && clan.getHasCastle() == _castleId && (player.getClanPrivileges() & L2Clan.CP_CS_MERCENARIES) == L2Clan.CP_CS_MERCENARIES || player.isGM())
				if(player.isInParty())
					player.sendMessage(new CustomMessage("l2open.gameserver.model.items.L2ItemInstance.NoMercInParty", player));
				else
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this);
			else
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);

			player.setTarget(this);
			player.sendActionFailed();
		}
		else
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this, null);
	}

	/**
	 * Returns the level of enchantment of the item
	 * @return int
	 */
	public int getRealEnchantLevel()
	{
		return _enchantLevel;
	}

	public int getEnchantLevel()
	{
		if(getOwner() != null && getOwner().getPlayer() != null)
		{
			if(ConfigValue.MaxEnchantForOlympiadEnable && getOwner().getPlayer().isInOlympiadMode())
			{
				if(isWeapon() && _enchantLevel > ConfigValue.MaxEnchantWeaponForOlympiad)
					return ConfigValue.MaxEnchantWeaponForOlympiad;
				else if(isArmor() && _enchantLevel > ConfigValue.MaxEnchantArmorForOlympiad)
					return ConfigValue.MaxEnchantArmorForOlympiad;
				else if(isAccessory() && _enchantLevel > ConfigValue.MaxEnchantJewelForOlympiad)
					return ConfigValue.MaxEnchantJewelForOlympiad;
				else if(ConfigValue.MinEnchantForOlympiadEnable)
				{
					if(isWeapon() && _enchantLevel < ConfigValue.MinEnchantWeaponForOlympiad)
						return ConfigValue.MinEnchantWeaponForOlympiad;
					else if(isArmor() && _enchantLevel < ConfigValue.MinEnchantArmorForOlympiad)
						return ConfigValue.MinEnchantArmorForOlympiad;
					else if(isAccessory() && _enchantLevel < ConfigValue.MinEnchantJewelForOlympiad)
						return ConfigValue.MinEnchantJewelForOlympiad;
				}
			}
			else if(getOwner().getPlayer().getEventMaster() != null)
				return getOwner().getPlayer().getEventMaster().getEnchantLevel(getOwner().getPlayer(), this);
		}
		return _enchantLevel;
	}

	public int getVisualEnchantLevel()
	{
		if(getOwner() != null && getOwner().getPlayer()._visual_enchant_level_test > -1)
			return getOwner().getPlayer()._visual_enchant_level_test;
		return _visual_enchant_level;
	}

	public void setEnchantLevel(int enchantLevel)
	{
		setEnchantLevel(enchantLevel, 1, false);
	}
	
	public void setEnchantLevel(int enchantLevel, long enchant_t)
	{
		setEnchantLevel(enchantLevel, enchant_t, false);
	}

	/**
	 * Sets the level of enchantment of the item
	 * @param enchantLevel level of enchant
	 */
	public void setEnchantLevel(int enchantLevel, long enchant_t, boolean isFantome)
	{
		_enchant_time=enchant_t;
		if(_enchantLevel == enchantLevel)
			return;
		_enchantLevel = enchantLevel;
		_storedInDb = false;
		if(getItem().getEnchantOptions().size() > 0)
		{
			int[] enchantOptions = getItem().getEnchantOptions().get(enchantLevel);
			_enchantOptions = enchantOptions == null ? new int[3] : enchantOptions;
		}

		L2Player player = getOwner();
		if(_enchantLevel > (isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor) /*&& enchant_t > 0*/ && (_enchant_timer == null || _enchant_time <= System.currentTimeMillis()))
		{
			//_log.info("setEnchantLevel: '"+getName()+"["+getItemId()+"]' ["+enchantLevel+"]["+(isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor)+"]["+enchant_t+"]");
			// При заточке, устанавливаем новое время
			if(enchant_t == 1)
			{
				_enchant_time = System.currentTimeMillis()+ConfigValue.EnchantDecriseTimer*1000L;
				_enchant_timer = ThreadPoolManager.getInstance().schedule(new EnchantDecrise(), ConfigValue.EnchantDecriseTimer*1000L);
				//_log.info("setEnchantLevel: new timer["+_enchant_time+"]["+ConfigValue.EnchantDecriseTimer+"]");
			}
			else
			{
				//_log.info("setEnchantLevel: update["+enchant_t+"]");
				_enchant_time=enchant_t;
				long time = _enchant_time-System.currentTimeMillis();
				if(time > 0)
				{
					_enchant_timer = ThreadPoolManager.getInstance().schedule(new EnchantDecrise(), time);
					//_log.info("setEnchantLevel: start timer["+enchant_t+"]["+time+"]");
				}
				else if(time <= 0)
				{
					//_log.info("setEnchantLevel: update1 timer["+enchant_t+"]["+time+"]");
					time=time*-1+(ConfigValue.EnchantDecriseTimer*1000L);
					int count = (int)(time/(ConfigValue.EnchantDecriseTimer*1000L));
					long next_run = System.currentTimeMillis()+((ConfigValue.EnchantDecriseTimer*1000L)-(time%(ConfigValue.EnchantDecriseTimer*1000L)));

					lock.lock();
					try
					{
						_enchant_time = 0;
						if(_enchantLevel > (isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor))
						{
							if(player != null && isEquipped())
								player.getInventory().refreshListeners(L2ItemInstance.this, Math.max((isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor), _enchantLevel-count), next_run);
							else
							{
								setEnchantLevel(Math.max((isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor), _enchantLevel-count), next_run);
								if(player != null)
									player.sendPacket(new InventoryUpdate().addModifiedItem(L2ItemInstance.this));
							}
						}
					}
					finally
					{
						lock.unlock();
					}
					//_log.info("setEnchantLevel: update2 timer["+count+"]["+next_run+"]");
				}
			}
		}
		else if(_enchant_timer != null && _enchantLevel <= (isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor))
		{
			_enchant_time = 0;
			if(_enchant_timer != null)
			{
				_enchant_timer.cancel(false);
				_enchant_timer = null;
			}
		}

		if(isEquipped())
		{
			if(player == null)
				return;

			L2Item it = getItem();
			//L2Skill[][] enchantSkill = it.getEnchant4Skill();
			HashMap<Integer, L2Skill> enchantSkill = it.getEnchant4Skill();

			try
			{
				// Для оружия и бижутерии, при несоотвествии грейда скилы не выдаем
				switch (it.getType2())
				{
					case L2Item.TYPE2_WEAPON:
						if(player.getWeaponsExpertisePenalty() > 0)
							enchantSkill = null;
						break;
					case L2Item.TYPE2_ACCESSORY:
						if(player.getArmorExpertisePenalty() > 0 && getCrystalType().ordinal() > player.expertiseIndex)
							enchantSkill = null;
						break;
				}
				if(enchantSkill != null && getEnchantLevel() > 0)
					for(int i = 1; i <= getEnchantLevel() && i <= (it.getType2() == L2Item.TYPE2_WEAPON ? ConfigValue.EnchantMaxWeapon : ConfigValue.EnchantMaxArmor); i++)
					{
						/*for(L2Skill sk : enchantSkill[i])
							if(sk != null)
								player.addSkill(sk, false);*/
						L2Skill sk = enchantSkill.get(i);
						if(sk != null)
						{
							// делаем так, что бы работал релоад скилов...
							player.addSkill(SkillTable.getInstance().getInfo(sk.getId(), sk.getLevel()), false);
						}
					}
			}
			finally
			{
				enchantSkill = null;
			}
		}
	}

	/**
	 * Returns false cause item can't be attacked
	 * @return boolean false
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}

	public boolean isAugmented()
	{
		return _augmentation == null ? false : true;
	}

	public L2Augmentation getAugmentation()
	{
		return _augmentation;
	}

	public int getAugmentationId()
	{
		return _augmentation == null ? 0 : _augmentation.getAugmentationId();
	}

	public boolean setAugmentation(L2Augmentation augmentation)
	{
		if(_augmentation != null)
			return false;
		_augmentation = augmentation;
		PlayerData.getInstance().updateItemAttributes(this);
		setCustomFlags(_customFlags & ~FLAG_PET_EQUIPPED, true);
		return true;
	}

	public boolean hasAttribute()
	{
		for(int i = 0; i < defenseAttributes.length; i++)
			if(defenseAttributes[i] > 0)
				return true;
		return attackAttributeElement != L2Item.ATTRIBUTE_NONE;
	}

	/**
	 * Returns the type of charge with SoulShot of the item.
	 * @return int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public byte getChargedSoulshot()
	{
		return _chargedSoulshot;
	}

	/**
	 * Returns the type of charge with SpiritShot of the item
	 * @return int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public byte getChargedSpiritshot()
	{
		return _chargedSpiritshot;
	}

	public boolean getChargedFishshot()
	{
		return _chargedFishtshot;
	}

	/**
	 * Sets the type of charge with SoulShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public void setChargedSoulshot(byte type)
	{
		_chargedSoulshot = type;
	}

	/**
	 * Sets the type of charge with SpiritShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public void setChargedSpiritshot(byte type)
	{
		_chargedSpiritshot = type;
	}

	public void setChargedFishshot(boolean type)
	{
		_chargedFishtshot = type;
	}

	protected FuncTemplate[] _funcTemplates;

	public void attachFunction(FuncTemplate f)
	{
		if(_funcTemplates == null)
			_funcTemplates = new FuncTemplate[] { f };
		else
		{
			int len = _funcTemplates.length;
			FuncTemplate[] tmp = new FuncTemplate[len + 1];
			System.arraycopy(_funcTemplates, 0, tmp, 0, len);
			tmp[len] = f;
			_funcTemplates = tmp;
		}
	}

	public void detachFunction(FuncTemplate f)
	{
		if(_funcTemplates == null || f == null)
			return;
		for(int i = 0; i < _funcTemplates.length; i++)
			if(f.equals(_funcTemplates[i]))
			{
				int len = _funcTemplates.length - 1;
				_funcTemplates[i] = _funcTemplates[len];
				FuncTemplate[] tmp = new FuncTemplate[len];
				System.arraycopy(_funcTemplates, 0, tmp, 0, len);
				_funcTemplates = tmp;
				break;
			}
	}

	/**
	 * This function basically returns a set of functions from
	 * L2Item/L2Armor/L2Weapon, but may add additional
	 * functions, if this particular item instance is enhanched
	 * for a particular player.
	 * @return Func[]
	 */
	public Func[] getStatFuncs(boolean visual)
	{
		GArray<Func> funcs = new GArray<Func>();
		if(!visual)
		{
			if(_itemTemplate.getAttachedFuncs() != null)
				for(FuncTemplate t : _itemTemplate.getAttachedFuncs())
				{
					Func f = t.getFunc(this);
					if(f != null)
						funcs.add(f);
				}
			if(_funcTemplates != null)
				for(FuncTemplate t : _funcTemplates)
				{
					Func f = t.getFunc(this);
					if(f != null)
						funcs.add(f);
				}
		}
		else if(ConfigValue.EnableVisualItemStat)
		{
			L2Item template = ItemTemplates.getInstance().getTemplate(getVisualItemId());
			if(template.getAttachedFuncs() != null)
				for(FuncTemplate t : template.getAttachedFuncs())
				{
					Func f = t.getFunc(this);
					if(f != null)
						funcs.add(f);
				}
		}
		if(funcs.size() == 0)
			return new Func[0];
		return funcs.toArray(new Func[funcs.size()]);
	}

	/**
	 * Updates database.<BR><BR>
	 * <U><I>Concept : </I></U><BR>
	 *
	 * <B>IF</B> the item exists in database :
	 * <UL>
	 *		<LI><B>IF</B> the item has no owner, or has no location, or has a null quantity : remove item from database</LI>
	 *		<LI><B>ELSE</B> : update item in database</LI>
	 * </UL>
	 *
	 * <B> Otherwise</B> :
	 * <UL>
	 *		<LI><B>IF</B> the item hasn't a null quantity, and has a correct location, and has a correct owner : insert item in database</LI>
	 * </UL>
	 */
	public void updateDatabase()
	{
		updateDatabase(false, true, false);
	}

	public void updateDatabase(boolean commit, boolean AllowRemoveAttributes)
	{
		updateDatabase(commit, AllowRemoveAttributes, false);
	}

	public synchronized void updateDatabase(boolean commit, boolean AllowRemoveAttributes, boolean noLazy)
	{
		if(_existsInDb)
		{
			if(_loc == ItemLocation.VOID || _count == 0 || getOwnerId() == 0)
				PlayerData.getInstance().removeFromDb(this, AllowRemoveAttributes);
			else if(!noLazy && ConfigValue.LazyItemUpdate && (isStackable() || ConfigValue.LazyItemUpdateAll)/* && (getOwner() == null ? true : !getOwner().isInStoreMode())*/)
			{
				if(commit)
				{
					// cancel lazy update task if need
					if(stopLazyUpdateTask(true))
					{
						PlayerData.getInstance().insertIntoDb(this); // на всякий случай...
						return;
					}
					PlayerData.getInstance().updateInDb(this);
					Stat.increaseUpdateItemCount();
					return;
				}
				Future<?> lazyUpdateInDb = _lazyUpdateInDb;
				if(lazyUpdateInDb == null || lazyUpdateInDb.isDone())
				{
					_lazyUpdateInDb = ThreadPoolManager.getInstance().schedule(new LazyUpdateInDb(this), isStackable() ? ConfigValue.LazyItemUpdateTime : ConfigValue.LazyItemUpdateAllTime);
					Stat.increaseLazyUpdateItem();
				}
			}
			else
			{
				PlayerData.getInstance().updateInDb(this);
				Stat.increaseUpdateItemCount();
			}
		}
		else
		{
			if(_count == 0 || _loc == ItemLocation.VOID || getOwnerId() == 0)
				return;
			PlayerData.getInstance().insertIntoDb(this);
		}
	}

	public boolean stopLazyUpdateTask(boolean interrupt)
	{
		boolean ret = false;
		if(_lazyUpdateInDb != null)
		{
			ret = _lazyUpdateInDb.cancel(interrupt);
			_lazyUpdateInDb = null;
		}
		return ret;
	}

	/**
	 * При фейле эти свитки не ломают вещь, но сбрасывают заточку
	 */
	public boolean isBlessedEnchantScroll()
	{
		switch(_itemId)
		{
			case 6569: // Wpn A
			case 6570: // Arm A
			case 6571: // Wpn B
			case 6572: // Arm B
			case 6573: // Wpn C
			case 6574: // Arm C
			case 6575: // Wpn D
			case 6576: // Arm D
			case 6577: // Wpn S
			case 6578: // Arm S
			case 17255: // Wpn A Event
			case 17256: // Arm A Event
			case 17257: // Wpn B Event
			case 17258: // Arm B Event
			case 17259: // Wpn C Event
			case 17260: // Arm C Event
			case 17261: // Wpn D Event
			case 17262: // Arm D Event
			case 17263: // Wpn S Event
			case 17264: // Arm S Event
			case 22314: // Wpn S
			case 22315: // Arm S
			case 22316: // Wpn A
			case 22317: // Wpn B
			case 22318: // Arm A
			case 22319: // Arm B
			case 22341: // Wpn C
			case 21582: // Blessed Olf's T-shirt
			case 21707: // Blessed Olf's T-shirt Event

			//---------------------
			case 32415:
			case 32416:
			case 22428:
			case 22429:
				return true;
		}
		return false;
	}

	/**
	 * При фейле эти свитки не имеют побочных эффектов
	 */
	public boolean isAncientEnchantScroll()
	{
		switch(_itemId)
		{
			case 22014: // item Mall Wpn B
			case 22015: // item Mall Wpn A
			case 20519: // item Mall Wpn S
			case 22016: // item Mall Arm B
			case 22017: // item Mall Arm A
			case 20520: // item Mall Arm S
				return true;
		}
		return false;
	}

	/**
	 * Свитоки Разрушения
	 **/
	public boolean isDestructionEnchantScroll()
	{
		switch(_itemId)
		{
			case 22221:
			case 22222:
			case 22223:
			case 22224:
			case 22225:
			case 22226:
			case 22227:
			case 22228:
			case 22229:
			case 22230:
				return true;
		}
		return false;
	}

	/**
	 * Эти свитки имеют 10% бонус шанса заточки
	 */
	public boolean isItemMallEnchantScroll()
	{
		switch(_itemId)
		{
			case 22006: // item Mall Wpn D
			case 22007: // item Mall Wpn C
			case 22008: // item Mall Wpn B
			case 22009: // item Mall Wpn A
			case 20517: // item Mall Wpn S
			case 22010: // item Mall Arm D
			case 22011: // item Mall Arm C
			case 22012: // item Mall Arm B
			case 22013: // item Mall Arm A
			case 20518: // item Mall Arm S
				return true;
			default:
				return isAncientEnchantScroll();
		}
	}

	/**
	 * Оружие РС-клуба.
	 **/
	public boolean isPcClubWeapon()
	{
		if((_itemId >= 13153 && _itemId <= 13224) || (_itemId >= 15313 && _itemId <= 15326))
			return true;
		return false;
	}

	/**
	 * Для модификации оружия РС-клуба.
	 **/
	public boolean isPcClubEnchantScroll()
	{
		switch(_itemId)
		{
			case 15346: // Wpn S
			case 15347: // Wpn A
			case 15348: // Wpn B
			case 15349: // Wpn C
			case 15350: // Wpn D
				return true;
		}
		return false;
	}

	/**
	 * Эти свитки имеют 100% шанс
	 */
	public boolean isDivineEnchantScroll()
	{
		switch(_itemId)
		{
			case 22018: // item Mall Wpn B
			case 22019: // item Mall Wpn A
			case 20521: // item Mall Wpn S
			case 22020: // item Mall Arm B
			case 22021: // item Mall Arm A
			case 20522: // item Mall Arm S
				return true;
		}
		return false;
	}

	/**
	 * Они не используются официальным серером, но могут быть задействованы альтами
	 */
	public boolean isCrystallEnchantScroll()
	{
		switch(_itemId)
		{
			case 731:
			case 732:
			case 949:
			case 950:
			case 953:
			case 954:
			case 957:
			case 958:
			case 961:
			case 962:
				return true;
		}
		return false;
	}

	/**
	 * Проверка соответствия свитка и катализатора грейду вещи.
	 * @return id кристалла для соответствующих и 0 для несоответствующих.
	 */
	public int getEnchantCrystalId(L2ItemInstance scroll, L2ItemInstance catalyst)
	{
		boolean scrollValid = false, catalystValid = false;

		for(int scrollId : getEnchantScrollId())
			if(scroll.getItemId() == scrollId)
			{
				scrollValid = true;
				break;
			}

		if(catalyst == null)
			catalystValid = true;
		else
			for(int catalystId : getEnchantCatalystId())
				if(catalystId == catalyst.getItemId())
				{
					catalystValid = true;
					break;
				}

		if(scrollValid && catalystValid)
			switch(_itemTemplate.getCrystalType().cry)
			{
				case L2Item.CRYSTAL_A:
					return 1461;
				case L2Item.CRYSTAL_B:
					return 1460;
				case L2Item.CRYSTAL_C:
					return 1459;
				case L2Item.CRYSTAL_D:
					return 1458;
				case L2Item.CRYSTAL_S:
					return 1462;
			}

		return 0;
	}

	/**
	 * Возвращает список свитков, которые подходят для вещи.
	 */
	public int[] getEnchantScrollId()
	{
		if(_itemTemplate.getType2() == L2Item.TYPE2_WEAPON)
			switch(_itemTemplate.getCrystalType().cry)
			{
				case L2Item.CRYSTAL_A:
					return new int[] { 729, 6569, 731, 22009, 22015, 22019, 22223, 17255, 22316, 15347 };
				case L2Item.CRYSTAL_B:
					return new int[] { 947, 6571, 949, 22008, 22014, 22018, 22225, 17257, 22317, 15348 };
				case L2Item.CRYSTAL_C:
					return new int[] { 951, 6573, 953, 22007, 22227, 17259, 22341, 15349 };
				case L2Item.CRYSTAL_D:
					return new int[] { 955, 6575, 957, 22006, 22229, 17261, 15350 };
				case L2Item.CRYSTAL_S:
					return new int[] { 959, 6577, 961, 20517, 20519, 20521, 22221, 17263, 22314, 15346, 32415, 22428 };
			}
		else if(_itemTemplate.getType2() == L2Item.TYPE2_SHIELD_ARMOR || _itemTemplate.getType2() == L2Item.TYPE2_ACCESSORY)
			switch(_itemTemplate.getCrystalType().cry)
			{
				case L2Item.CRYSTAL_A:
					return new int[] { 730, 6570, 732, 22013, 22017, 22021, 22224, 17256, 22318 };
				case L2Item.CRYSTAL_B:
					return new int[] { 948, 6572, 950, 22012, 22016, 22020, 22226, 17258, 22319 };
				case L2Item.CRYSTAL_C:
					return new int[] { 952, 6574, 954, 22011, 22228, 17260 };
				case L2Item.CRYSTAL_D:
					return new int[] { 956, 6576, 958, 22010, 22230, 17262 };
				case L2Item.CRYSTAL_S:
					return new int[] { 960, 6578, 962, 20518, 20520, 20522, 22222, 17264, 22315, 32416, 22429 };
			}
		return new int[0];
	}

	private static final int[][] catalyst = {
			// enchant catalyst list
			{ 12362, 14078, 14702 }, // 0 - W D
			{ 12363, 14079, 14703 }, // 1 - W C
			{ 12364, 14080, 14704 }, // 2 - W B
			{ 12365, 14081, 14705 }, // 3 - W A
			{ 12366, 14082, 14706 }, // 4 - W S
			{ 12367, 14083, 14707 }, // 5 - A D
			{ 12368, 14084, 14708 }, // 6 - A C
			{ 12369, 14085, 14709 }, // 7 - A B
			{ 12370, 14086, 14710 }, // 8 - A A
			{ 12371, 14087, 14711 }, // 9 - A S
	};

	public int[] getEnchantCatalystId()
	{
		if(_itemTemplate.getType2() == L2Item.TYPE2_WEAPON)
			switch(_itemTemplate.getCrystalType().cry)
			{
				case L2Item.CRYSTAL_A:
					return catalyst[3];
				case L2Item.CRYSTAL_B:
					return catalyst[2];
				case L2Item.CRYSTAL_C:
					return catalyst[1];
				case L2Item.CRYSTAL_D:
					return catalyst[0];
				case L2Item.CRYSTAL_S:
					return catalyst[4];
			}
		else if(_itemTemplate.getType2() == L2Item.TYPE2_SHIELD_ARMOR || _itemTemplate.getType2() == L2Item.TYPE2_ACCESSORY)
			switch(_itemTemplate.getCrystalType().cry)
			{
				case L2Item.CRYSTAL_A:
					return catalyst[8];
				case L2Item.CRYSTAL_B:
					return catalyst[7];
				case L2Item.CRYSTAL_C:
					return catalyst[6];
				case L2Item.CRYSTAL_D:
					return catalyst[5];
				case L2Item.CRYSTAL_S:
					return catalyst[9];
			}
		return new int[] { 0, 0, 0 };
	}

	public int getCatalystPower()
	{
		switch(_itemId)
		{
			case 12362:
			case 14078:
			case 14702:
				return 20;
			case 12363:
			case 14079:
			case 14703:
				return 18;
			case 12364:
			case 14080:
			case 14704:
				return 15;
			case 12365:
			case 14081:
			case 14705:
				return 12;
			case 12366:
			case 14082:
			case 14706:
				return 10;
			case 12367:
			case 14083:
			case 14707:
				return 35;
			case 12368:
			case 14084:
			case 14708:
				return 27;
			case 12369:
			case 14085:
			case 14709:
				return 23;
			case 12370:
			case 14086:
			case 14710:
				return 18;
			case 12371:
			case 14087:
			case 14711:
				return 15;
			default:
				return 0;
		}
	}

	/**
	 * Return true if item is hero-item
	 * @return boolean
	 */
	public boolean isHeroWeapon()
	{
		return _itemTemplate.isHeroWeapon();
	}

	/**
	 * Return true if item is ClanApella-item
	 * @return boolean
	 */
	public boolean isClanApellaItem()
	{
		return _itemId >= 7860 && _itemId <= 7879 || _itemId >= 9830 && _itemId <= 9839;
	}

	public boolean isLifeStone()
	{
		return _itemId >= 8723 && _itemId <= 8762 || _itemId >= 9573 && _itemId <= 9576 || _itemId >= 10483 && _itemId <= 10486 || _itemId >= 14166 && _itemId <= 14169 || _itemId >= 16160 && _itemId <= 16167;
	}

	public boolean isAccessoryLifeStone()
	{
		return _itemId >= 12754 && _itemId <= 12763 || _itemId >= 12840 && _itemId <= 12851 || _itemId == 12821 || _itemId == 12822 || _itemId == 14008 || _itemId == 16177 || _itemId == 16178;
	}

	/**
	 * Return true if item can be destroyed
	 */
	public boolean canBeDestroyed(L2Player player)
	{
		if(player.isGM())
			return true;
		else if((_customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY)
			return false;
		else if(isHeroWeapon() || _visual_item_id > 0 || _visual_enchant_level > 0)
			return false;
		else if(_is_event > -1)
			return false;
		else if(PetDataTable.isPetControlItem(this) && player.isMounted())
			return false;
		else if(player.getPet() != null && getObjectId() == player.getPet().getControlItemObjId())
			return false;
		else if(isCursed())
			return false;
		//else if(isEquipped())
		//	return false;
		else if(isWear())
			return false;
		else if(player.getInventory().isLockedItem(this))
			return false;
		return isDestroyable();
	}

	/**
	 * Return true if item can be dropped
	 */
	public boolean canBeDropped(L2Player player, boolean pk)
	{
		boolean IgnorUnTradebleDurable = Util.contains_int(ConfigValue.IgnorUnTradebleDurable, _itemId);
		if(player.isGM() || IgnorUnTradebleDurable)
			return true;
		else if(ConfigValue.CanNotByTradeItem)
			return Util.contains_int(ConfigValue.CanNotByTradeItems, _itemId);
		else if((_customFlags & FLAG_NO_DROP) == FLAG_NO_DROP)
			return false;
		else if(isHeroWeapon())
			return false;
		else if(isShadowItem() && !IgnorUnTradebleDurable || _visual_item_id > 0 || _visual_enchant_level > 0)
			return false;
		else if(isTemporalItem() && !IgnorUnTradebleDurable)
			return false;
		else if(isAugmented() && (pk && !ConfigValue.DropAugmented || !ConfigValue.AllowDropAugmented))
			return false;
		else if(_itemTemplate.getType2() == L2Item.TYPE2_QUEST)
			return false;
		else if(PetDataTable.isPetControlItem(this) && player.isMounted())
			return false;
		else if(player.getPet() != null && getObjectId() == player.getPet().getControlItemObjId())
			return false;
		else if(isCursed() || getItem().isCombatFlag() || getItem().isTerritoryFlag())
			return false;
		else if(!pk && isEquipped())
			return false;
		else if(isWear())
			return false;
		else if(getItem().getType2() == L2Item.TYPE2_QUEST)
			return false;
		else if(player.getEnchantScroll() == this)
			return false;
		else if(player.getInventory().isLockedItem(this))
			return false;
		return _itemTemplate.isDropable();
	}

	public boolean isNoTrd()
	{
		if(Util.contains_int(ConfigValue.CanNotByTradeItemPA, _itemId))
			return true;
		return false;
	}

	public boolean isTrd()
	{
		if(ConfigValue.CanByTradeItemsPA.length == 0 || Util.contains_int(ConfigValue.CanByTradeItemsPA, _itemId))
			return true;
		return false;
	}

	public boolean canBeTraded(L2Player owner)
	{
		boolean can_by_trade_item = owner.getBonus().CanByTradeItemPA;
		boolean IgnorUnTradebleDurable = Util.contains_int(ConfigValue.IgnorUnTradebleDurable, _itemId);

		if(owner.isGM() || IgnorUnTradebleDurable)
			return true;
		else if(ConfigValue.CanNotByTradeItem)
			return Util.contains_int(ConfigValue.CanNotByTradeItems, _itemId);
		else if((_customFlags & FLAG_NO_TRADE) == FLAG_NO_TRADE || getItem().getType2() == L2Item.TYPE2_QUEST || isHeroWeapon() || _visual_item_id > 0 || _visual_enchant_level > 0 || isNoTrd() || isCursed() || isEquipped() || isWear() || owner.getEnchantScroll() == this)
			return false;
		else if((isTemporalItem() || isShadowItem()) && (!can_by_trade_item || ConfigValue.CanByTradeItemsPA.length > 0) && !IgnorUnTradebleDurable)
			return false;
		else if(isAugmented() && !ConfigValue.AllowDropAugmented && !can_by_trade_item)
			return false;
		else if(PetDataTable.isPetControlItem(this) && owner.isMounted() || owner.getPet() != null && getObjectId() == owner.getPet().getControlItemObjId())
			return false;
		else if(owner.getInventory().isLockedItem(this))
			return false;
		else if(can_by_trade_item && getItem().isPvP() && ConfigValue.CanByTradePvpItem)
			return true;
		return (_itemTemplate.isTradeable() || can_by_trade_item && isTrd());
	}

	/**
	 * Можно ли положить на клановый склад или передать фрейтом
	 */
	public boolean canBeStored(L2Player player, boolean privatewh)
	{
		boolean IgnorUnTradebleDurable = Util.contains_int(ConfigValue.IgnorUnTradebleDurable, _itemId);

		if(player.isGM() || IgnorUnTradebleDurable)
			return true;
		else if(!privatewh && ConfigValue.CanNotByTradeItem)
			return Util.contains_int(ConfigValue.CanNotByTradeItems, _itemId);
		else if((_customFlags & FLAG_NO_TRANSFER) == FLAG_NO_TRANSFER)
			return false;
		else if(!getItem().isStoreable() || _visual_item_id > 0 || _visual_enchant_level > 0)
			return false;
		else if(!privatewh && !IgnorUnTradebleDurable && (isShadowItem() || isTemporalItem()))
			return false;
		else if(PetDataTable.isPetControlItem(this) && player.isMounted())
			return false;
		else if(player.getPet() != null && getObjectId() == player.getPet().getControlItemObjId())
			return false;
		else if(!privatewh && isAugmented() && !ConfigValue.AllowDropAugmented)
			return false;
		else if(isCursed())
			return false;
		else if(isEquipped())
			return false;
		else if(isWear())
			return false;
		else if(getItem().getType2() == L2Item.TYPE2_QUEST)
			return false;
		else if(player.getEnchantScroll() == this)
			return false;
		else if(player.getInventory().isLockedItem(this))
			return false;
		return privatewh || _itemTemplate.isTradeable();
	}

	public boolean canBeCrystallized(L2Player player, boolean msg)
	{
		if((_customFlags & FLAG_NO_CRYSTALLIZE) == FLAG_NO_CRYSTALLIZE)
			return false;
		else if(isHeroWeapon())
			return false;
		else if(isShadowItem())
			return false;
		else if(isTemporalItem())
			return false;

		//can player crystallize?
		int level = player.getSkillLevel(L2Skill.SKILL_CRYSTALLIZE);
		if(level < 1 || _itemTemplate.getCrystalType().cry - L2Item.CRYSTAL_D + 1 > level)
		{
			if(msg)
			{
				player.sendPacket(Msg.CANNOT_CRYSTALLIZE_CRYSTALLIZATION_SKILL_LEVEL_TOO_LOW);
				player.sendActionFailed();
			}
			return false;
		}
		else if(PetDataTable.isPetControlItem(this) && player.isMounted())
			return false;
		else if(player.getPet() != null && getObjectId() == player.getPet().getControlItemObjId())
			return false;
		else if(isCursed())
			return false;
		else if(isEquipped())
			return false;
		else if(isWear())
			return false;
		else if(getItem().getType2() == L2Item.TYPE2_QUEST)
			return false;
		else if(player.getInventory().isLockedItem(this))
			return false;
		return _itemTemplate.isCrystallizable();
	}

	public boolean canBeEnchanted()
	{
		boolean IgnorUnEnchantDurable = Util.contains_int(ConfigValue.IgnorUnEnchantDurable, _itemId);

		if((_customFlags & FLAG_NO_ENCHANT) == FLAG_NO_ENCHANT || (isShadowItem() || isTemporalItem()) && !IgnorUnEnchantDurable || isWear() || getOwner() != null && getOwner().getInventory().isLockedItem(this))
			return false;
		return _itemTemplate.canBeEnchanted();
	}

	public boolean canBeAugmented(L2Player player, boolean isAccessoryLifeStone)
	{
		if(!canBeEnchanted())
			return false;
		else if(isAugmented())
			return false;
		else if(isRaidAccessory() || getItemId() == 13752 || getItemId() == 13753 || getItemId() == 13754)
			return false;
		else if(getItem().getItemGrade().ordinal() < Grade.C.ordinal())
			return false;
		else if(getItemId() >= 14801 && getItemId() <= 14809 || getItemId() >= 15282 && getItemId() <= 15299)
			return false; // бижутерия с ТВ
		int itemType = getItem().getType2();
		if((isAccessoryLifeStone ? itemType != L2Item.TYPE2_ACCESSORY : itemType != L2Item.TYPE2_WEAPON) && !ConfigValue.AugmentAll)
			return false;
		else if(player.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE || player.isDead() || player.isParalyzed() || player.isFishing() || player.isSitting())
			return false;
		return true;
	}

	public boolean isRaidAccessory()
	{
		return _itemTemplate.isRaidAccessory();
	}

	/**
	 * Returns the item in String format
	 */
	@Override
	public String toString()
	{
		//return _itemTemplate.toString();
		return "["+getName()+"]["+getItemId()+"]["+_owner+"]["+_ownerId+"]["+getObjectId()+"]["+_loc+"]";
	}

	public boolean isNightLure()
	{
		return _itemId >= 8505 && _itemId <= 8513 || _itemId == 8485;
	}

	/**
	 * Используется только для Shadow вещей
	 */
	public void shadowNotify(boolean equipped)
	{
		if(!isShadowItem()) // Вещь не теневая? До свидания.
			return;

		if(!equipped) // При снятии прерывать таск
		{
			if(_itemLifeTimeTask != null)
				_itemLifeTimeTask.cancel(false);
			_itemLifeTimeTask = null;
			return;
		}

		if(_itemLifeTimeTask != null && !_itemLifeTimeTask.isDone()) // Если таск уже тикает, то повторно дергать не надо
			return;

		L2Player owner = getOwner();
		if(owner == null)
			return;

		setLifeTimeRemaining(owner, getLifeTimeRemaining() - 1);

		if(!checkDestruction(owner)) // Если у вещи ещё есть мана - запустить таск уменьшения
			_itemLifeTimeTask = ThreadPoolManager.getInstance().schedule(new LifeTimeTask(), 60000);
	}

	public void startTemporalTask(L2Player owner)
	{
		if(!isTemporalItem() || owner == null) // Вещь не временная? До свидания.
			return;

		if(_itemLifeTimeTask != null && !_itemLifeTimeTask.isDone()) // Если таск уже тикает, то повторно дергать не надо
			return;

		if(!checkDestruction(owner)) // Если у вещи ещё есть мана - запустить таск уменьшения
			_itemLifeTimeTask = ThreadPoolManager.getInstance().schedule(new LifeTimeTask(), 60000);
	}

	public boolean isShadowItem()
	{
		return _itemTemplate.isShadowItem();
	}

	public boolean isTemporalItem()
	{
		return _temporal;
	}

	public boolean isCommonItem()
	{
		return _itemTemplate.isCommonItem();
	}

	public boolean isAltSeed()
	{
		return _itemTemplate.isAltSeed();
	}

	public boolean isCursed()
	{
		return _itemTemplate.isCursed();
	}

	private L2Player getOwner()
	{
		return _owner;
	}

	/**
	 * true означает завершить таск, false продолжить
	 */
	private boolean checkDestruction(L2Player owner)
	{
		if(!isShadowItem() && !isTemporalItem())
			return true;

		int left = getLifeTimeRemaining();
		if(isTemporalItem())
			left /= 60;
		if(left == 10 || left == 5 || left == 1 || left <= 0)
		{
			if(isShadowItem())
			{
				SystemMessage sm;
				if(left == 10)
					sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_10);
				else if(left == 5)
					sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_5);
				else if(left == 1)
					sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON);
				else
					sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
				sm.addItemName(getItemId());
				owner.sendPacket(sm);
			}

			if(left <= 0)
			{
				owner.getInventory().unEquipItem(this);
				owner.getInventory().destroyItem(this, getCount(), true);
				if(isTemporalItem())
					owner.sendPacket(new SystemMessage(SystemMessage.THE_LIMITED_TIME_ITEM_HAS_BEEN_DELETED).addItemName(_itemTemplate.getItemId()));
				owner.sendPacket(new ItemList(owner, false)); // перестраховка
				owner.broadcastUserInfo(true);
				return true;
			}
		}

		return false;
	}

	public int getLifeTimeRemaining()
	{
		if(isTemporalItem())
			return _lifeTimeRemaining - (int) (System.currentTimeMillis() / 1000);
		return _lifeTimeRemaining;
	}

	public void setLifeTimeRemaining(L2Player owner, int lt)
	{
		//assert !isTemporalItem();

		_lifeTimeRemaining = lt;
		_storedInDb = false;

		owner.sendPacket(new InventoryUpdate().addModifiedItem(this));
	}

	public class LifeTimeTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2Player owner = getOwner();
			if(owner == null || !owner.isOnline())
				return;

			if(isShadowItem())
			{
				if(!isEquipped())
					return;

				setLifeTimeRemaining(owner, getLifeTimeRemaining() - 1);
			}

			if(checkDestruction(owner))
				return;

			_itemLifeTimeTask = ThreadPoolManager.getInstance().schedule(this, 60000); // У шэдовов 1 цикл = 60 сек.
		}
	}

	public void dropToTheGround(L2Player lastAttacker, L2NpcInstance dropper)
	{
		if(dropper == null)
		{
			Location dropPos = Rnd.coordsRandomize(lastAttacker, 70);
			for(int i = 0; i < 20 && !GeoEngine.canMoveWithCollision(lastAttacker.getX(), lastAttacker.getY(), lastAttacker.getZ(), dropPos.x, dropPos.y, dropPos.z, getReflection().getGeoIndex()); i++)
				dropPos = Rnd.coordsRandomize(lastAttacker, 70);
			dropMe(lastAttacker, dropPos);
			if(ConfigValue.AutoDestroyDroppedItemAfter > 0 && !isCursed() && _is_event == -1)
				ItemsAutoDestroy.getInstance().addItem(this);
			return;
		}

		// 20 попыток уронить дроп в точке смерти моба
		Location dropPos = Rnd.coordsRandomize(dropper, 70);
		if(lastAttacker != null)
		{
			for(int i = 0; i < 20 && !GeoEngine.canMoveWithCollision(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, getReflection().getGeoIndex()); i++)
				dropPos = Rnd.coordsRandomize(dropper, 70);

			// Если в точке смерти моба дропу негде упасть, то падает под ноги чару
			if(!GeoEngine.canMoveWithCollision(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, getReflection().getGeoIndex()))
				dropPos = lastAttacker.getLoc();
		}

		// Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
		dropMe(dropper, dropPos);

		// Add drop to auto destroy item task
		if(isHerb())
			ItemsAutoDestroy.getInstance().addHerb(this);
		else if(ConfigValue.AutoDestroyDroppedItemAfter > 0 && !isCursed())
			ItemsAutoDestroy.getInstance().addItem(this);

		// activate non owner penalty
		if(lastAttacker != null) // lastAttacker в данном случае top damager
			setItemDropOwner(lastAttacker, (ConfigValue.NonOwnerItemPickupDelay * 1000) + (dropper.isRaid() || dropper.isEpicRaid() || dropper.isBoss() || dropper.isRefRaid() ? 285000 : 0));
	}

	/**
	 * Бросает вещь на землю туда, где ее можно поднять
	 */
	public void dropToTheGround(L2Character dropper, Location dropPos)
	{
		if(GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, getReflection().getGeoIndex()))
			dropMe(dropper, dropPos);
		else
			dropMe(dropper, dropper.getLoc());

		// Add drop to auto destroy item task
		if(isHerb())
			ItemsAutoDestroy.getInstance().addHerb(this);
		else if(ConfigValue.AutoDestroyPlayerDroppedItemAfter > 0)
			ItemsAutoDestroy.getInstance().addItemPlayer(this);
	}

	public boolean isDestroyable()
	{
		return _itemTemplate.isDestroyable();
	}

	public ItemClass getItemClass()
	{
		return _itemTemplate.getItemClass();
	}

	public void setItemId(int id)
	{
		_itemId = id;
		_itemTemplate = ItemTemplates.getInstance().getTemplate(id);
		_storedInDb = false;
	}

	public void setVisualItemId(int id)
	{
		_visual_item_id = id;
		_storedInDb = false;
		PlayerData.getInstance().updateInDb(this);
	}

	/**
	 * Возвращает защиту от элемента: огонь.
	 * @return значение защиты
	 */
	public int getDefenceFire()
	{
		return _itemTemplate instanceof L2Armor ? defenseAttributes[0] : 0;
	}

	/**
	 * Возвращает защиту от элемента: вода.
	 * @return значение защиты
	 */
	public int getDefenceWater()
	{
		return _itemTemplate instanceof L2Armor ? defenseAttributes[1] : 0;
	}

	/**
	 * Возвращает защиту от элемента: воздух.
	 * @return значение защиты
	 */
	public int getDefenceWind()
	{
		return _itemTemplate instanceof L2Armor ? defenseAttributes[2] : 0;
	}

	/**
	 * Возвращает защиту от элемента: земля.
	 * @return значение защиты
	 */
	public int getDefenceEarth()
	{
		return _itemTemplate instanceof L2Armor ? defenseAttributes[3] : 0;
	}

	/**
	 * Возвращает защиту от элемента: свет.
	 * @return значение защиты
	 */
	public int getDefenceHoly()
	{
		return _itemTemplate instanceof L2Armor ? defenseAttributes[4] : 0;
	}

	/**
	 * Возвращает защиту от элемента: тьма.
	 * @return значение защиты
	 */
	public int getDefenceUnholy()
	{
		return _itemTemplate instanceof L2Armor ? defenseAttributes[5] : 0;
	}

	/**
	 * Возвращает элемент атрибуции предмета.<br>
	 * Element (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
	 * @return id элемента
	 */
	public byte getAttackAttributeElement()
	{
		if(_enchantAttributeFuncTemplate == null || _enchantAttributeFuncTemplate.get(0) == null)
			return L2Item.ATTRIBUTE_NONE;
		return getEnchantAttributeByStat(_enchantAttributeFuncTemplate.get(0)._stat);
	}

	public int[] getAttackElementAndValue()
	{
		if(_enchantAttributeFuncTemplate == null && _itemTemplate instanceof L2Weapon)
			return new int[] { L2Item.ATTRIBUTE_NONE, 0 };
		return new int[] { attackAttributeElement, attackAttributeValue };
	}

	public byte getAttackElement()
	{
		return attackAttributeElement;
	}

	public int getAttackElementValue()
	{
		return attackAttributeValue;
	}

	public byte[] getArmorAttributeLevel()
	{
		byte[] levels = new byte[] { 0, 0, 0, 0, 0, 0 };
		for(int i = 0; i < getDeffAttr().length; i++)
		{
			levels[i] = getArmorAttributeLevel(getDeffAttr()[i]);
		}
		return levels;
	}

	public int[] getDeffAttr()
	{
		return defenseAttributes;
	}

	public int getElementDefAttr(byte element)
	{
		return isArmor() ? defenseAttributes[element] : 0;
	}

	/**
	* Возвращает значение элемента атрибуции предмета
	* @return сила элемента
	*/

	public List<FuncTemplate> getAttributeFuncTemplate()
	{
		return _enchantAttributeFuncTemplate;
	}

	/**
	 * Устанавливает элемент атрибуции предмета.<br>
	 * Element (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
	 * @param element элемент
	 */
	public void setAttributeElement(byte element, int value, int[] deffAttr, boolean updateDb)
	{
		Stats stat = getStatByEnchantAttribute(element);

		if(isWeapon())
		{
			if(stat == null || value == 0)
			{
				if(_enchantAttributeFuncTemplate != null)
					detachFunction(_enchantAttributeFuncTemplate.get(0));
				_enchantAttributeFuncTemplate = null;
				attackAttributeElement = L2Item.ATTRIBUTE_NONE;
				attackAttributeValue = 0;
				if(updateDb)
					PlayerData.getInstance().updateItemAttributes(this);
				return;
			}
			else if(_enchantAttributeFuncTemplate == null || _enchantAttributeFuncTemplate.get(0) == null)
			{
				_enchantAttributeFuncTemplate = new ArrayList<FuncTemplate>(1);
				_enchantAttributeFuncTemplate.add(new FuncTemplate(null, "Add", stat, 0x40, value));
				attachFunction(_enchantAttributeFuncTemplate.get(0));
			}
			else
			{
				// TODO: !!!!!!
				_enchantAttributeFuncTemplate.get(0)._stat = stat;
				_enchantAttributeFuncTemplate.get(0)._value = value;
			}
			defenseAttributes = new int[] { 0, 0, 0, 0, 0, 0 };
			attackAttributeElement = element;
			attackAttributeValue = value;
		}
		else if(isArmor() || isAccessory())
		{
			for(int i = 0; i < 6; i++)
			{
				stat = getStatByEnchantAttribute((byte) i);
				if(deffAttr[i] == 0 && _enchantAttributeFuncTemplate != null && _enchantAttributeFuncTemplate.get(i) != null)
				{ //удалили элемент, но функция осталась
					detachFunction(_enchantAttributeFuncTemplate.get(i));
					_enchantAttributeFuncTemplate.set(i, null);
				}
				else if((deffAttr[i] > 0 && _enchantAttributeFuncTemplate != null && _enchantAttributeFuncTemplate.get(i) == null) || (deffAttr[i] > 0 && _enchantAttributeFuncTemplate == null))
				{ //добавили новый элемент, добавляем функцию
					if(_enchantAttributeFuncTemplate == null)
					{
						_enchantAttributeFuncTemplate = new ArrayList<FuncTemplate>(6);
						for(int i2 = 0; i2 < 6; i2++)
							_enchantAttributeFuncTemplate.add(null);
					}
					_enchantAttributeFuncTemplate.set(i, new FuncTemplate(null, "Sub", stat, 0x40, deffAttr[i]));
					attachFunction(_enchantAttributeFuncTemplate.get(i));
				}
				else if(deffAttr[i] > 0 && _enchantAttributeFuncTemplate != null && _enchantAttributeFuncTemplate.get(i) != null)
				{ //функция элемента уже существует, просто добавляем стат
					_enchantAttributeFuncTemplate.get(i)._stat = stat;
					_enchantAttributeFuncTemplate.get(i)._value = deffAttr[i];
				}
			}
			attackAttributeElement = -2;
			attackAttributeValue = 0;
		}
		defenseAttributes = deffAttr;

		if(updateDb)
			PlayerData.getInstance().updateItemAttributes(this);
	}

	public boolean isArmor()
	{
		return getItem().isArmor();
	}

	public boolean isAccessory()
	{
		return getItem().isAccessory();
	}

	public byte getEnchantAttributeByStat(Stats stat)
	{
		switch(stat)
		{
			case ATTACK_ELEMENT_FIRE:
			case FIRE_RECEPTIVE:
				return L2Item.ATTRIBUTE_FIRE;
			case ATTACK_ELEMENT_WATER:
			case WATER_RECEPTIVE:
				return L2Item.ATTRIBUTE_WATER;
			case ATTACK_ELEMENT_EARTH:
			case EARTH_RECEPTIVE:
				return L2Item.ATTRIBUTE_EARTH;
			case ATTACK_ELEMENT_WIND:
			case WIND_RECEPTIVE:
				return L2Item.ATTRIBUTE_WIND;
			case ATTACK_ELEMENT_UNHOLY:
			case UNHOLY_RECEPTIVE:
				return L2Item.ATTRIBUTE_DARK;
			case ATTACK_ELEMENT_SACRED:
			case SACRED_RECEPTIVE:
				return L2Item.ATTRIBUTE_HOLY;
			default:
				return L2Item.ATTRIBUTE_NONE;
		}
	}

	public Stats getStatByEnchantAttribute(byte attribute)
	{
		if(getItem() instanceof L2Weapon)
			switch(attribute)
			{
				case L2Item.ATTRIBUTE_FIRE:
					return Stats.ATTACK_ELEMENT_FIRE;
				case L2Item.ATTRIBUTE_WATER:
					return Stats.ATTACK_ELEMENT_WATER;
				case L2Item.ATTRIBUTE_EARTH:
					return Stats.ATTACK_ELEMENT_EARTH;
				case L2Item.ATTRIBUTE_WIND:
					return Stats.ATTACK_ELEMENT_WIND;
				case L2Item.ATTRIBUTE_DARK:
					return Stats.ATTACK_ELEMENT_UNHOLY;
				case L2Item.ATTRIBUTE_HOLY:
					return Stats.ATTACK_ELEMENT_SACRED;
			}
		else
			switch(attribute)
			{
				case L2Item.ATTRIBUTE_FIRE:
					return Stats.FIRE_RECEPTIVE;
				case L2Item.ATTRIBUTE_WATER:
					return Stats.WATER_RECEPTIVE;
				case L2Item.ATTRIBUTE_EARTH:
					return Stats.EARTH_RECEPTIVE;
				case L2Item.ATTRIBUTE_WIND:
					return Stats.WIND_RECEPTIVE;
				case L2Item.ATTRIBUTE_DARK:
					return Stats.UNHOLY_RECEPTIVE;
				case L2Item.ATTRIBUTE_HOLY:
					return Stats.SACRED_RECEPTIVE;
			}
		return null;
	}

	/**
	 * Возвращает тип элемента для камня атрибуции
	 * @return значение элемента
	 */
	public byte getEnchantAttributeStoneElement(boolean inverse)
	{
		switch(_itemId)
		{
			case 9546:
			case 9552:
			case 10521:
			case 9558:
			case 9564:
				return inverse ? L2Item.ATTRIBUTE_WATER : L2Item.ATTRIBUTE_FIRE;
			case 9547:
			case 9553:
			case 10522:
			case 9559:
			case 9565:
				return inverse ? L2Item.ATTRIBUTE_FIRE : L2Item.ATTRIBUTE_WATER;
			case 9548:
			case 9554:
			case 10523:
			case 9560:
			case 9566:
				return inverse ? L2Item.ATTRIBUTE_WIND : L2Item.ATTRIBUTE_EARTH;
			case 9549:
			case 9555:
			case 10524:
			case 9561:
			case 9567:
				return inverse ? L2Item.ATTRIBUTE_EARTH : L2Item.ATTRIBUTE_WIND;
			case 9550:
			case 9556:
			case 10525:
			case 9562:
			case 9568:
				return inverse ? L2Item.ATTRIBUTE_HOLY : L2Item.ATTRIBUTE_DARK;
			case 9551:
			case 9557:
			case 10526:
			case 9563:
			case 9569:
				return inverse ? L2Item.ATTRIBUTE_DARK : L2Item.ATTRIBUTE_HOLY;
			default:
				return L2Item.ATTRIBUTE_NONE;
		}
	}

	public byte getAttributeElementLevel()
	{
		switch(_itemId)
		{
		/*Stone*/
			case 9546:
			case 9547:
			case 9548:
			case 9549:
			case 9550:
			case 9551:
			case 10521:
			case 10522:
			case 10523:
			case 10524:
			case 10525:
			case 10526:
				return 3;
				/*Crystals*/
			case 9552:
			case 9557:
			case 9555:
			case 9554:
			case 9553:
			case 9556:
				return 6;
				/*Jewels*/
			case 9558:
			case 9563:
			case 9561:
			case 9560:
			case 9562:
			case 9559:
				return 9;
				/*Energy*/
			case 9567:
			case 9566:
			case 9568:
			case 9565:
			case 9564:
			case 9569:
				return 12;
		}
		return -1;
	}

	public byte getWeaponElementLevel()
	{
		int val = getAttackElementValue();
		if(!isWeapon() || val == 0)
			return 0;
		if(val > 0 && val < 25)
			return 1;
		else if(val >= 25 && val < 75)
			return 2;
		else if(val >= 75 && val < 150)
			return 3;
		else if(val >= 150 && val < 175)
			return 4;
		else if(val >= 175 && val < 225)
			return 5;
		else if(val >= 225 && val < 300)
			return 6;
		else if(val >= 300 && val < 325)
			return 7;
		else if(val >= 325 && val < 375)
			return 8;
		else if(val >= 375 && val < 450)
			return 9;
		else if(val >= 450 && val < 475)
			return 10;
		else if(val >= 475 && val < 525)
			return 11;
		else if(val >= 525 && val < 600)
			return 12;
		else if(val >= 600)
			return 13;
		return 0;
	}

	private byte getArmorAttributeLevel(int val)
	{
		if(!isArmor() || val == 0)
			return 0;
		if(val > 0 && val < 12)
			return 1;
		else if(val >= 12 && val < 30)
			return 2;
		else if(val >= 30 && val < 60)
			return 3;
		else if(val >= 60 && val < 72)
			return 4;
		else if(val >= 72 && val < 90)
			return 5;
		else if(val >= 90 && val < 120)
			return 6;
		else if(val >= 120 && val < 132)
			return 7;
		else if(val >= 132 && val < 150)
			return 8;
		else if(val >= 150 && val < 180)
			return 9;
		else if(val >= 180 && val < 192)
			return 10;
		else if(val >= 192 && val < 210)
			return 11;
		else if(val >= 210 && val < 240)
			return 12;
		else if(val >= 240)
			return 13;
		return 0;
	}

	public boolean isAttributeCrystal()
	{
		return _itemId == 9552 || _itemId == 9553 || _itemId == 9554 || _itemId == 9555 || _itemId == 9556 || _itemId == 9557;
	}

	public boolean isAttributeJewel()
	{
		return _itemId == 9558 || _itemId == 9559 || _itemId == 9560 || _itemId == 9561 || _itemId == 9562 || _itemId == 9563;
	}

	public boolean isAttributeEnergy()
	{
		return _itemId == 9564 || _itemId == 9565 || _itemId == 9566 || _itemId == 9567 || _itemId == 9568 || _itemId == 9569;
	}

	/**
	 * Проверяет, является ли данный инстанс предмета хербом
	 * @return true если предмет является хербом
	 */
	public boolean isHerb()
	{
		return getItem().isHerb();
	}

	public Grade getCrystalType()
	{
		return _itemTemplate.getCrystalType();
	}

	public void setCustomFlags(int i, boolean updateDb)
	{
		if(_customFlags != i)
		{
			_customFlags = i;
			if(updateDb)
				updateDatabase();
			else
				_storedInDb = false;
		}
	}

	public int getCustomFlags()
	{
		return _customFlags;
	}

	@Override
	public String getName()
	{
		return getItem().getName();
	}

	public boolean isWeapon()
	{
		return getItem().isWeapon();
	}

	private int[] _enchantOptions = new int[3];

	public int[] getEnchantOptions()
	{
		return _enchantOptions;
	}

	public int getAgathionEnergy()
	{
		return _agathionEnergy;
	}

	public void setAgathionEnergy(int agathionEnergy)
	{
		_agathionEnergy = agathionEnergy;
	}

	@Override
	public boolean isItem()
	{
		return true;
	}

	public class EnchantDecrise extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2Player owner = getOwner();

			lock.lock();
			try
			{
				_enchant_time = 0;
				if(_enchantLevel > (isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor))
				{
					if(owner != null && isEquipped())
						owner.getInventory().refreshListeners(L2ItemInstance.this, _enchantLevel-1);
					else
					{
						setEnchantLevel(_enchantLevel-1);
						if(owner != null)
							owner.sendPacket(new InventoryUpdate().addModifiedItem(L2ItemInstance.this));
					}
				}
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	@Override
	public void deleteMe()
	{
		//_log.info("L2ItemInstance: deleteMe: "+this);
		//Util.test();
		/*if(_enchant_timer != null)
		{
			_enchant_timer.cancel(false);
			_enchant_timer = null;
		}*/
		super.deleteMe();
	}

	public boolean pickupMe(L2Character target)
	{
		// Create a server->client GetItem packet to pick up the L2ItemInstance
		//player.broadcastPacket(new GetItem((L2ItemInstance) this, player.getObjectId()));

		// if this item is a mercenary ticket, remove the spawns!
		if(isItem())
		{
			int itemId = getItemId();
			/*if(itemId >= 3960 && itemId <= 3972 // Gludio
			 || itemId >= 3973 && itemId <= 3985 // Dion
			 || itemId >= 3986 && itemId <= 3998 // Giran
			 || itemId >= 3999 && itemId <= 4011 // Oren
			 || itemId >= 4012 && itemId <= 4026 // Aden
			 || itemId >= 5205 && itemId <= 5215 // Innadril
			 || itemId >= 6779 && itemId <= 6833 // Goddard
			 || itemId >= 7973 && itemId <= 8029 // Rune
			 || itemId >= 7918 && itemId <= 7972 // Schuttgart
			 )*/
			if(itemId >= 3960 && itemId <= 4026 || itemId >= 5205 && itemId <= 5214 || itemId >= 6038 && itemId <= 6306 || itemId >= 6779 && itemId <= 6833 || itemId >= 7918 && itemId <= 8029)
				MercTicketManager.getInstance().removeTicket(this);

			if(target != null && target.isPlayer())
			{
				L2Player player = (L2Player) target;

				if(getItem().isCombatFlag() && !FortressSiegeManager.checkIfCanPickup(player))
					return false;
				if(getItem().isTerritoryFlag())
					return false;

				if(itemId == 57 || itemId == 6353)
				{
					Quest q = QuestManager.getQuest(255);
					if(q != null)
						player.processQuestEvent(q.getName(), "CE" + itemId, null);
				}

				if(player.getEventMaster() != null && !player.getEventMaster().canPickupItem(player, this))
					return false;
			}
			else if(getItem().isCombatFlag())
				return false;
		}

		// Remove the L2ItemInstance from the world
		_hidden = true;
		L2World.removeVisibleObject(this);

		return true;
	}

	public boolean _not_save=false;
	public void notSave()
	{
		_not_save=true;
	}
}