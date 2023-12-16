package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SummonSaveTable.*;

@Table(
        name = "summon_save",
        fields = {
                @Field(name = CHAR_OBJ_ID),
                @Field(name = CLASS_ID),
                @Field(name = ITEM_OBJ_ID),
                @Field(name = NPC_ID),
                @Field(name = LIFE_TIME),
                @Field(name = ITEM_CONSUME_ID_IN_TIME),
                @Field(name = ITEM_CONSUME_COUNT_IN_TIME),
                @Field(name = ITEM_CONSUME_DELAY),
                @Field(name = EXP_PENALTY),
        }
)
public class SummonSaveTable extends DataBaseTable<SummonSaveTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String CLASS_ID = "class_id";
    public static final String ITEM_OBJ_ID = "item_obj_id";
    public static final String NPC_ID = "npc_id";
    public static final String LIFE_TIME = "life_time";
    public static final String ITEM_CONSUME_ID_IN_TIME = "item_consume_idInTime";
    public static final String ITEM_CONSUME_COUNT_IN_TIME = "item_consume_countInTime";
    public static final String ITEM_CONSUME_DELAY = "item_consume_delay";
    public static final String EXP_PENALTY = "exp_penalty";

    public SummonSaveTable() {
super(SummonSaveTable.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public Integer getItemObjId() {
        return get(ITEM_OBJ_ID, Integer.class);
    }
    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public Integer getLifeTime() {
        return get(LIFE_TIME, Integer.class);
    }
    public Integer getItemConsumeIdInTime() {
        return get(ITEM_CONSUME_ID_IN_TIME, Integer.class);
    }
    public Integer getItemConsumeCountInTime() {
        return get(ITEM_CONSUME_COUNT_IN_TIME, Integer.class);
    }
    public Integer getItemConsumeDelay() {
        return get(ITEM_CONSUME_DELAY, Integer.class);
    }
    public Double getExpPenalty() {
        return get(EXP_PENALTY, Double.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setItemObjId(Integer value) {
        set(ITEM_OBJ_ID, value);
    }
    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setLifeTime(Integer value) {
        set(LIFE_TIME, value);
    }
    public void setItemConsumeIdInTime(Integer value) {
        set(ITEM_CONSUME_ID_IN_TIME, value);
    }
    public void setItemConsumeCountInTime(Integer value) {
        set(ITEM_CONSUME_COUNT_IN_TIME, value);
    }
    public void setItemConsumeDelay(Integer value) {
        set(ITEM_CONSUME_DELAY, value);
    }
    public void setExpPenalty(Double value) {
        set(EXP_PENALTY, value);
    }

}