package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSubclassesTable.*;

@Table(
        name = "character_subclasses",
        fields = {
                @Field(name = CHAR_OBJ_ID),
                @Field(name = CLASS_ID),
                @Field(name = LEVEL),
                @Field(name = EXP),
                @Field(name = SP),
                @Field(name = CUR_HP),
                @Field(name = CUR_MP),
                @Field(name = CUR_CP),
                @Field(name = MAX_HP),
                @Field(name = MAX_MP),
                @Field(name = MAX_CP),
                @Field(name = ACTIVE),
                @Field(name = IS_BASE),
                @Field(name = DEATH_PENALTY),
                @Field(name = CERTIFICATION),
        }
)
public class CharacterSubclassesTable extends DataBaseTable<CharacterSubclassesTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String CLASS_ID = "class_id";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String SP = "sp";
    public static final String CUR_HP = "curHp";
    public static final String CUR_MP = "curMp";
    public static final String CUR_CP = "curCp";
    public static final String MAX_HP = "maxHp";
    public static final String MAX_MP = "maxMp";
    public static final String MAX_CP = "maxCp";
    public static final String ACTIVE = "active";
    public static final String IS_BASE = "isBase";
    public static final String DEATH_PENALTY = "death_penalty";
    public static final String CERTIFICATION = "certification";

    public CharacterSubclassesTable() {
super(CharacterSubclassesTable.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Long getExp() {
        return get(EXP, Long.class);
    }
    public Long getSp() {
        return get(SP, Long.class);
    }
    public Double getCurHp() {
        return get(CUR_HP, Double.class);
    }
    public Double getCurMp() {
        return get(CUR_MP, Double.class);
    }
    public Double getCurCp() {
        return get(CUR_CP, Double.class);
    }
    public Integer getMaxHp() {
        return get(MAX_HP, Integer.class);
    }
    public Integer getMaxMp() {
        return get(MAX_MP, Integer.class);
    }
    public Integer getMaxCp() {
        return get(MAX_CP, Integer.class);
    }
    public Integer getActive() {
        return get(ACTIVE, Integer.class);
    }
    public Integer getIsBase() {
        return get(IS_BASE, Integer.class);
    }
    public Integer getDeathPenalty() {
        return get(DEATH_PENALTY, Integer.class);
    }
    public Integer getCertification() {
        return get(CERTIFICATION, Integer.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setExp(Long value) {
        set(EXP, value);
    }
    public void setSp(Long value) {
        set(SP, value);
    }
    public void setCurHp(Double value) {
        set(CUR_HP, value);
    }
    public void setCurMp(Double value) {
        set(CUR_MP, value);
    }
    public void setCurCp(Double value) {
        set(CUR_CP, value);
    }
    public void setMaxHp(Integer value) {
        set(MAX_HP, value);
    }
    public void setMaxMp(Integer value) {
        set(MAX_MP, value);
    }
    public void setMaxCp(Integer value) {
        set(MAX_CP, value);
    }
    public void setActive(Integer value) {
        set(ACTIVE, value);
    }
    public void setIsBase(Integer value) {
        set(IS_BASE, value);
    }
    public void setDeathPenalty(Integer value) {
        set(DEATH_PENALTY, value);
    }
    public void setCertification(Integer value) {
        set(CERTIFICATION, value);
    }

}