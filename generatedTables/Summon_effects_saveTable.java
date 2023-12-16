package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Summon_effects_saveTable.*;

@Table(
        name = "summon_effects_save",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = NPC_ID,
                @Field(name = SKILL_ID,
                @Field(name = SKILL_LEVEL,
                @Field(name = EFFECT_COUNT,
                @Field(name = EFFECT_CUR_TIME,
                @Field(name = DURATION,
                @Field(name = ORDER,
        }
)
public class Summon_effects_saveTable extends DataBaseTable<Summon_effects_saveTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String NPC_ID = "npc_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String EFFECT_COUNT = "effect_count";
    public static final String EFFECT_CUR_TIME = "effect_cur_time";
    public static final String DURATION = "duration";
    public static final String ORDER = "order";
    public Summon_effects_saveTable() {
super(Summon_effects_saveTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetNpc_id() {
        return get(NPC_ID, Integer.class);
    }
    public IntegergetSkill_id() {
        return get(SKILL_ID, Integer.class);
    }
    public IntegergetSkill_level() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public IntegergetEffect_count() {
        return get(EFFECT_COUNT, Integer.class);
    }
    public IntegergetEffect_cur_time() {
        return get(EFFECT_CUR_TIME, Integer.class);
    }
    public IntegergetDuration() {
        return get(DURATION, Integer.class);
    }
    public IntegergetOrder() {
        return get(ORDER, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setNpc_id() {
        set(NPC_ID, value);
    }
    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setSkill_level() {
        set(SKILL_LEVEL, value);
    }
    public void setEffect_count() {
        set(EFFECT_COUNT, value);
    }
    public void setEffect_cur_time() {
        set(EFFECT_CUR_TIME, value);
    }
    public void setDuration() {
        set(DURATION, value);
    }
    public void setOrder() {
        set(ORDER, value);
    }

}
