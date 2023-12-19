package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SkillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "skills",
        primary_key = {ID,LEVEL},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEVEL , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = LEARN , defValue = "0"),
                @Column(is_null = NO , type = TEXT , type_size = 65535 , name = OPERATE_TYPE , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 100 , name = NAME , defValue = ""),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = IS_MAGIC , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = MAGIC_LEVEL , defValue = "null"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ACTIVATE_RATE , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EFFECT_POINT , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = MP_CONSUME1 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = MP_CONSUME2 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = HP_CONSUME , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CAST_RANGE , defValue = "0"),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = SKILL_HIT_TIME , defValue = "0"),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = COOL_TIME , defValue = "null"),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = HIT_CANCEL_TIME , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POWER , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = ENCHANT , defValue = "0"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = REUSE , defValue = "-1"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = OLYMPIAD_USE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STATIC_HITTIME , defValue = "0"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = STATIC_REUSE , defValue = "0"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = ABNORMAL_TIME , defValue = "-1"),
                @Column(is_null = NO , type = VARCHAR , type_size = 40 , name = ABNORMAL_TYPE , defValue = "none"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ABNORMAL_LV , defValue = "-1"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = ABNORMAL_INSTANT , defValue = "-1"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = BUFF_PROTECT_LEVEL , defValue = "-1"),
                @Column(is_null = NO , type = VARCHAR , type_size = 30 , name = TRAIT , defValue = "trait_none"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LV_BONUS_RATE , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 5 , name = BASIC_PROPERTY , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 10 , name = NEXT_ACTION , defValue = "null"),
                @Column(is_null = NO , type = ENUM , type_size = 17 , name = TARGET_TYPE , defValue = "none"),
                @Column(is_null = NO , type = ENUM , type_size = 19 , name = AFFECT_SCOPE , defValue = "none"),
                @Column(is_null = NO , type = ENUM , type_size = 20 , name = AFFECT_OBJECT , defValue = "none"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AFFECT_RANGE , defValue = "-1"),
                @Column(is_null = NO , type = VARCHAR , type_size = 7 , name = AFFECT_LIMIT , defValue = "0;0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 16 , name = FAN_RANGE , defValue = "0;0;0;0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EFFECTIVE_RANGE , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EFFECT_POINT , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = IRREPLACEABLE_BUFF , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 31 , name = ABNORMAL_VISUAL_EFFECT , defValue = "ave_none"),
                @Column(is_null = NO , type = TEXT , type_size = 65535 , name = ICON , defValue = "null"),
        }
)
public class SkillsResource extends DataBaseTable<SkillsResource> {

    public static final String ID = "id";
    public static final String LEVEL = "level";
    public static final String LEARN = "learn";
    public static final String OPERATE_TYPE = "operate_type";
    public static final String NAME = "name";
    public static final String IS_MAGIC = "is_magic";
    public static final String MAGIC_LEVEL = "magic_level";
    public static final String ACTIVATE_RATE = "activate_rate";
    public static final String EFFECT_POINT = "effectPoint";
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
    public static final String EFFECT_POINT2 = "effect_point";
    public static final String IRREPLACEABLE_BUFF = "irreplaceable_buff";
    public static final String ABNORMAL_VISUAL_EFFECT = "abnormal_visual_effect";
    public static final String ICON = "icon";

