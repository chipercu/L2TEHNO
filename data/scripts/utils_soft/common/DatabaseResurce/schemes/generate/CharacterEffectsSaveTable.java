package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterEffectsSaveTable.*;

@Table(
        name = "character_effects_save",
        fields = {
                @Field(name = CHAR_OBJ_ID),
                @Field(name = SKILL_ID),
                @Field(name = SKILL_LEVEL),
                @Field(name = EFFECT_COUNT),
                @Field(name = EFFECT_CUR_TIME),
                @Field(name = DURATION),
                @Field(name = ORDER),
                @Field(name = CLASS_INDEX),
        }
)
public class CharacterEffectsSaveTable extends DataBaseTable<CharacterEffectsSaveTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String EFFECT_COUNT = "effect_count";
    public static final String EFFECT_CUR_TIME = "effect_cur_time";
    public static final String DURATION = "duration";
    public static final String ORDER = "order";
    public static final String CLASS_INDEX = "class_index";

    public CharacterEffectsSaveTable() {
super(CharacterEffectsSaveTable.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public Integer getEffectCount() {
        return get(EFFECT_COUNT, Integer.class);
    }
    public Integer getEffectCurTime() {
        return get(EFFECT_CUR_TIME, Integer.class);
    }
    public Integer getDuration() {
        return get(DURATION, Integer.class);
    }
    public Integer getOrder() {
        return get(ORDER, Integer.class);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLevel(Integer value) {
        set(SKILL_LEVEL, value);
    }
    public void setEffectCount(Integer value) {
        set(EFFECT_COUNT, value);
    }
    public void setEffectCurTime(Integer value) {
        set(EFFECT_CUR_TIME, value);
    }
    public void setDuration(Integer value) {
        set(DURATION, value);
    }
    public void setOrder(Integer value) {
        set(ORDER, value);
    }
    public void setClassIndex(Integer value) {
        set(CLASS_INDEX, value);
    }

}
