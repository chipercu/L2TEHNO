package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillsResource.*;

@Table(
        name = "skills",
        primary_key = {ID,LEVEL},
        fields = {
                @Field(name = ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LEVEL , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LEARN , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = OPERATE_TYPE , data_type = "text"),
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = IS_MAGIC , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = MAGIC_LEVEL , data_type = "smallint"),
                @Field(name = ACTIVATE_RATE , data_type = "smallint"),
                @Field(name = EFFECT_POINT , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = MP_CONSUME1 , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = MP_CONSUME2 , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = HP_CONSUME , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = CAST_RANGE , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_HIT_TIME , data_type = "double" , defValue = @DefValue(Double = 0)),
                @Field(name = COOL_TIME , data_type = "double"),
                @Field(name = HIT_CANCEL_TIME , data_type = "double"),
                @Field(name = POWER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ENCHANT , data_type = "varchar" , defValue = @DefValue(String = "0")),
                @Field(name = REUSE , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = OLYMPIAD_USE , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = STATIC_HITTIME , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = STATIC_REUSE , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = ABNORMAL_TIME , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = ABNORMAL_TYPE , data_type = "varchar" , defValue = @DefValue(String = "none")),
                @Field(name = ABNORMAL_LV , data_type = "smallint" , defValue = @DefValue(Integer = -1)),
                @Field(name = ABNORMAL_INSTANT , data_type = "tinyint" , defValue = @DefValue(Integer = -1)),
                @Field(name = BUFF_PROTECT_LEVEL , data_type = "tinyint" , defValue = @DefValue(Integer = -1)),
                @Field(name = TRAIT , data_type = "varchar" , defValue = @DefValue(String = "trait_none")),
                @Field(name = LV_BONUS_RATE , data_type = "int"),
                @Field(name = BASIC_PROPERTY , data_type = "varchar"),
                @Field(name = NEXT_ACTION , data_type = "varchar"),
                @Field(name = TARGET_TYPE , data_type = "enum" , defValue = @DefValue(String = "none")),
                @Field(name = AFFECT_SCOPE , data_type = "enum" , defValue = @DefValue(String = "none")),
                @Field(name = AFFECT_OBJECT , data_type = "enum" , defValue = @DefValue(String = "none")),
                @Field(name = AFFECT_RANGE , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = AFFECT_LIMIT , data_type = "varchar" , defValue = @DefValue(String = "0;0")),
                @Field(name = FAN_RANGE , data_type = "varchar" , defValue = @DefValue(String = "0;0;0;0")),
                @Field(name = EFFECTIVE_RANGE , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = EFFECT_POINT2 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = IRREPLACEABLE_BUFF , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = ABNORMAL_VISUAL_EFFECT , data_type = "varchar" , defValue = @DefValue(String = "ave_none")),
                @Field(name = ICON , data_type = "text"),
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
        return get(ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Integer getLearn() {
        return get(LEARN, Integer.class);
    }
    public String getOperateType() {
        return get(OPERATE_TYPE, String.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getIsMagic() {
        return get(IS_MAGIC, Integer.class);
    }
    public Integer getMagicLevel() {
        return get(MAGIC_LEVEL, Integer.class);
    }
    public Integer getActivateRate() {
        return get(ACTIVATE_RATE, Integer.class);
    }
    public Integer getEffectPoint() {
        return get(EFFECT_POINT, Integer.class);
    }
    public Integer getMpConsume1() {
        return get(MP_CONSUME1, Integer.class);
    }
    public Integer getMpConsume2() {
        return get(MP_CONSUME2, Integer.class);
    }
    public Integer getHpConsume() {
        return get(HP_CONSUME, Integer.class);
    }
    public Integer getCastRange() {
        return get(CAST_RANGE, Integer.class);
    }
    public Double getSkillHitTime() {
        return get(SKILL_HIT_TIME, Double.class);
    }
    public Double getCoolTime() {
        return get(COOL_TIME, Double.class);
    }
    public Double getHitCancelTime() {
        return get(HIT_CANCEL_TIME, Double.class);
    }
    public Integer getPower() {
        return get(POWER, Integer.class);
    }
    public String getEnchant() {
        return get(ENCHANT, String.class);
    }
    public Integer getReuse() {
        return get(REUSE, Integer.class);
    }
    public Integer getOlympiadUse() {
        return get(OLYMPIAD_USE, Integer.class);
    }
    public Integer getStaticHittime() {
        return get(STATIC_HITTIME, Integer.class);
    }
    public Integer getStaticReuse() {
        return get(STATIC_REUSE, Integer.class);
    }
    public Integer getAbnormalTime() {
        return get(ABNORMAL_TIME, Integer.class);
    }
    public String getAbnormalType() {
        return get(ABNORMAL_TYPE, String.class);
    }
    public Integer getAbnormalLv() {
        return get(ABNORMAL_LV, Integer.class);
    }
    public Integer getAbnormalInstant() {
        return get(ABNORMAL_INSTANT, Integer.class);
    }
    public Integer getBuffProtectLevel() {
        return get(BUFF_PROTECT_LEVEL, Integer.class);
    }
    public String getTrait() {
        return get(TRAIT, String.class);
    }
    public Integer getLvBonusRate() {
        return get(LV_BONUS_RATE, Integer.class);
    }
    public String getBasicProperty() {
        return get(BASIC_PROPERTY, String.class);
    }
    public String getNextAction() {
        return get(NEXT_ACTION, String.class);
    }
    public String getTargetType() {
        return get(TARGET_TYPE, String.class);
    }
    public String getAffectScope() {
        return get(AFFECT_SCOPE, String.class);
    }
    public String getAffectObject() {
        return get(AFFECT_OBJECT, String.class);
    }
    public Integer getAffectRange() {
        return get(AFFECT_RANGE, Integer.class);
    }
    public String getAffectLimit() {
        return get(AFFECT_LIMIT, String.class);
    }
    public String getFanRange() {
        return get(FAN_RANGE, String.class);
    }
    public Integer getEffectiveRange() {
        return get(EFFECTIVE_RANGE, Integer.class);
    }
    public Integer getEffectPoint2() {
        return get(EFFECT_POINT2, Integer.class);
    }
    public Integer getIrreplaceableBuff() {
        return get(IRREPLACEABLE_BUFF, Integer.class);
    }
    public String getAbnormalVisualEffect() {
        return get(ABNORMAL_VISUAL_EFFECT, String.class);
    }
    public String getIcon() {
        return get(ICON, String.class);
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
