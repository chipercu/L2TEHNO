package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.CharacterSubclassesResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "character_subclasses",
        primary_key = {CHAR_OBJ_ID,CLASS_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CLASS_ID , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = LEVEL , defValue = "1"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = EXP , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = SP , defValue = "0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = CUR_HP , defValue = "0.0000"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = CUR_MP , defValue = "0.0000"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = CUR_CP , defValue = "0.0000"),
                @Column(is_null = NO , type = MEDIUMINT , type_size = 0 , name = MAX_HP , defValue = "0"),
                @Column(is_null = NO , type = MEDIUMINT , type_size = 0 , name = MAX_MP , defValue = "0"),
                @Column(is_null = NO , type = MEDIUMINT , type_size = 0 , name = MAX_CP , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ACTIVE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = IS_BASE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = DEATH_PENALTY , defValue = "0"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = CERTIFICATION , defValue = "0"),
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

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Long getExp() {
        return get(EXP);
    }
    public Long getSp() {
        return get(SP);
    }
    public Double getCurHp() {
        return get(CUR_HP);
    }
    public Double getCurMp() {
        return get(CUR_MP);
    }
    public Double getCurCp() {
        return get(CUR_CP);
    }
    public Integer getMaxHp() {
        return get(MAX_HP);
    }
    public Integer getMaxMp() {
        return get(MAX_MP);
    }
    public Integer getMaxCp() {
        return get(MAX_CP);
    }
    public Integer getActive() {
        return get(ACTIVE);
    }
    public Integer getIsBase() {
        return get(IS_BASE);
    }
    public Integer getDeathPenalty() {
        return get(DEATH_PENALTY);
    }
    public Integer getCertification() {
        return get(CERTIFICATION);
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
