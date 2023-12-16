package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_subclassesTable.*;

@Table(
        name = "character_subclasses",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = CLASS_ID,
                @Field(name = LEVEL,
                @Field(name = EXP,
                @Field(name = SP,
                @Field(name = CURHP,
                @Field(name = CURMP,
                @Field(name = CURCP,
                @Field(name = MAXHP,
                @Field(name = MAXMP,
                @Field(name = MAXCP,
                @Field(name = ACTIVE,
                @Field(name = ISBASE,
                @Field(name = DEATH_PENALTY,
                @Field(name = CERTIFICATION,
        }
)
public class Character_subclassesTable extends DataBaseTable<Character_subclassesTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String CLASS_ID = "class_id";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String SP = "sp";
    public static final String CURHP = "curHp";
    public static final String CURMP = "curMp";
    public static final String CURCP = "curCp";
    public static final String MAXHP = "maxHp";
    public static final String MAXMP = "maxMp";
    public static final String MAXCP = "maxCp";
    public static final String ACTIVE = "active";
    public static final String ISBASE = "isBase";
    public static final String DEATH_PENALTY = "death_penalty";
    public static final String CERTIFICATION = "certification";
    public Character_subclassesTable() {
super(Character_subclassesTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public ObjectgetExp() {
        return get(EXP, Object.class);
    }
    public ObjectgetSp() {
        return get(SP, Object.class);
    }
    public DoublegetCurHp() {
        return get(CURHP, Double.class);
    }
    public DoublegetCurMp() {
        return get(CURMP, Double.class);
    }
    public DoublegetCurCp() {
        return get(CURCP, Double.class);
    }
    public IntegergetMaxHp() {
        return get(MAXHP, Integer.class);
    }
    public IntegergetMaxMp() {
        return get(MAXMP, Integer.class);
    }
    public IntegergetMaxCp() {
        return get(MAXCP, Integer.class);
    }
    public IntegergetActive() {
        return get(ACTIVE, Integer.class);
    }
    public IntegergetIsBase() {
        return get(ISBASE, Integer.class);
    }
    public IntegergetDeath_penalty() {
        return get(DEATH_PENALTY, Integer.class);
    }
    public IntegergetCertification() {
        return get(CERTIFICATION, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setClass_id() {
        set(CLASS_ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setExp() {
        set(EXP, value);
    }
    public void setSp() {
        set(SP, value);
    }
    public void setCurHp() {
        set(CURHP, value);
    }
    public void setCurMp() {
        set(CURMP, value);
    }
    public void setCurCp() {
        set(CURCP, value);
    }
    public void setMaxHp() {
        set(MAXHP, value);
    }
    public void setMaxMp() {
        set(MAXMP, value);
    }
    public void setMaxCp() {
        set(MAXCP, value);
    }
    public void setActive() {
        set(ACTIVE, value);
    }
    public void setIsBase() {
        set(ISBASE, value);
    }
    public void setDeath_penalty() {
        set(DEATH_PENALTY, value);
    }
    public void setCertification() {
        set(CERTIFICATION, value);
    }

}
