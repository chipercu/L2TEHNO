package l2open.gameserver.templates;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.skills.StatTemplate;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.skills.triggers.TriggerInfo;
import l2open.gameserver.skills.triggers.TriggerType;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2EtcItem.EtcItemType;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Util;

import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class contains all informations concerning the item (weapon, armor, etc).<BR>
 * Mother class of :
 * <LI>L2Armor</LI>
 * <LI>L2EtcItem</LI>
 * <LI>L2Weapon</LI>
 */
public abstract class L2Item extends StatTemplate
{
	private static final Logger _log = Logger.getLogger(L2Item.class.getName());
	/**
	 * Pc Cafe Bang Points item id. Используется на корейских серверах, но английский клиент в состоянии
	 * поддерживать даный функционал.
	 */
	public static final short ITEM_ID_PC_BANG_POINTS = -100;
	/** Item ID для клановой репутации */
	public static final short ITEM_ID_CLAN_REPUTATION_SCORE = -200;
	public static final short ITEM_ID_FAME = -300;
	public static final short field_cycle_point_id3 = -400;
	public static final short PVP_COIN = -500;
	public static final short ITEM_ID_OWER_POINT = 29600;
	public static final short ITEM_ID_RAID_POINT = 29500;
	public static final int ITEM_ID_ADENA = 57;

	public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
	public static final int TYPE1_SHIELD_ARMOR = 1;
	public static final int TYPE1_OTHER = 2;
	public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;

	public static final byte TYPE2_WEAPON = 0;
	public static final byte TYPE2_SHIELD_ARMOR = 1;
	public static final byte TYPE2_ACCESSORY = 2;
	public static final byte TYPE2_QUEST = 3;
	public static final byte TYPE2_MONEY = 4;
	public static final byte TYPE2_OTHER = 5;
	public static final byte TYPE2_PET_WOLF = 6;
	public static final byte TYPE2_PET_HATCHLING = 7;
	public static final byte TYPE2_PET_STRIDER = 8;
	public static final byte TYPE2_NODROP = 9;
	public static final byte TYPE2_PET_GWOLF = 10;
	public static final byte TYPE2_PENDANT = 11;
	public static final byte TYPE2_PET_BABY = 12;

	public static final int SLOT_NONE = 0x00000;
	public static final int SLOT_UNDERWEAR = 0x00001;
	public static final int SLOT_CLOAK = 0x0003; //TODO:????

	public static final int SLOT_R_EAR = 0x00002;
	public static final int SLOT_L_EAR = 0x00004;

	public static final int SLOT_NECK = 0x00008;

	public static final int SLOT_R_FINGER = 0x00010;
	public static final int SLOT_L_FINGER = 0x00020;

	public static final int SLOT_HEAD = 0x00040;
	public static final int SLOT_R_HAND = 0x00080;
	public static final int SLOT_L_HAND = 0x00100;
	public static final int SLOT_GLOVES = 0x00200;
	public static final int SLOT_CHEST = 0x00400;
	public static final int SLOT_LEGS = 0x00800;
	public static final int SLOT_FEET = 0x01000;
	public static final int SLOT_BACK = 0x02000;
	public static final int SLOT_LR_HAND = 0x04000;
	public static final int SLOT_FULL_ARMOR = 0x08000;
	public static final int SLOT_HAIR = 0x10000;
	public static final int SLOT_FORMAL_WEAR = 0x20000;
	public static final int SLOT_DHAIR = 0x40000;
	public static final int SLOT_HAIRALL = 0x80000;
	public static final int SLOT_R_BRACELET = 0x100000;
	public static final int SLOT_L_BRACELET = 0x200000;
	public static final int SLOT_DECO = 0x400000;
	public static final int SLOT_SIGIL = 0x000000; // TODO: fix
	public static final int SLOT_BELT = 0x10000000;
	public static final int SLOT_WOLF = -100;
	public static final int SLOT_HATCHLING = -101;
	public static final int SLOT_STRIDER = -102;
	public static final int SLOT_BABYPET = -103;
	public static final int SLOT_GWOLF = -104;
	public static final int SLOT_PENDANT = -105;

