package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SummonEffectsSaveResource.*;

@Table(
        name = "summon_effects_save",
        primary_key = {CHAR_OBJ_ID,NPC_ID,SKILL_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.MEDIUMINT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SKILL_LEVEL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = EFFECT_COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = EFFECT_CUR_TIME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = DURATION , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ORDER , defValue = "0"),
        }
)
public class SummonEffectsSaveResource extends DataBaseTable<SummonEffectsSaveResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String NPC_ID = "npc_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String EFFECT_COUNT = "effect_count";
    public static final String EFFECT_CUR_TIME = "effect_cur_time";
    public static final String DURATION = "duration";
    public static final String ORDER = "order";

    public SummonEffectsSaveResource() {
        super(SummonEffectsSaveResource.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL);
    }
    public Integer getEffectCount() {
        return get(EFFECT_COUNT);
    }
    public Integer getEffectCurTime() {
        return get(EFFECT_CUR_TIME);
    }
    public Integer getDuration() {
        return get(DURATION);
    }
    public Integer getOrder() {
        return get(ORDER);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setNpcId(Integer value) {
        set(NPC_ID, value);
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

}