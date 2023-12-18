package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSubclassesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_subclasses",
        primary_key = {CHAR_OBJ_ID,CLASS_ID},
        fields = {
                @Field(name = CHAR_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = CLASS_ID , type = TINYINT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = LEVEL , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = EXP , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = SP , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = CUR_HP , type = DECIMAL , nullable = false , defValue = @DefValue(Double = 0.0000)),
                @Field(name = CUR_MP , type = DECIMAL , nullable = false , defValue = @DefValue(Double = 0.0000)),
                @Field(name = CUR_CP , type = DECIMAL , nullable = false , defValue = @DefValue(Double = 0.0000)),
                @Field(name = MAX_HP , type = MEDIUMINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MAX_MP , type = MEDIUMINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MAX_CP , type = MEDIUMINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ACTIVE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = IS_BASE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DEATH_PENALTY , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CERTIFICATION , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = 0)),
        }
)
public class CharacterSubclassesResource extends DataBaseTable<CharacterSubclassesResource> {

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

    public CharacterSubclassesResource() {
super(CharacterSubclassesResource.class);
}


    public CharacterSubclassesResource(String char_obj_id,String class_id){
        super(CharacterSubclassesResource.class);
        getSTAT_SET().set(CHAR_OBJ_ID, char_obj_id);
        getSTAT_SET().set(CLASS_ID, class_id);
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
