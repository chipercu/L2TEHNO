package l2open.gameserver.skills;

import l2open.config.*;
import l2open.gameserver.skills.inits.InitConst;
import l2open.gameserver.skills.inits.InitFunc;
import l2open.gameserver.skills.inits.Init_rShld;

import java.util.NoSuchElementException;

public enum Stats
{
	// Внимание, для значений из конфига использовать Integer а не int! Это нужно для изменения значений на лету.
	p_max_hp("p_max_hp", 1, ConfigValue.MaxHP, true),
	p_max_mp("p_max_mp", 1, ConfigValue.MaxMP, true),
	p_max_cp("p_max_cp", 1, ConfigValue.MaxCP, true),

	// Для эффектов типа Seal of Limit
	p_limit_hp("p_limit_hp", 1, 100, false, new InitConst(100.)),
	MP_LIMIT("mpLimit", 1, 100, false, new InitConst(100.)),
	CP_LIMIT("cpLimit", 1, 100, false, new InitConst(100.)),

	REGENERATE_HP_RATE("regHp", null, null, false),
	REGENERATE_CP_RATE("regCp", null, null, false),
	REGENERATE_MP_RATE("regMp", null, null, false),

	p_speed("p_speed", 0, ConfigValue.LimitMove, true),

	p_physical_defence("p_physical_defence", 0, ConfigValue.LimitPDef, true),
	p_magical_defence("p_magical_defence", 0, ConfigValue.LimitMDef, true),
	p_physical_attack("p_physical_attack", 0, ConfigValue.LimitPatk, true),
	p_magical_attack("p_magical_attack", 0, ConfigValue.LimitMAtk, true),
	p_attack_speed("p_attack_speed", 0, ConfigValue.LimitPatkSpd, false),
	p_magic_speed("p_magic_speed", 0, ConfigValue.LimitMatkSpd, false),

	MAGIC_REUSE_RATE("mReuse", null, null, false),
	PHYSIC_REUSE_RATE("pReuse", null, null, false),
	ATK_REUSE("atkReuse", null, null, false),
	ATK_BASE("atkBaseSpeed", null, null, false),

	CRITICAL_DAMAGE("p_critical_damage_per", 0, ConfigValue.LimitCriticalDamage / 2, false, new InitConst(ConfigValue.BaseCriticalDamage)),
	CRITICAL_DAMAGE_STATIC("p_critical_damage_diff", null, null, false, new InitConst(0)),
	EVASION_RATE("p_avoid", 0, ConfigValue.LimitEvasion, false),
	p_hit("p_hit", 0, ConfigValue.LimitAccuracy, false),
	CRITICAL_BASE("p_critical_rate_diff", 0, ConfigValue.LimitCritical, false, new InitConst(100)),
	CRITICAL_RATE("p_critical_rate_per", 0, null, false, new InitConst(100)),
	MCRITICAL_RATE("mCritRate", 0, ConfigValue.LimitMCritical, false, new InitConst(10.)),
	MCRITICAL_DAMAGE("mCritDamage", 0, 10, false, new InitConst(2.5)),
	MCRIT_DAMAGE_RECEPTIVE("mCritDamRcpt", Integer.MIN_VALUE, Integer.MAX_VALUE, false, new InitConst(100)),

	PHYSICAL_DAMAGE("physDamage", null, null, false, null), // TODO: Потом убрать...Юзается у некоторых клиентов на рубахе ольфа...
	MAGIC_DAMAGE("magicDamage", null, null, false, null),

	CAST_INTERRUPT("concentration", 0, 100, false, null),

	SHIELD_DEFENCE("sDef", null, null, false),
	SHIELD_RATE("rShld", 0, 90, false, new Init_rShld()),
	SHIELD_ANGLE("shldAngle", null, null, false, new InitConst(60)),

	POWER_ATTACK_RANGE("pAtkRange", 0, 1500, false),
	MAGIC_ATTACK_RANGE("mAtkRange", 0, 1500, false),
	POLE_ATTACK_ANGLE("poleAngle", 0, 180, false),
	POLE_TARGERT_COUNT("poleTargetCount", null, null, false),

	STAT_STR("STR", 1, 99, false),
	STAT_CON("CON", 1, 99, false),
	STAT_DEX("DEX", 1, 99, false),
	STAT_INT("INT", 1, 99, false),
	STAT_WIT("WIT", 1, 99, false),
	STAT_MEN("MEN", 1, 99, false),

	BREATH("breath", null, null, false),
	FALL("fall", null, null, false),
	EXP_LOST("expLost", null, null, false),

	p_resist_dd_magic("p_resist_dd_magic", null, null, false, new InitConst(0)),
	CANCEL_RECEPTIVE("cancelRcpt", -200, 200, false, new InitConst(0)), // p_resist_dispel_by_category;slot_all
	DEBUFF_RECEPTIVE("debuffRcpt", -200, 200, false, new InitConst(0)),