	public static final int CRYSTAL_NONE = 0;
	public static final int CRYSTAL_D = 1458;
	public static final int CRYSTAL_C = 1459;
	public static final int CRYSTAL_B = 1460;
	public static final int CRYSTAL_A = 1461;
	public static final int CRYSTAL_S = 1462;

	// Все слоты, используемые броней.
	public static final int SLOTS_ARMOR = SLOT_HEAD | SLOT_L_HAND | SLOT_GLOVES | SLOT_CHEST | SLOT_LEGS | SLOT_FEET | SLOT_BACK | SLOT_FULL_ARMOR;
	// Все слоты, используемые бижей.
	public static final int SLOTS_JEWELRY = SLOT_R_EAR | SLOT_L_EAR | SLOT_NECK | SLOT_R_FINGER | SLOT_L_FINGER;

	private static final int[] crystalEnchantBonusArmor = { 0, 11, 6, 11, 19, 25, 25, 25 };
	private static final int[] crystalEnchantBonusWeapon = { 0, 90, 45, 67, 144, 250, 250, 250 };

	public static enum Grade
	{
		NONE(CRYSTAL_NONE, 0),
		D(CRYSTAL_D, 1),
		C(CRYSTAL_C, 2),
		B(CRYSTAL_B, 3),
		A(CRYSTAL_A, 4),
		S(CRYSTAL_S, 5),
		S80(CRYSTAL_S, 5),
		S84(CRYSTAL_S, 5);

		/** ID соответствующего грейду кристалла */
		public final int cry;
		/** ID грейда, без учета уровня S */
		public final int externalOrdinal;

		private Grade(int crystal, int ext)
		{
			cry = crystal;
			externalOrdinal = ext;
		}
	}

	public static enum Bodypart
	{
		NONE(0x00),
		UNDERWEAR(0x01),
		CLOAK(0x03),
		REAR(0x02),
		LEAR(0x04),
		LREAR(0x06),
		NECK(0x08),
		RFINGER(0x10),
		LFINGER(0x20),
		LRFINGER(0x30),
		HEAD(0x40),
		RHAND(0x80),
		LHAND(0x0100),
		GLOVES(0x0200),
		CHEST(0x0400),
		LEGS(0x0800),
		FEET(0x1000),
		BACK(0x2000),
		LRHAND(0x4000),
		FULLARMOR(0x8000),
		HAIR(0x010000),
		FORMALWEAR(0x020000),
		FACE(0x040000),
		HAIRALL(0x080000),
		RBRACELET(0x100000),
		LBRACELET(0x200000),
		TALISMAN(0x400000),
		SIGIL(0x000000),
		BELT(0x10000000),
		WOLF(-100),
		HATCHLING(-101),
		STRIDER(-102),
		BABY(-103),
		GWOLF(-104),
		PENDANT(-105);

		private final int val;

		private Bodypart(int val)
		{
			this.val = val;
		}

		public int getVal()
		{
			return val;
		}
	}

	public static final byte ATTRIBUTE_NONE = -2;
	public static final byte ATTRIBUTE_FIRE = 0;
	public static final byte ATTRIBUTE_WATER = 1;
	public static final byte ATTRIBUTE_WIND = 2;
	public static final byte ATTRIBUTE_EARTH = 3;
	public static final byte ATTRIBUTE_HOLY = 4;
	public static final byte ATTRIBUTE_DARK = 5;

