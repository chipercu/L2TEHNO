package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.SkillsTable.*;

@Table(
        name = "skills",
        fields = {
                @Field(name = ID,
                @Field(name = LEVEL,
                @Field(name = LEARN,
                @Field(name = OPERATE_TYPE,
                @Field(name = NAME,
                @Field(name = IS_MAGIC,
                @Field(name = MAGIC_LEVEL,
                @Field(name = ACTIVATE_RATE,
                @Field(name = EFFECTPOINT,
                @Field(name = MP_CONSUME1,
                @Field(name = MP_CONSUME2,
                @Field(name = HP_CONSUME,
                @Field(name = CAST_RANGE,
                @Field(name = SKILL_HIT_TIME,
                @Field(name = COOL_TIME,
                @Field(name = HIT_CANCEL_TIME,
                @Field(name = POWER,
                @Field(name = ENCHANT,
                @Field(name = REUSE,
                @Field(name = OLYMPIAD_USE,
                @Field(name = STATIC_HITTIME,
                @Field(name = STATIC_REUSE,
                @Field(name = ABNORMAL_TIME,
                @Field(name = ABNORMAL_TYPE,
                @Field(name = ABNORMAL_LV,
                @Field(name = ABNORMAL_INSTANT,
                @Field(name = BUFF_PROTECT_LEVEL,
                @Field(name = TRAIT,
                @Field(name = LV_BONUS_RATE,
                @Field(name = BASIC_PROPERTY,
                @Field(name = NEXT_ACTION,
                @Field(name = TARGET_TYPE,
                @Field(name = AFFECT_SCOPE,
                @Field(name = AFFECT_OBJECT,
                @Field(name = AFFECT_RANGE,
                @Field(name = AFFECT_LIMIT,
                @Field(name = FAN_RANGE,
                @Field(name = EFFECTIVE_RANGE,
                @Field(name = EFFECT_POINT,
                @Field(name = IRREPLACEABLE_BUFF,
                @Field(name = ABNORMAL_VISUAL_EFFECT,
                @Field(name = ICON,
        }
)
public class SkillsTable extends DataBaseTable<SkillsTable> {

    public static final String ID = "id";
    public static final String LEVEL = "level";
    public static final String LEARN = "learn";
    public static final String OPERATE_TYPE = "operate_type";
    public static final String NAME = "name";
    public static final String IS_MAGIC = "is_magic";
    public static final String MAGIC_LEVEL = "magic_level";
    public static final String ACTIVATE_RATE = "activate_rate";
    public static final String EFFECTPOINT = "effectPoint";
    public static final String MP_CONSUME1 = "mp_consume1";
    public static final String MP_CONSUME2 = "mp_consume2";
    public static final String HP_CONSUME = "hp_consume";
    public static final String CAST_RANGE = "cast_range";
    public static final String SKILL_HIT_TIME = "skill_hit_time";
    public static final String COOL_TIME = "cool_time";
    public static final String HIT_CANCEL_TIME = "hit_cancel_time";
    public static final String POWER = "power";
    public static final String ENCHANT = "enchant";
    public static final String REUSE = "reuse";
    public static final String OLYMPIAD_USE = "olympiad_use";
    public static final String STATIC_HITTIME = "static_hittime";
    public static final String STATIC_REUSE = "static_reuse";
    public static final String ABNORMAL_TIME = "abnormal_time";
    public static final String ABNORMAL_TYPE = "abnormal_type";
    public static final String ABNORMAL_LV = "abnormal_lv";
    public static final String ABNORMAL_INSTANT = "abnormal_instant";
    public static final String BUFF_PROTECT_LEVEL = "buff_protect_level";
    public static final String TRAIT = "trait";
    public static final String LV_BONUS_RATE = "lv_bonus_rate";
    public static final String BASIC_PROPERTY = "basic_property";
    public static final String NEXT_ACTION = "next_action";
    public static final String TARGET_TYPE = "target_type";
    public static final String AFFECT_SCOPE = "affect_scope";
    public static final String AFFECT_OBJECT = "affect_object";
    public static final String AFFECT_RANGE = "affect_range";
    public static final String AFFECT_LIMIT = "affect_limit";
    public static final String FAN_RANGE = "fan_range";
    public static final String EFFECTIVE_RANGE = "effective_range";
    public static final String EFFECT_POINT = "effect_point";
    public static final String IRREPLACEABLE_BUFF = "irreplaceable_buff";
    public static final String ABNORMAL_VISUAL_EFFECT = "abnormal_visual_effect";
    public static final String ICON = "icon";
    public SkillsTable() {
super(SkillsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public IntegergetLearn() {
        return get(LEARN, Integer.class);
    }
    public StringgetOperate_type() {
        return get(OPERATE_TYPE, String.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetIs_magic() {
        return get(IS_MAGIC, Integer.class);
    }
    public IntegergetMagic_level() {
        return get(MAGIC_LEVEL, Integer.class);
    }
    public IntegergetActivate_rate() {
        return get(ACTIVATE_RATE, Integer.class);
    }
    public IntegergetEffectPoint() {
        return get(EFFECTPOINT, Integer.class);
    }
    public IntegergetMp_consume1() {
        return get(MP_CONSUME1, Integer.class);
    }
    public IntegergetMp_consume2() {
        return get(MP_CONSUME2, Integer.class);
    }
    public IntegergetHp_consume() {
        return get(HP_CONSUME, Integer.class);
    }
    public IntegergetCast_range() {
        return get(CAST_RANGE, Integer.class);
    }
    public DoublegetSkill_hit_time() {
        return get(SKILL_HIT_TIME, Double.class);
    }
    public DoublegetCool_time() {
        return get(COOL_TIME, Double.class);
    }
    public DoublegetHit_cancel_time() {
        return get(HIT_CANCEL_TIME, Double.class);
    }
    public IntegergetPower() {
        return get(POWER, Integer.class);
    }
    public StringgetEnchant() {
        return get(ENCHANT, String.class);
    }
    public IntegergetReuse() {
        return get(REUSE, Integer.class);
    }
    public IntegergetOlympiad_use() {
        return get(OLYMPIAD_USE, Integer.class);
    }
    public IntegergetStatic_hittime() {
        return get(STATIC_HITTIME, Integer.class);
    }
    public IntegergetStatic_reuse() {
        return get(STATIC_REUSE, Integer.class);
    }
    public IntegergetAbnormal_time() {
        return get(ABNORMAL_TIME, Integer.class);
    }
    public StringgetAbnormal_type() {
        return get(ABNORMAL_TYPE, String.class);
    }
    public IntegergetAbnormal_lv() {
        return get(ABNORMAL_LV, Integer.class);
    }
    public IntegergetAbnormal_instant() {
        return get(ABNORMAL_INSTANT, Integer.class);
    }
    public IntegergetBuff_protect_level() {
        return get(BUFF_PROTECT_LEVEL, Integer.class);
    }
    public StringgetTrait() {
        return get(TRAIT, String.class);
    }
    public IntegergetLv_bonus_rate() {
        return get(LV_BONUS_RATE, Integer.class);
    }
    public StringgetBasic_property() {
        return get(BASIC_PROPERTY, String.class);
    }
    public StringgetNext_action() {
        return get(NEXT_ACTION, String.class);
    }
    public ObjectgetTarget_type() {
        return get(TARGET_TYPE, Object.class);
    }
    public ObjectgetAffect_scope() {
        return get(AFFECT_SCOPE, Object.class);
    }
    public ObjectgetAffect_object() {
        return get(AFFECT_OBJECT, Object.class);
    }
    public IntegergetAffect_range() {
        return get(AFFECT_RANGE, Integer.class);
    }
    public StringgetAffect_limit() {
        return get(AFFECT_LIMIT, String.class);
    }
    public StringgetFan_range() {
        return get(FAN_RANGE, String.class);
    }
    public IntegergetEffective_range() {
        return get(EFFECTIVE_RANGE, Integer.class);
    }
    public IntegergetEffect_point() {
        return get(EFFECT_POINT, Integer.class);
    }
    public IntegergetIrreplaceable_buff() {
        return get(IRREPLACEABLE_BUFF, Integer.class);
    }
    public StringgetAbnormal_visual_effect() {
        return get(ABNORMAL_VISUAL_EFFECT, String.class);
    }
    public StringgetIcon() {
        return get(ICON, String.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setLearn() {
        set(LEARN, value);
    }
    public void setOperate_type() {
        set(OPERATE_TYPE, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setIs_magic() {
        set(IS_MAGIC, value);
    }
    public void setMagic_level() {
        set(MAGIC_LEVEL, value);
    }
    public void setActivate_rate() {
        set(ACTIVATE_RATE, value);
    }
    public void setEffectPoint() {
        set(EFFECTPOINT, value);
    }
    public void setMp_consume1() {
        set(MP_CONSUME1, value);
    }
    public void setMp_consume2() {
        set(MP_CONSUME2, value);
    }
    public void setHp_consume() {
        set(HP_CONSUME, value);
    }
    public void setCast_range() {
        set(CAST_RANGE, value);
    }
    public void setSkill_hit_time() {
        set(SKILL_HIT_TIME, value);
    }
    public void setCool_time() {
        set(COOL_TIME, value);
    }
    public void setHit_cancel_time() {
        set(HIT_CANCEL_TIME, value);
    }
    public void setPower() {
        set(POWER, value);
    }
    public void setEnchant() {
        set(ENCHANT, value);
    }
    public void setReuse() {
        set(REUSE, value);
    }
    public void setOlympiad_use() {
        set(OLYMPIAD_USE, value);
    }
    public void setStatic_hittime() {
        set(STATIC_HITTIME, value);
    }
    public void setStatic_reuse() {
        set(STATIC_REUSE, value);
    }
    public void setAbnormal_time() {
        set(ABNORMAL_TIME, value);
    }
    public void setAbnormal_type() {
        set(ABNORMAL_TYPE, value);
    }
    public void setAbnormal_lv() {
        set(ABNORMAL_LV, value);
    }
    public void setAbnormal_instant() {
        set(ABNORMAL_INSTANT, value);
    }
    public void setBuff_protect_level() {
        set(BUFF_PROTECT_LEVEL, value);
    }
    public void setTrait() {
        set(TRAIT, value);
    }
    public void setLv_bonus_rate() {
        set(LV_BONUS_RATE, value);
    }
    public void setBasic_property() {
        set(BASIC_PROPERTY, value);
    }
    public void setNext_action() {
        set(NEXT_ACTION, value);
    }
    public void setTarget_type() {
        set(TARGET_TYPE, value);
    }
    public void setAffect_scope() {
        set(AFFECT_SCOPE, value);
    }
    public void setAffect_object() {
        set(AFFECT_OBJECT, value);
    }
    public void setAffect_range() {
        set(AFFECT_RANGE, value);
    }
    public void setAffect_limit() {
        set(AFFECT_LIMIT, value);
    }
    public void setFan_range() {
        set(FAN_RANGE, value);
    }
    public void setEffective_range() {
        set(EFFECTIVE_RANGE, value);
    }
    public void setEffect_point() {
        set(EFFECT_POINT, value);
    }
    public void setIrreplaceable_buff() {
        set(IRREPLACEABLE_BUFF, value);
    }
    public void setAbnormal_visual_effect() {
        set(ABNORMAL_VISUAL_EFFECT, value);
    }
    public void setIcon() {
        set(ICON, value);
    }

}
