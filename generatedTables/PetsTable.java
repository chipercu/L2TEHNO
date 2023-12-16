package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.PetsTable.*;

@Table(
        name = "pets",
        fields = {
                @Field(name = ITEM_OBJ_ID,
                @Field(name = OBJID,
                @Field(name = NAME,
                @Field(name = LEVEL,
                @Field(name = CURHP,
                @Field(name = CURMP,
                @Field(name = EXP,
                @Field(name = SP,
                @Field(name = FED,
                @Field(name = MAX_FED,
        }
)
public class PetsTable extends DataBaseTable<PetsTable> {

    public static final String ITEM_OBJ_ID = "item_obj_id";
    public static final String OBJID = "objId";
    public static final String NAME = "name";
    public static final String LEVEL = "level";
    public static final String CURHP = "curHp";
    public static final String CURMP = "curMp";
    public static final String EXP = "exp";
    public static final String SP = "sp";
    public static final String FED = "fed";
    public static final String MAX_FED = "max_fed";
    public PetsTable() {
super(PetsTable.class);
}
    public IntegergetItem_obj_id() {
        return get(ITEM_OBJ_ID, Integer.class);
    }
    public IntegergetObjId() {
        return get(OBJID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public IntegergetCurHp() {
        return get(CURHP, Integer.class);
    }
    public IntegergetCurMp() {
        return get(CURMP, Integer.class);
    }
    public ObjectgetExp() {
        return get(EXP, Object.class);
    }
    public IntegergetSp() {
        return get(SP, Integer.class);
    }
    public IntegergetFed() {
        return get(FED, Integer.class);
    }
    public IntegergetMax_fed() {
        return get(MAX_FED, Integer.class);
    }

    public void setItem_obj_id() {
        set(ITEM_OBJ_ID, value);
    }
    public void setObjId() {
        set(OBJID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setCurHp() {
        set(CURHP, value);
    }
    public void setCurMp() {
        set(CURMP, value);
    }
    public void setExp() {
        set(EXP, value);
    }
    public void setSp() {
        set(SP, value);
    }
    public void setFed() {
        set(FED, value);
    }
    public void setMax_fed() {
        set(MAX_FED, value);
    }

}
