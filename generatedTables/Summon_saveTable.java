package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Summon_saveTable.*;

@Table(
        name = "summon_save",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = CLASS_ID,
                @Field(name = ITEM_OBJ_ID,
                @Field(name = NPC_ID,
                @Field(name = LIFE_TIME,
                @Field(name = ITEM_CONSUME_IDINTIME,
                @Field(name = ITEM_CONSUME_COUNTINTIME,
                @Field(name = ITEM_CONSUME_DELAY,
                @Field(name = EXP_PENALTY,
        }
)
public class Summon_saveTable extends DataBaseTable<Summon_saveTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String CLASS_ID = "class_id";
    public static final String ITEM_OBJ_ID = "item_obj_id";
    public static final String NPC_ID = "npc_id";
    public static final String LIFE_TIME = "life_time";
    public static final String ITEM_CONSUME_IDINTIME = "item_consume_idInTime";
    public static final String ITEM_CONSUME_COUNTINTIME = "item_consume_countInTime";
    public static final String ITEM_CONSUME_DELAY = "item_consume_delay";
    public static final String EXP_PENALTY = "exp_penalty";
    public Summon_saveTable() {
super(Summon_saveTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }
    public IntegergetItem_obj_id() {
        return get(ITEM_OBJ_ID, Integer.class);
    }
    public IntegergetNpc_id() {
        return get(NPC_ID, Integer.class);
    }
    public IntegergetLife_time() {
        return get(LIFE_TIME, Integer.class);
    }
    public IntegergetItem_consume_idInTime() {
        return get(ITEM_CONSUME_IDINTIME, Integer.class);
    }
    public IntegergetItem_consume_countInTime() {
        return get(ITEM_CONSUME_COUNTINTIME, Integer.class);
    }
    public IntegergetItem_consume_delay() {
        return get(ITEM_CONSUME_DELAY, Integer.class);
    }
    public DoublegetExp_penalty() {
        return get(EXP_PENALTY, Double.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setClass_id() {
        set(CLASS_ID, value);
    }
    public void setItem_obj_id() {
        set(ITEM_OBJ_ID, value);
    }
    public void setNpc_id() {
        set(NPC_ID, value);
    }
    public void setLife_time() {
        set(LIFE_TIME, value);
    }
    public void setItem_consume_idInTime() {
        set(ITEM_CONSUME_IDINTIME, value);
    }
    public void setItem_consume_countInTime() {
        set(ITEM_CONSUME_COUNTINTIME, value);
    }
    public void setItem_consume_delay() {
        set(ITEM_CONSUME_DELAY, value);
    }
    public void setExp_penalty() {
        set(EXP_PENALTY, value);
    }

}