	protected final int _itemId;
	private final int _itemDisplayId;
	private final ItemClass _class;
	protected final String _name;
	protected final String _addname;
	protected String _icon;
	private final int _type1; // needed for item list (inventory)
	private final int _type2; // different lists for armor, weapon, etc
	private final int _weight;
	private final boolean _crystallizable;
	private final boolean _stackable;
	protected final Grade _crystalType; // default to none-grade
	private final int _flags;
	private final int _durability;
	private boolean _temporal;
	private final int _bodyPart;
	private final int _referencePrice;
	private final short _crystalCount;
	private final boolean _sellable;
	private final boolean _dropable;
	private final boolean _tradeable;
	private final boolean _destroyable;
	private final boolean _storeable;
	private final boolean _isMasterwork;
	private boolean _rare = false;
	private boolean _pvp = false;
	private boolean _sa = false;
	private boolean _common = false;
	public boolean _is_hero = false;
	private boolean _isConsume = false;
	private boolean isOlympiadUse = true;
	private boolean magic_weapon = false;
	private int _reusedelay;
	private final int _triggerSkillId;
	private final int _triggerSkillLvl;
	private final int _triggerChance;
	private final String _triggerType;
	private int _reuseDelayOnEquip = 0;
	public int immediate_effect = 0;
	public int ex_immediate_effect = 0;
	public int delay_share_group = -1;

	private final byte _attAtackType;
	private final int _attAtackValue;
	private final int[] _attDefType = new int[6];
	public boolean _is_premium = false;
	private final int _add_karma;
	private final int _castle;

	protected L2Skill[] _skills;
	protected L2Skill _unequip_skill;
	//protected L2Skill[][] _enchantSkill = new L2Skill[(ConfigValue.EnchantMaxWeapon > ConfigValue.EnchantMaxArmor ? ConfigValue.EnchantMaxWeapon : ConfigValue.EnchantMaxArmor)+1][5]; // Скиллы при достежении определенной заточки.

	protected HashMap<Integer, L2Skill> _enchantSkill = new HashMap<Integer, L2Skill>();

	protected TriggerInfo[] _triggers = null;
	public final Enum type;

	private IntObjectMap<int[]> _enchantOptions = Containers.emptyIntObjectMap();
	protected FuncTemplate[] _funcTemplates;
	private static final Pattern _noskill;
	private final int _agathionEnergy;
	static
	{
		_noskill = Pattern.compile("(^0$)|(^-1$)");
	}

