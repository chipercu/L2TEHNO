package l2open.gameserver.templates;

import l2open.config.*;
import l2open.gameserver.model.base.ClassType;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Weapon.WeaponType;

import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class L2Armor extends L2Item
{
	public static final double EMPTY_RING = 5;
	public static final double EMPTY_EARRING = 9;
	public static final double EMPTY_NECKLACE = 13;
	public static final double EMPTY_HELMET = 12;
	public static final double EMPTY_BODY_FIGHTER = 31;
	public static final double EMPTY_LEGS_FIGHTER = 18;
	public static final double EMPTY_BODY_MYSTIC = 15;
	public static final double EMPTY_LEGS_MYSTIC = 8;
	public static final double EMPTY_GLOVES = 8;
	public static final double EMPTY_BOOTS = 7;

	private final int _pDef;
	private final int _mDef;
	private final int _mpBonus;
	private final double _evsmod;
	private final ClassType _classtype;

    public enum ArmorType
	{
		NONE(1, "None"),
		LIGHT(2, "Light"),
		HEAVY(3, "Heavy"),
		MAGIC(4, "Magic"),
		PET(5, "Pet"),
		SIGIL(6, "Sigil");

		final int _id;
		final String _name;

		ArmorType(int id, String name)
		{
			_id = id;
			_name = name;
		}

		public long mask()
		{
			return 1L << (_id + WeaponType.values().length);
		}

		@Override
		public String toString()
		{
			return _name;
		}
	}

	/**
	 * Constructor<?> for Armor.<BR><BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>_avoidModifier</LI>
	 * <LI>_pDef & _mDef</LI>
	 * <LI>_mpBonus & _hpBonus</LI>
	 * @param type : L2ArmorType designating the type of armor
	 * @param set : StatsSet designating the set of couples (key,value) caracterizing the armor
	 * @see L2Item constructor
	 */
	public L2Armor(ArmorType type, StatsSet set)
	{
		super(type, set);

		_pDef = set.getInteger("p_def", 0);
		_mDef = set.getInteger("m_def", 0);
		_mpBonus = set.getInteger("mp_bonus", 0);
		_evsmod = set.getDouble("avoid_modify", 0.0);
		_classtype = set.getEnum("player_class", ClassType.class, null);
		//System.out.println("---------------------- L2Armor Load: "+getItemType().toString()+" ----------------------");
		for(int i=1;i<=ConfigValue.EnchantMaxArmor;i++)
		{
			int sId = set.getInteger("enchant"+i+"_skill_id", 0);
			int sLv = set.getInteger("enchant"+i+"_skill_lvl", 0);
			
			if(sId > 0 && sLv > 0)
			{
				//_enchantSkill[i][0] = SkillTable.getInstance().getInfo(sId, sLv);
				_enchantSkill.put(i, SkillTable.getInstance().getInfo(sId, sLv));
			}
			/*for(int i2 = 1;i2<5;i2++)
			{
				sId = set.getInteger("enchant"+i+"_skill_id"+i2, 0);
				sLv = set.getInteger("enchant"+i+"_skill_lvl"+i2, 0);
				if(sId > 0 && sLv > 0)
					_enchantSkill[i][i2] = SkillTable.getInstance().getInfo(sId, sLv);
			}*/
		}

		if(_pDef > 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.p_physical_defence, 0x10, _pDef));
		if(_mDef > 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.p_magical_defence, 0x10, _mDef));
		if(_mpBonus > 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.p_max_mp, 0x40, _mpBonus));
		if(_evsmod != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.EVASION_RATE, 0x10, _evsmod));

		if(_crystalType != Grade.NONE || getItemId() == 21580 || getItemId() == 21706)
		{
			if(_pDef > 0)
			{
				attachFunction(new FuncTemplate(null, "Enchant", Stats.p_physical_defence, 0x0C, 0));
				if(set.getInteger("type2") == L2Item.TYPE2_SHIELD_ARMOR)
					attachFunction(new FuncTemplate(null, "Enchant", Stats.p_max_hp, 0x80, 0));
			}
			if(_mDef > 0)
				attachFunction(new FuncTemplate(null, "Enchant", Stats.p_magical_defence, 0x0C, 0));
		}
	}

	/**
	 * Returns the type of the armor.
	 * @return L2ArmorType
	 */
	@Override
	public ArmorType getItemType()
	{
		return (ArmorType) super.type;
	}

	/**
	 * Returns the ID of the item after applying the mask.
	 * @return int : ID of the item
	 */
	@Override
	public final long getItemMask()
	{
		return getItemType().mask();
	}

	public ClassType getClassType()
	{
		return _classtype;
	}
}