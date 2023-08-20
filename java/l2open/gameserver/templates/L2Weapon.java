package l2open.gameserver.templates;

import l2open.config.*;
import l2open.gameserver.model.base.ClassType;
import l2open.gameserver.skills.SkillTrait;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Log;

import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class L2Weapon extends L2Item
{
	private final int _soulShotCount;
	private final int _spiritShotCount;
	private final int _pDam;
	private final int _rndDam;
	private final int _atkReuse;
	private final int _mpConsume;
	private final int _mDam;
	private final int _aSpd;
	private final int _critical;
	private final double _accmod;
	private final double _evsmod;
	public final int _sDef;
	private final int _rShld;
    private final int kamaelAnalog;
	private final int _miser;
	private final int _miserChance;
	private final int _pAtkRange;
	private final int _poleAngle;
	private final int _cheapShot;
	private final int _cheapShotChance;
	private final ClassType _classtype;

    public enum WeaponType
	{
		NONE(1, "Shield", null),
		SWORD(2, "Sword", SkillTrait.trait_sword),
		BLUNT(3, "Blunt", SkillTrait.trait_blunt),
		DAGGER(4, "Dagger", SkillTrait.trait_dagger),
		BOW(5, "Bow", SkillTrait.trait_bow),
		POLE(6, "Pole", SkillTrait.trait_pole),
		ETC(7, "Etc", null),
		FIST(8, "Fist", SkillTrait.trait_fist),
		DUAL(9, "Dual Sword", SkillTrait.trait_dual),
		DUALFIST(10, "Dual Fist", SkillTrait.trait_dualfist),
		BIGSWORD(11, "Big Sword", SkillTrait.trait_sword), // Two Handed Swords
		PET(12, "Pet", SkillTrait.trait_sword),
		ROD(13, "Rod", null), // fishingrod
		BIGBLUNT(14, "Big Blunt", SkillTrait.trait_blunt),
		CROSSBOW(15, "Crossbow", SkillTrait.trait_crossbow),
		RAPIER(16, "Rapier", SkillTrait.trait_rapier),
		ANCIENTSWORD(17, "Ancient Sword", SkillTrait.trait_ancientsword), // Kamael 2h sword
		DUALDAGGER(18, "Dual Dagger", SkillTrait.trait_dualdagger);
/**
2-1
4-2
8-3
16-4
32-5
64-6
128-7
256-8
512-9
1024-10
2048-11
4096-12
8192-13
16384-14
32768-15
65536-16
131072-17
262144-18
*/
		private final int _id;
		private final String _name;
		private final SkillTrait _trait;

		private WeaponType(int id, String name, SkillTrait trait)
		{
			_id = id;
			_name = name;
			_trait = trait;
		}

		public long mask()
		{
			return 1L << _id;
		}

		public SkillTrait getTrait()
		{
			return _trait;
		}

		@Override
		public String toString()
		{
			return _name;
		}
	}

	/**
	 * Constructor<?> for Weapon.<BR><BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>_soulShotCount & _spiritShotCount</LI>
	 * <LI>_pDam & _mDam & _rndDam</LI>
	 * <LI>_critical</LI>
	 * <LI>_hitModifier</LI>
	 * <LI>_avoidModifier</LI>
	 * <LI>_shieldDes & _shieldDefRate</LI>
	 * <LI>_atkSpeed & _AtkReuse</LI>
	 * <LI>_mpConsume</LI>
	 * @param type : L2ArmorType designating the type of armor
	 * @param set : StatsSet designating the set of couples (key,value) caracterizing the armor
	 * @see L2Item constructor
	 */
	public L2Weapon(WeaponType type, StatsSet set)
	{
		super(type, set);
		_soulShotCount = set.getInteger("soulshots", 0);
		_spiritShotCount = set.getInteger("spiritshots", 0);
		_pDam = set.getInteger("p_dam", 0);
		_rndDam = set.getInteger("rnd_dam", 0);
		_atkReuse = set.getInteger("atk_reuse", type == WeaponType.BOW ? 1500 : type == WeaponType.CROSSBOW ? 820 : 0);
		_mpConsume = set.getInteger("mp_consume", 0);
		_mDam = set.getInteger("m_dam", 0);
		_aSpd = set.getInteger("atk_speed", 0);
		_critical = 0;set.getInteger("critical", 0);
		_accmod = set.getDouble("hit_modify", 0.0);
		_evsmod = set.getDouble("avoid_modify", 0.0);
		_sDef = set.getInteger("shield_def", 0);
		_rShld = set.getInteger("shield_def_rate", 0);
        kamaelAnalog = set.getInteger("kamaelAnalog", -1);
		_miser = set.getInteger("miser", 0);
		_miserChance = set.getInteger("miserChance", 0);
		_pAtkRange = set.getInteger("pAtkRange", 0);
		_poleAngle = set.getInteger("poleAngle", 0);
		_cheapShot = set.getInteger("cheapShot", 0);
		_classtype = set.getEnum("player_class", ClassType.class, null);
		_cheapShotChance = set.getInteger("cheapShotChance", 0);

		if(!_addname.isEmpty() && _skills == null)
			Log.add("id=" + _itemId + " name=" + _name + " [" + _addname + "]", "unimplemented_sa");

		for(int i=1;i<=ConfigValue.EnchantMaxWeapon;i++)
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

		//if(_enchantSkill[4][0] == null && type == WeaponType.RAPIER)
		//	_enchantSkill[4][0] = SkillTable.getInstance().getInfo(3426, 1); // Maximum Ability
		if(_enchantSkill.get(4) == null && type == WeaponType.RAPIER)
			_enchantSkill.put(4, SkillTable.getInstance().getInfo(3426, 1)); // Maximum Ability

		if(_pDam != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.p_physical_attack, 0x10, _pDam));
		if(_mDam != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.p_magical_attack, 0x10, _mDam));
		//if(_critical != 0)
		//	attachFunction(new FuncTemplate(null, "Set", Stats.CRITICAL_BASE, 0x08, _critical * 10));
		if(_aSpd != 0)
			attachFunction(new FuncTemplate(null, "Set", Stats.ATK_BASE, 0x08, _aSpd));

		if(_sDef != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.SHIELD_DEFENCE, 0x10, _sDef));
		if(_accmod != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.p_hit, 0x10, _accmod));
		if(_evsmod != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.EVASION_RATE, 0x10, _evsmod));
		if(_rShld != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.SHIELD_RATE, 0x10, _rShld));

		if(_miserChance != 0)
		{
			attachFunction(new FuncTemplate(null, "Add", Stats.SS_USE_BOW, 0x08, _miser));
			attachFunction(new FuncTemplate(null, "Add", Stats.SS_USE_BOW_CHANCE, 0x08, _miserChance));
		}
		if(_pAtkRange != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.POWER_ATTACK_RANGE, 0x40, _pAtkRange));
		if(_poleAngle != 0)
			attachFunction(new FuncTemplate(null, "Add", Stats.POLE_ATTACK_ANGLE, 0x40, _poleAngle));
		if(_cheapShotChance != 0)
		{
			attachFunction(new FuncTemplate(null, "Add", Stats.MP_USE_BOW, 0x08, _cheapShot));
			attachFunction(new FuncTemplate(null, "Add", Stats.MP_USE_BOW_CHANCE, 0x08, _cheapShotChance));
		}

		if(_crystalType != Grade.NONE)
		{
			if(_sDef > 0)
			{
				attachFunction(new FuncTemplate(null, "Enchant", Stats.SHIELD_DEFENCE, 0x0C, 0));
				if(set.getInteger("type2") == L2Item.TYPE2_SHIELD_ARMOR)
					attachFunction(new FuncTemplate(null, "Enchant", Stats.p_max_hp, 0x80, 0));
			}
			if(_pDam > 0)
				attachFunction(new FuncTemplate(null, "Enchant", Stats.p_physical_attack, 0x0C, 0));
			if(_mDam > 0)
				attachFunction(new FuncTemplate(null, "Enchant", Stats.p_magical_attack, 0x0C, 0));
		}
	}

	/**
	 * Returns the type of Weapon
	 * @return L2WeaponType
	 */
	@Override
	public WeaponType getItemType()
	{
		return (WeaponType) type;
	}

	/**
	 * Возвращает базовую скорость атаки
	 */
	public int getBaseSpeed()
	{
		return _aSpd;
	}

	/**
	 * Returns the ID of the Etc item after applying the mask.
	 * @return int : ID of the Weapon
	 */
	@Override
	public long getItemMask()
	{
		return getItemType().mask();
	}

	/**
	 * Returns the quantity of SoulShot used.
	 * @return int
	 */
	public int getSoulShotCount()
	{
		return _soulShotCount;
	}

	/**
	 * Returns the quatity of SpiritShot used.
	 * @return int
	 */
	public int getSpiritShotCount()
	{
		return _spiritShotCount;
	}

	/**
	 * Returns the physical damage.
	 * @return int
	 */
	public int getPDamage()
	{
		return _pDam;
	}

	public int getCritical()
	{
		switch(getItemType())
		{
			case CROSSBOW:
			case BOW:
				return 120 + _critical;
			case DUALDAGGER:
			case DAGGER:
				return 120 + _critical;
			case POLE:
				return 80 + _critical;
			case RAPIER:
			case SWORD:
			case DUAL:
			case BIGSWORD:
			case ANCIENTSWORD:
				return 80 + _critical;
			case BIGBLUNT:
			case BLUNT:
				return 40 + _critical;
			case DUALFIST:
			case FIST:
				return 40 + _critical;
			case ETC:
				return 40 + _critical;
			default:
				return 40 + _critical;
		}
	}

	/**
	 * Returns the random damage inflicted by the weapon
	 * @return int
	 */
	public int getRandomDamage()
	{
		return _rndDam;
	}

	/**
	 * Return the Attack Reuse Delay of the L2Weapon.<BR><BR>
	 * @return int
	 */
	public int getAttackReuseDelay()
	{
		return _atkReuse;
	}

	/**
	 * Returns the magical damage inflicted by the weapon
	 * @return int
	 */
	public int getMDamage()
	{
		return _mDam;
	}

	/**
	 * Returns the MP consumption with the weapon
	 * @return int
	 */
	public int getMpConsume()
	{
		if(type == WeaponType.BOW)
			return getMpConsumeBow();
		return _mpConsume;
	}

	public int getMpConsumeBow()
	{
		switch(_crystalType)
		{
			case NONE:
				return 1;
			case D:
				return 2;
			case C:
				return 3;
			case B:
				return 4;
			case A:
				return 5;
			case S:
				return 6;
			default:
				return _mpConsume;
		}
	}

/*	public int getAttackRange()
	{
		switch(getItemType())
		{
			case BOW:
				return 460;
			case CROSSBOW:
				return 360;
			case POLE:
				return 40;
			default:
				return 0;
		}
	}*/
	// TODO: REV
	
	public int getAttackRange()
	{
		switch(getItemType())
		{
			case BOW:
				return 480;
			case CROSSBOW:
				return 380;
			case POLE:
				return 60;
			default:
				return 20;
		}
	}

    public int getKamaelAnalog()
	{
        return kamaelAnalog;
    }

	public ClassType getClassType()
	{
		return _classtype;
	}
}