	CANCEL_POWER("cancelPower", -200, 200, false, new InitConst(0)),
	MAGIC_POWER("magicPower", -2300, 200, false, new InitConst(0)),

	SKILL_CRIT_CHANCE_MOD("SkillCritChanceMod", 10, 190, false, new InitConst(100)),
	FATALBLOW_RATE("blowRate", 0, 10, false, new InitConst(1.)),

	FIRE_RECEPTIVE("fireRcpt", null, null, false),
	WIND_RECEPTIVE("windRcpt", null, null, false),
	WATER_RECEPTIVE("waterRcpt", null, null, false),
	EARTH_RECEPTIVE("earthRcpt", null, null, false),
	UNHOLY_RECEPTIVE("unholyRcpt", null, null, false),
	SACRED_RECEPTIVE("sacredRcpt", null, null, false),

	CRIT_DAMAGE_RECEPTIVE("critDamRcpt", Integer.MIN_VALUE, Integer.MAX_VALUE, false, new InitConst(100)),
	CRIT_CHANCE_RECEPTIVE("critChanceRcpt", 0, 190, false, new InitConst(100)),

	ATTACK_ELEMENT_FIRE("attackFire", 0, 500, false),
	ATTACK_ELEMENT_WATER("attackWater", 0, 500, false),
	ATTACK_ELEMENT_WIND("attackWind", 0, 500, false),
	ATTACK_ELEMENT_EARTH("attackEarth", 0, 500, false),
	ATTACK_ELEMENT_SACRED("attackSacred", 0, 500, false),
	ATTACK_ELEMENT_UNHOLY("attackUnholy", 0, 500, false),

	ABSORB_DAMAGE_PERCENT("absorbDam", 0, ConfigValue.LimitAbsorbDam, false),
	ABSORB_DAMAGEMP_PERCENT("absorbDamMp", 0, ConfigValue.LimitAbsorbDamMp, false),

	TRANSFER_PET_DAMAGE_PERCENT("transferPetDam", 0, 100, false),
	TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT("transferToEffectorDam", 0, 100, false),
	TRANSFER_MP_DAMAGE_PERCENT("transferMpDam", 0, 100, false),

	// Отражение урона с шансом. Урон получает только атакующий.
	REFLECT_AND_BLOCK_DAMAGE_CHANCE("reflectAndBlockDam", 0, 100, false), // Ближний урон без скиллов
	REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE("reflectAndBlockPSkillDam", 0, 100, false), // Ближний урон скиллами
	REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE("reflectAndBlockMSkillDam", 0, 100, false), // Любой урон магией

	// Отражение урона в процентах. Урон получает и атакующий и цель
	REFLECT_DAMAGE_PERCENT("reflectDam", 0, ConfigValue.LimitReflectDam, false), // Ближний урон без скиллов
	REFLECT_PSKILL_DAMAGE_PERCENT("reflectPSkillDam", 0, 100, false), // Ближний урон скиллами
	REFLECT_MSKILL_DAMAGE_PERCENT("reflectMSkillDam", 0, 100, false), // Любой урон магией

	REFLECT_PHYSIC_SKILL("reflectPhysicSkill", 0, 100, false),
	REFLECT_MAGIC_SKILL("reflectMagicSkill", 0, 100, false),

	REFLECT_PHYSIC_DEBUFF("reflectPhysicDebuff", 0, 100, false),
	REFLECT_MAGIC_DEBUFF("reflectMagicDebuff", 0, 100, false),

	PSKILL_EVASION("pSkillEvasion", 0, 100, false),

	COUNTER_ATTACK("counterAttack", 0, 100, false),

	HEAL_EFFECTIVNESS("hpEff", 0, 1000, false),   //не прибавляет статчиное число только %
	MANAHEAL_EFFECTIVNESS("mpEff", 0, null, false),
	CPHEAL_EFFECTIVNESS("cpEff", 0, 1000, false),
	HEAL_POWER("healPower", null, null, false),
	MP_MAGIC_SKILL_CONSUME("mpConsum", null, null, false),
	MP_PHYSICAL_SKILL_CONSUME("mpConsumePhysical", null, null, false),
	MP_DANCE_SKILL_CONSUME("mpDanceConsume", null, null, false),
	MP_USE_BOW("cheapShot", null, null, false),
	MP_USE_BOW_CHANCE("cheapShotChance", null, null, false),
	SS_USE_BOW("miser", null, null, false),
	SS_USE_BOW_CHANCE("miserChance", null, null, false),
	ACTIVATE_RATE("activateRate", null, null, false),
	SKILL_MASTERY("skillMastery", 0, null, false),

