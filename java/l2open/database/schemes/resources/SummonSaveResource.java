package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SummonSaveResource.*;

@Table(
        name = "summon_save",
        primary_key = {CHAR_OBJ_ID,NPC_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LIFE_TIME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_CONSUME_ID_IN_TIME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_CONSUME_COUNT_IN_TIME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_CONSUME_DELAY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DOUBLE , type_size = 0 , name = EXP_PENALTY , defValue = "0"),
        }
)
public class SummonSaveResource extends DataBaseTable<SummonSaveResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String CLASS_ID = "class_id";
    public static final String ITEM_OBJ_ID = "item_obj_id";
    public static final String NPC_ID = "npc_id";
    public static final String LIFE_TIME = "life_time";
    public static final String ITEM_CONSUME_ID_IN_TIME = "item_consume_idInTime";
    public static final String ITEM_CONSUME_COUNT_IN_TIME = "item_consume_countInTime";
    public static final String ITEM_CONSUME_DELAY = "item_consume_delay";
    public static final String EXP_PENALTY = "exp_penalty";

    public SummonSaveResource() {
        super(SummonSaveResource.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public Integer getItemObjId() {
        return get(ITEM_OBJ_ID);
    }
    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getLifeTime() {
        return get(LIFE_TIME);
    }
    public Integer getItemConsumeIdInTime() {
        return get(ITEM_CONSUME_ID_IN_TIME);
    }
    public Integer getItemConsumeCountInTime() {
        return get(ITEM_CONSUME_COUNT_IN_TIME);
    }
    public Integer getItemConsumeDelay() {
        return get(ITEM_CONSUME_DELAY);
    }
    public Double getExpPenalty() {
        return get(EXP_PENALTY);
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