    public SkillsResource() {
        super(SkillsResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Integer getLearn() {
        return get(LEARN);
    }
    public String getOperateType() {
        return get(OPERATE_TYPE);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getIsMagic() {
        return get(IS_MAGIC);
    }
    public Integer getMagicLevel() {
        return get(MAGIC_LEVEL);
    }
    public Integer getActivateRate() {
        return get(ACTIVATE_RATE);
    }
    public Integer getEffectPoint() {
        return get(EFFECT_POINT);
    }
    public Integer getMpConsume1() {
        return get(MP_CONSUME1);
    }
    public Integer getMpConsume2() {
        return get(MP_CONSUME2);
    }
    public Integer getHpConsume() {
        return get(HP_CONSUME);
    }
    public Integer getCastRange() {
        return get(CAST_RANGE);
    }
    public Double getSkillHitTime() {
        return get(SKILL_HIT_TIME);
    }
    public Double getCoolTime() {
        return get(COOL_TIME);
    }
    public Double getHitCancelTime() {
        return get(HIT_CANCEL_TIME);
    }
    public Integer getPower() {
        return get(POWER);
    }
    public String getEnchant() {
        return get(ENCHANT);
    }
    public Integer getReuse() {
        return get(REUSE);
    }
    public Integer getOlympiadUse() {
        return get(OLYMPIAD_USE);
    }
    public Integer getStaticHittime() {
        return get(STATIC_HITTIME);
    }
    public Integer getStaticReuse() {
        return get(STATIC_REUSE);
    }
    public Integer getAbnormalTime() {
        return get(ABNORMAL_TIME);
    }
    public String getAbnormalType() {
        return get(ABNORMAL_TYPE);
    }
    public Integer getAbnormalLv() {
        return get(ABNORMAL_LV);
    }
    public Integer getAbnormalInstant() {
        return get(ABNORMAL_INSTANT);
    }
    public Integer getBuffProtectLevel() {
        return get(BUFF_PROTECT_LEVEL);
    }
    public String getTrait() {
        return get(TRAIT);
    }
    public Integer getLvBonusRate() {
        return get(LV_BONUS_RATE);
    }
    public String getBasicProperty() {
        return get(BASIC_PROPERTY);
    }
    public String getNextAction() {
        return get(NEXT_ACTION);
    }
    public String getTargetType() {
        return get(TARGET_TYPE);
    }
    public String getAffectScope() {
        return get(AFFECT_SCOPE);
    }
    public String getAffectObject() {
        return get(AFFECT_OBJECT);
    }
    public Integer getAffectRange() {
        return get(AFFECT_RANGE);
    }
    public String getAffectLimit() {
        return get(AFFECT_LIMIT);
    }
    public String getFanRange() {
        return get(FAN_RANGE);
    }
    public Integer getEffectiveRange() {
        return get(EFFECTIVE_RANGE);
    }
    public Integer getEffectPoint2() {
        return get(EFFECT_POINT2);
    }
    public Integer getIrreplaceableBuff() {
        return get(IRREPLACEABLE_BUFF);
    }
    public String getAbnormalVisualEffect() {
        return get(ABNORMAL_VISUAL_EFFECT);
    }
    public String getIcon() {
        return get(ICON);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setLearn(Integer value) {
        set(LEARN, value);
    }
    public void setOperateType(String value) {
        set(OPERATE_TYPE, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setIsMagic(Integer value) {
        set(IS_MAGIC, value);
    }
    public void setMagicLevel(Integer value) {
        set(MAGIC_LEVEL, value);
    }
    public void setActivateRate(Integer value) {
        set(ACTIVATE_RATE, value);
    }
    public void setEffectPoint(Integer value) {
        set(EFFECT_POINT, value);
    }
    public void setMpConsume1(Integer value) {
        set(MP_CONSUME1, value);
    }
    public void setMpConsume2(Integer value) {
        set(MP_CONSUME2, value);
    }
    public void setHpConsume(Integer value) {
        set(HP_CONSUME, value);
    }
    public void setCastRange(Integer value) {
        set(CAST_RANGE, value);
    }
    public void setSkillHitTime(Double value) {
        set(SKILL_HIT_TIME, value);
    }
    public void setCoolTime(Double value) {
        set(COOL_TIME, value);
    }
    public void setHitCancelTime(Double value) {
        set(HIT_CANCEL_TIME, value);
    }
    public void setPower(Integer value) {
        set(POWER, value);
    }
    public void setEnchant(String value) {
        set(ENCHANT, value);
    }
    public void setReuse(Integer value) {
        set(REUSE, value);
    }
    public void setOlympiadUse(Integer value) {
        set(OLYMPIAD_USE, value);
    }
    public void setStaticHittime(Integer value) {
        set(STATIC_HITTIME, value);
    }
    public void setStaticReuse(Integer value) {
        set(STATIC_REUSE, value);
    }
    public void setAbnormalTime(Integer value) {
        set(ABNORMAL_TIME, value);
    }
    public void setAbnormalType(String value) {
        set(ABNORMAL_TYPE, value);
    }
    public void setAbnormalLv(Integer value) {
        set(ABNORMAL_LV, value);
    }
    public void setAbnormalInstant(Integer value) {
        set(ABNORMAL_INSTANT, value);
    }
    public void setBuffProtectLevel(Integer value) {
        set(BUFF_PROTECT_LEVEL, value);
    }
    public void setTrait(String value) {
        set(TRAIT, value);
    }
    public void setLvBonusRate(Integer value) {
        set(LV_BONUS_RATE, value);
    }
    public void setBasicProperty(String value) {
        set(BASIC_PROPERTY, value);
    }
    public void setNextAction(String value) {
        set(NEXT_ACTION, value);
    }
    public void setTargetType(String value) {
        set(TARGET_TYPE, value);
    }
    public void setAffectScope(String value) {
        set(AFFECT_SCOPE, value);
    }
    public void setAffectObject(String value) {
        set(AFFECT_OBJECT, value);
    }
    public void setAffectRange(Integer value) {
        set(AFFECT_RANGE, value);
    }
    public void setAffectLimit(String value) {
        set(AFFECT_LIMIT, value);
    }
    public void setFanRange(String value) {
        set(FAN_RANGE, value);
    }
    public void setEffectiveRange(Integer value) {
        set(EFFECTIVE_RANGE, value);
    }
    public void setEffectPoint2(Integer value) {
        set(EFFECT_POINT2, value);
    }
    public void setIrreplaceableBuff(Integer value) {
        set(IRREPLACEABLE_BUFF, value);
    }
    public void setAbnormalVisualEffect(String value) {
        set(ABNORMAL_VISUAL_EFFECT, value);
    }
    public void setIcon(String value) {
        set(ICON, value);
    }

}