	MAX_LOAD("maxLoad", null, null, false),
	MAX_NO_PENALTY_LOAD("maxNoPenaltyLoad", null, null, false),
	INVENTORY_LIMIT("inventoryLimit", null, ConfigValue.ExpandInventoryMax, false),
	STORAGE_LIMIT("storageLimit", null, null, false),
	TRADE_LIMIT("tradeLimit", null, null, false),
	COMMON_RECIPE_LIMIT("CommonRecipeLimit", null, null, false),
	DWARVEN_RECIPE_LIMIT("DwarvenRecipeLimit", null, null, false),
	BUFF_LIMIT("buffLimit", null, null, false),
	SONG_LIMIT("songLimit", null, null, false),
	SOULS_LIMIT("soulsLimit", null, null, false),
	SOULS_CONSUME_EXP("soulsExp", null, null, false),
	TALISMANS_LIMIT("talismansLimit", 0, 6, false),
	
	PVE_PHYSICAL_DMG("pvePhysDmg", null, null, false, null), //
	PVE_PHYS_SKILL_DMG("pvePhysSkillsDmg", null, null, false, null),
	PVE_BOW_DMG("pveBowDmg", null, null, false, null),
	PVE_BOW_SKILL_DMG("pveBowSkillsDmg", null, null, false, null),
	PVE_MAGICAL_DMG("pveMagicalDmg", null, null, false, null),
	p_pve_magical_skill_defence_bonus("p_pve_magical_skill_defence_bonus", null, null, false, null),
	p_pve_physical_attack_defence_bonus("p_pve_physical_attack_defence_bonus", null, null, false, null),
	p_pve_physical_skill_defence_bonus("p_pve_physical_skill_defence_bonus", null, null, false, null),

	PVR_PHYSICAL_DMG("pvrPhysDmg", null, null, false, null), //
	PVR_PHYS_SKILL_DMG("pvrPhysSkillsDmg", null, null, false, null),
	PVR_BOW_DMG("pvrBowDmg", null, null, false, null),
	PVR_BOW_SKILL_DMG("pvrBowSkillsDmg", null, null, false, null),
	PVR_MAGICAL_DMG("pvrMagicalDmg", null, null, false, null),

	GRADE_EXPERTISE_LEVEL("gradeExpertiseLevel", null, null, false),
	EXP("ExpMultiplier", 0, null, false),
	SP("SpMultiplier", 0, null, false),
	DROP("DropMultiplier", null, null, false),
	SPOIL("SpoilMultiplier", null, null, false),
	ADENA("AdenaMultiplier", null, null, false),
	CRAFT("p_craft_chance", 0, null, false),

	PVP_MAGIC_SKILL_DMG_BONUS("pvpMagicSkillDmgBonus", null, null, false, null),
	p_pvp_magical_skill_defence_bonus("p_pvp_magical_skill_defence_bonus", null, null, false, null),
	PVP_PHYS_DMG_BONUS("pvpPhysDmgBonus", null, null, false, null),
	p_pvp_physical_attack_defence_bonus("p_pvp_physical_attack_defence_bonus", null, null, false, null),
	PVP_PHYS_SKILL_DMG_BONUS("pvpPhysSkillDmgBonus", null, null, false, null),
	p_pvp_physical_skill_defence_bonus("p_pvp_physical_skill_defence_bonus", null, null, false, null),
	P_CRITICAL_RATE_POSITION_BONUS("p_critical_rate_position_bonus", null, null, false, null),
	P_CRITICAL_DAMAGE_POSITION("p_critical_damage_position", null, null, false, null),
	p_skill_power_per("p_skill_power_per", null, null, false),
	p_skill_power_diff("p_skill_power_diff", null, null, false);

	public static final int NUM_STATS = values().length;

	private String _value;
	public final Integer _min;
	public final Integer _max;
	private boolean _limitOnlyPlayable;
	private InitFunc _init;

	public String getValue()
	{
		return _value;
	}

	public boolean isLimitOnlyPlayable()
	{
		return _limitOnlyPlayable;
	}

	public InitFunc getInit()
	{
		return _init;
	}

	private Stats(String s, Integer min, Integer max, boolean limitOnlyPlayable)
	{
		_value = s;
		_min = min;
		_max = max;
		_limitOnlyPlayable = limitOnlyPlayable;
		_init = null;
	}

	private Stats(String s, Integer min, Integer max, boolean limitOnlyPlayable, InitFunc init)
	{
		_value = s;
		_min = min;
		_max = max;
		_limitOnlyPlayable = limitOnlyPlayable;
		_init = init;
	}

	public static Stats valueOfXml(String name)
	{
		for(Stats s : values())
			if(s.getValue().equals(name))
				return s;

		throw new NoSuchElementException("Unknown name '" + name + "' for enum BaseStats");
	}

	@Override
	public String toString()
	{
		return _value;
	}
}