	/**
	 * Constructor<?> of the L2Item that fill class variables.<BR><BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>type</LI>
	 * <LI>_itemId</LI>
	 * <LI>_name</LI>
	 * <LI>_type1 & _type2</LI>
	 * <LI>_weight</LI>
	 * <LI>_crystallizable</LI>
	 * <LI>_stackable</LI>
	 * <LI>_materialType & _crystalType & _crystlaCount</LI>
	 * <LI>_durability</LI>
	 * <LI>_bodypart</LI>
	 * <LI>_referencePrice</LI>
	 * <LI>_sellable</LI>
	 * @param type : Enum designating the type of the item
	 * @param set : StatsSet corresponding to a set of couples (key,value) for description of the item
	 */
	protected L2Item(final Enum<?> type, final StatsSet set)
	{
		this.type = type;
		_itemId = set.getInteger("item_id");
		_itemDisplayId = set.getInteger("display_id", _itemId);
		_class = set.getEnum("class", ItemClass.class, ItemClass.OTHER);// для брони и оружия автоматом проставляется EQUIPMENT, для EtcItem, если не указан, то OTHER
		_name = set.getString("name");
		_addname = set.getString("additional_name", "");
		_icon = set.getString("icon", "");
		_type1 = set.getInteger("type1"); // needed for item list (inventory)
		_type2 = set.getInteger("type2"); // different lists for armor, weapon, etc
		_weight = set.getInteger("weight", 0);
		_stackable = set.getBool("stackable", false);
		_durability = set.getInteger("durability", -1);
		_temporal = set.getBool("temporal", false);
		_bodyPart = set.getEnum("bodypart", L2Item.Bodypart.class, Bodypart.NONE).getVal();
		_referencePrice = ConfigValue.SetAllPrice > -1 ? ConfigValue.SetAllPrice : (int) (set.getInteger("price", 0) * ConfigValue.ItemPraceMod);
		_crystalType = set.getEnum("crystal_type", Grade.class, Grade.NONE); // default to none-grade
		_crystalCount = set.getShort("crystal_count", (short) 0);
		_crystallizable = _crystalCount > 0 && _crystalType != Grade.NONE;
		_dropable = set.getBool("dropable", true);
		_destroyable = set.getBool("destroyable", true);
		_tradeable = set.getBool("tradeable", true);
		_storeable = set.getBool("storeable", true);
		_sellable = set.getBool("sellable", _referencePrice > 0);
		_flags = set.getInteger("flags", 0);
		_rare = set.getBool("isRare", false);
		_pvp = set.getBool("isPvP", false);
		_sa = set.getBool("isSa", false);
		_common = set.getBool("isCommon", false);
		_is_hero = set.getBool("isHero", false);
		_isConsume = set.getBool("isConsume", false);
		_isMasterwork = set.getBool("isMasterworkBody", false);
		_reuseDelayOnEquip = set.getInteger("reuseOnEquip", 0);
		_triggerSkillId = set.getInteger("triger_id", 0);
		_triggerSkillLvl = set.getInteger("triger_level", 1);
		_triggerChance = set.getInteger("triger_chance", 0);
		_triggerType = set.getString("triger_type", "");
		String[] skills = set.getString("skill_id", "0").split(";");
		String[] skilllevels = set.getString("skill_level", "1").split(";");

		_attAtackType = (byte) set.getInteger("att_atack_type", -2);
		_attAtackValue = set.getInteger("att_atack_value", -2);
		String[] attDefType = set.getString("att_def_type", "").split(";");
		_agathionEnergy = set.getInteger("agathion_energy", 0);
		_add_karma = set.getInteger("add_karma", 0);
		_castle = set.getInteger("castle", -1);

		String[] unequip_skill = set.getString("unequip_skill", "").split(":");
		if(unequip_skill.length == 2)
			_unequip_skill = SkillTable.getInstance().getInfo(Integer.parseInt(unequip_skill[0]), Integer.parseInt(unequip_skill[1]));

		if(attDefType.length == 6)
			for(int i = 0; i < 6; i++)
				_attDefType[i] = Integer.parseInt(attDefType[i]);

		UpdateParametr(_itemId);
		try
		{
			for(int i = 0; i < skills.length; i++)
				if(!(_noskill.matcher(skills[i]).matches() || _noskill.matcher(skilllevels[i]).matches()))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(Integer.parseInt(skills[i]), Integer.parseInt(skilllevels[i]));
					if(skill != null)
					{
						if(skill.getSkillType() == SkillType.NOTDONE)
							_log.warning("WARNING: item " + _itemId + " action attached skill not done: " + skill);
						attachSkill(skill);
					}
					else
						_log.warning("WARNING: item " + _itemId + " attached skill not exist: " + skills[i] + " " + skilllevels[i]);
				}

			if(_triggerSkillId > 0)
				attachTrigger(_triggerSkillId, _triggerSkillLvl, _triggerType, _triggerChance);
		}
		catch(Exception e)
		{
			_log.warning("Skill: " + set.getString("skill_id", "0"));
			_log.warning("Level: " + set.getString("skill_level", "1"));
			e.printStackTrace();
		}
		finally
		{
			skills = null;
			skilllevels = null;
			attDefType = null;
		}
		if(!_icon.isEmpty() && !_icon.contains("."))
			_icon = "icon."+_icon;
	}

	private void attachTrigger(int triggerSkillId, int triggerSkillLvl, String triggerType, int triggerChance)
	{
		int id = triggerSkillId;
		int level = triggerSkillLvl;
		TriggerType t = TriggerType.valueOf(triggerType);
		double chance = triggerChance;

		TriggerInfo trigger = new TriggerInfo(id, level, t, chance, (byte)1);

		addTrigger(trigger);
		attachTriggerSkill(trigger);
	}

	private void UpdateParametr(int itemId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM itemall WHERE id=?");
			statement.setInt(1, itemId);
			rset = statement.executeQuery();

			// Go though the recordset of this SQL query
			while(rset.next())
			{
				_reusedelay = rset.getInt("reuse");
				magic_weapon = rset.getInt("magic_weapon") == 1;
				isOlympiadUse = rset.getInt("is_olympiad_can_use") == 1;
				immediate_effect = rset.getInt("immediate_effect");
				ex_immediate_effect = rset.getInt("ex_immediate_effect");
				delay_share_group = rset.getInt("delay_share_group");
				_is_premium = rset.getInt("is_premium") == 1;

				if(delay_share_group > 0)
				{
					if(!ItemTemplates._reuseGroups.containsKey(delay_share_group))
						ItemTemplates._reuseGroups.put(delay_share_group, new ArrayList<Integer>());
					if(!ItemTemplates._reuseGroups.get(delay_share_group).contains(itemId))
						ItemTemplates._reuseGroups.get(delay_share_group).add(itemId);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void attachTriggerSkill(TriggerInfo trigger)
	{
		if(_triggers == null)
			_triggers = new TriggerInfo[] { trigger };
		else
		{
			int len = _triggers.length;
			TriggerInfo[] tmp = new TriggerInfo[len + 1];
			System.arraycopy(_triggers, 0, tmp, 0, len);
			tmp[len] = trigger;
			_triggers = tmp;
		}
	}

	/**
	 * Returns the itemType.
	 * @return Enum
	 */
	public Enum getItemType()
	{
		return type;
	}

	public String getIcon()
	{
		return _icon;
	}

	public int getAgathionEnergy()
	{
		return _agathionEnergy;
	}

	/**
	 * Returns the durability of th item
	 * @return int
	 */
	public final int getDurability()
	{
		return _durability;
	}

	public final boolean isTemporal()
	{
		return _temporal;
	}

	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public final int getItemId()
	{
		return _itemId;
	}

	// TODO: Реализовать эту хрень, тогда можно будет делать дубликаты предметов в сервере не добавляя итем в клиент.
	public final int getItemDisplayId()
	{
		return _itemDisplayId;
	}

	public boolean isOlympiadUse()
	{
		return isOlympiadUse;
	}

	public int getReuseDelay()
	{
		return _reusedelay;
	}

	public abstract long getItemMask();

	/**
	 * Returns the type 2 of the item
	 * @return int
	 */
	public final int getType2()
	{
		return _type2;
	}

	public final int getType2ForPackets()
	{
		int type2 = _type2;
		switch(_type2)
		{
			case TYPE2_PET_WOLF:
			case TYPE2_PET_HATCHLING:
			case TYPE2_PET_STRIDER:
			case TYPE2_PET_GWOLF:
			case TYPE2_PET_BABY:
				if(_bodyPart == L2Item.SLOT_CHEST)
					type2 = TYPE2_SHIELD_ARMOR;
				else
					type2 = TYPE2_WEAPON;
				break;
			case TYPE2_PENDANT:
				type2 = TYPE2_ACCESSORY;
				break;
		}
		return type2;
	}

	/**
	 * Returns the weight of the item
	 * @return int
	 */
	public final int getWeight()
	{
		return _weight;
	}

	/**
	 * Returns if the item is crystallizable
	 * @return boolean
	 */
	public final boolean isCrystallizable()
	{
		return _crystallizable && !isStackable() && getCrystalType() != Grade.NONE && getCrystalCount() > 0;
	}

	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final Grade getCrystalType()
	{
		return _crystalType;
	}

	/**
	 * Returns the grade of the item.<BR><BR>
	 * <U><I>Concept :</I></U><BR>
	 * In fact, this fucntion returns the type of crystal of the item.
	 * @return int
	 */
	public final Grade getItemGrade()
	{
		return getCrystalType();
	}

	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _crystalCount;
	}

	/**
	 * @param enchantLevel 
	 * @return the quantity of crystals for crystallization on specific enchant level
	 */
	public final int getCrystalCount(int enchantLevel)
	{
		if(enchantLevel > 3)
			switch(_type2)
			{
				case TYPE2_SHIELD_ARMOR:
				case TYPE2_ACCESSORY:
					return _crystalCount + crystalEnchantBonusArmor[getCrystalType().externalOrdinal] * (3 * enchantLevel - 6);
				case TYPE2_WEAPON:
					return _crystalCount + crystalEnchantBonusWeapon[getCrystalType().externalOrdinal] * (2 * enchantLevel - 3);
				default:
					return _crystalCount;
			}
		else if(enchantLevel > 0)
			switch(_type2)
			{
				case TYPE2_SHIELD_ARMOR:
				case TYPE2_ACCESSORY:
					return _crystalCount + crystalEnchantBonusArmor[getCrystalType().externalOrdinal] * enchantLevel;
				case TYPE2_WEAPON:
					return _crystalCount + crystalEnchantBonusWeapon[getCrystalType().externalOrdinal] * enchantLevel;
				default:
					return _crystalCount;
			}
		else
			return _crystalCount;
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	public final String getName()
	{
		return _name;
	}

	/**
	 * Returns the additional name of the item
	 * @return String
	 */
	public final String getAdditionalName()
	{
		return _addname;
	}

	/**
	 * Return the part of the body used with the item.
	 * @return int
	 */
	public final int getBodyPart()
	{
		return _bodyPart;
	}

	/**
	 * Returns the type 1 of the item
	 * @return int
	 */
	public final int getType1()
	{
		return _type1;
	}

	/**
	 * Returns if the item is stackable
	 * @return boolean
	 */
	public final boolean isStackable()
	{
		return _stackable;
	}

	/**
	 * Returns the price of reference of the item
	 * @return int
	 */
	public final int getReferencePrice()
	{
		return _referencePrice;
	}

	/**
	 * Returns if the item can be sold
	 * @return boolean
	 */
	public final boolean isSellable()
	{
		return _sellable;
	}

	/**
	 * Returns if item is for hatchling
	 * @return boolean
	 */
	public boolean isForHatchling()
	{
		return _type2 == TYPE2_PET_HATCHLING;
	}

	/**
	 * Returns if item is for strider
	 * @return boolean
	 */
	public boolean isForStrider()
	{
		return _type2 == TYPE2_PET_STRIDER;
	}

	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForWolf()
	{
		return _type2 == TYPE2_PET_WOLF;
	}

	public boolean isForPetBaby()
	{
		return _type2 == TYPE2_PET_BABY;
	}

	/**
	 * Returns if item is for great wolf
	 * @return boolean
	 */
	public boolean isForGWolf()
	{
		return _type2 == TYPE2_PET_GWOLF;
	}

	/**
	 *  Магическая броня для петов 
	 */
	public boolean isPendant()
	{
		return _type2 == TYPE2_PENDANT;
	}

	public boolean isTradeable()
	{
		return _tradeable;
	}

	public boolean isDestroyable()
	{
		return _destroyable;
	}

	public boolean isStoreable()
	{
		return _storeable;
	}

	public boolean isDropable()
	{
		return _dropable;
	}

	public boolean isForPet()
	{
		return _type2 == TYPE2_PENDANT || _type2 == TYPE2_PET_HATCHLING || _type2 == TYPE2_PET_WOLF || _type2 == TYPE2_PET_STRIDER || _type2 == TYPE2_PET_GWOLF || _type2 == TYPE2_PET_BABY;
	}

	/**
	 * Add the FuncTemplate f to the list of functions used with the item
	 * @param f : FuncTemplate to add
	 */
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

	public FuncTemplate[] getAttachedFuncs()
	{
		return _funcTemplates;
	}

	/**
	 * Add the L2Skill skill to the list of skills generated by the item
	 * @param skill : L2Skill
	 */
	public void attachSkill(L2Skill skill)
	{
		if(_skills == null)
			_skills = new L2Skill[] { skill };
		else
		{
			int len = _skills.length;
			L2Skill[] tmp = new L2Skill[len + 1];
			System.arraycopy(_skills, 0, tmp, 0, len);
			tmp[len] = skill;
			_skills = tmp;
		}
	}

	public L2Skill[] getAttachedSkills()
	{
		return _skills;
	}

	public L2Skill getUnequipSkill()
	{
		return _unequip_skill;
	}

	public TriggerInfo[] getAttachedTriggers()
	{
		return _triggers;
	}

	/**
	 * @return skill that player get when has equipped weapon +4  or more  (for duals SA)
	 */
	/*public L2Skill[][] getEnchant4Skill()
	{
		return _enchantSkill;
	}*/

	public HashMap<Integer, L2Skill> getEnchant4Skill()
	{
		return _enchantSkill;
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	@Override
	public String toString()
	{
		return _name;
	}

	/**
	 * Определяет призрачный предмет или нет
	 * @return true, если предмет призрачный
	 */
	public boolean isShadowItem()
	{
		return _durability > 0 && !isTemporal();
	}

	public boolean isCommonItem()
	{
		return _common;
	}

	public boolean isRare()
	{
		return _rare;
	}

	public boolean isPvP()
	{
		return _pvp;
	}

	public boolean isSa()
	{
		return _sa;
	}

	public void setRare(boolean isRare)
	{
		_rare = isRare;
	}

	public void setPvP(boolean isPvP)
	{
		_pvp = isPvP;
	}

	public void setSa(boolean isSa)
	{
		_sa = isSa;
	}

	public boolean isAltSeed()
	{
		return _name.contains("Alternative");
	}

	public ItemClass getItemClass()
	{
		return _class;
	}

	/**
	 * Является ли вещь аденой или камнем печати
	 */
	public boolean isAdena()
	{
		return _itemId == 57 || _itemId == 6360 || _itemId == 6361 || _itemId == 6362;
	}

	public boolean isEpaulette()
	{
		return _itemId == 9912;
	}

	public boolean isEquipment()
	{
		return _type1 != TYPE1_ITEM_QUESTITEM_ADENA;
	}

	public boolean isKeyMatherial()
	{
		return _class == ItemClass.PIECES;
	}

	public boolean isSpellBook()
	{
		return _class == ItemClass.SPELLBOOKS;
	}

	public boolean isRaidAccessory()
	{
		return _itemId == 6661 || _itemId == 6659 || _itemId == 6656 || _itemId == 6660 || _itemId == 6662 || _itemId == 6658 || _itemId == 8191 || _itemId == 6657 || _itemId == 10314 || _itemId == 16025 || _itemId == 16026 || _itemId == 21712 || _itemId == 22173 || _itemId == 22174 || _itemId == 22175;
	}

	public boolean isSpecialKey()
	{
		if(_itemId == 1661) // thief key
			return false;
		if(_itemId >= 6665 && _itemId <= 6672) // deluxe chest key
			return false;
		return getName().contains("Key");
	}

	public boolean isArrow()
	{
		return type == EtcItemType.ARROW;
	}

	public boolean isBelt()
	{
		return _bodyPart == SLOT_BELT;
	}

	public boolean isBracelet()
	{
		return _bodyPart == SLOT_R_BRACELET || _bodyPart == SLOT_L_BRACELET;
	}

	public boolean isUnderwear()
	{
		return _bodyPart == SLOT_UNDERWEAR;
	}

	public boolean isCloak()
	{
		return _bodyPart == SLOT_BACK;
	}

	public boolean isTalisman()
	{
		return _bodyPart == SLOT_DECO;
	}

	public boolean isHerb()
	{
		return _itemId >= 8154 && _itemId <= 8157 || _itemId >= 8600 && _itemId <= 8614 || _itemId == 8952 || _itemId == 8953 || _itemId == 10432 || _itemId == 10433 || _itemId >= 10655 && _itemId <= 10657 || _itemId >= 13028 && _itemId <= 13031 || _itemId == 9849 || _itemId >= 14824 && _itemId <= 14827;
	}

	public boolean isMageSA()
	{
		return _addname.equalsIgnoreCase("Acumen") //
				|| _addname.equalsIgnoreCase("Empower") //
				|| _addname.equalsIgnoreCase("Magic Silence") //
				|| _addname.equalsIgnoreCase("Magic Mental Shield") //
				|| _addname.equalsIgnoreCase("Mana Up") //
				|| _addname.equalsIgnoreCase("Updown") //
				|| _addname.equalsIgnoreCase("Magic Hold") //
				|| _addname.equalsIgnoreCase("MP Regeneration");
	}

	public boolean isHeroWeapon()
	{
		return _itemId >= 6611 && _itemId <= 6621 || _itemId >= 9388 && _itemId <= 9390 || _is_hero;
	}

	public boolean isCursed()
	{
		return CursedWeaponsManager.getInstance().isCursed(_itemId);
	}

	public boolean isCombatFlag()
	{
		return _itemId == 9819;
	}

	public boolean isTerritoryFlag()
	{
		return _itemId == 13560 || _itemId == 13561 || _itemId == 13562 || _itemId == 13563 || _itemId == 13564 || _itemId == 13565 || _itemId == 13566 || _itemId == 13567 || _itemId == 13568;
	}

	public boolean isRod()
	{
		return getItemType() == WeaponType.ROD;
	}

	public boolean isMasterworkBody()
	{
		return _isMasterwork;
	}

	public boolean isWeapon()
	{
		return getType2() == L2Item.TYPE2_WEAPON;
	}

	public boolean isArmor()
	{
		return getType2() == L2Item.TYPE2_SHIELD_ARMOR;
	}

	public boolean isAccessory()
	{
		return getType2() == L2Item.TYPE2_ACCESSORY;
	}

	public boolean isQuest()
	{
		return getType2() == L2Item.TYPE2_QUEST;
	}

	public boolean canBeEnchanted()
	{
		boolean IgnorUnEnchantDurable = Util.contains_int(ConfigValue.IgnorUnEnchantDurable, _itemId);

		if(isShadowItem() && !IgnorUnEnchantDurable)
			return false;

		if(isTemporal() && !IgnorUnEnchantDurable)
			return false;

		if(isCommonItem())
			return false;

		if(isRod())
			return false;

		if(isBracelet())
			return false;

		if(isCloak() || isHeroWeapon())
			return Util.contains(ConfigValue.EnableEnchantCloak, getItemId());

		if(isCursed())
			return false;

		if(isQuest())
			return false;

		return isCrystallizable();
	}

	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return getItemType() == EtcItemType.BAIT || getItemType() == EtcItemType.ARROW || getItemType() == EtcItemType.BOLT || !(getBodyPart() == 0 || this instanceof L2EtcItem);
	}

	public int getFlags()
	{
		return _flags;
	}

	public boolean isConsume()
	{
		return _isConsume;
	}

	public byte attAtackType()
	{
		return _attAtackType;
	}

	public int attAtackValue()
	{
		return _attAtackValue;
	}

	public int[] attDefType()
	{
		return _attDefType;
	}

	public boolean isAttAtack()
	{
		return _attAtackType != -2;
	}

	public boolean isAttDef()
	{
		return !(_attDefType[0] == 0 && _attDefType[1] == 0 && _attDefType[2] == 0 && _attDefType[3] == 0 && _attDefType[4] == 0 && _attDefType[5] == 0);
	}

	public int getReuseDelayOnEquip()
	{
		return _reuseDelayOnEquip;
	}

	public void addEnchantOptions(int level, int[] options)
	{
		if(_enchantOptions.isEmpty())
			_enchantOptions = new HashIntObjectMap<int[]>();

		_enchantOptions.put(level, options);
	}

	public IntObjectMap<int[]> getEnchantOptions()
	{
		return _enchantOptions;
	}

	public int addKarma()
	{
		return _add_karma;
	}

	public boolean isMage()
	{
		return magic_weapon;
	}

	public int hasCastle()
	{
		return _castle;
	}
}

/** etcitem_type **
188.230.36.53:27017
race_ticket
dye
castle_guard
bolt
recipe
arrow
bless_scrl_enchant_wp
lure
rune
crop
scrl_enchant_am
ancient_crystal_enchant_am
bless_scrl_enchant_am
potion
none
scrl_enchant_wp
coupon
scrl_enchant_attr
seed2
scroll
seed
ticket_of_lord
rune_select
maturecrop
elixir
material
lotto
scrl_inc_enchant_prop_wp
ancient_crystal_enchant_wp
scrl_inc_enchant_prop_am
pet_collar
harvest
**/
