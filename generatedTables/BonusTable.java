package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.BonusTable.*;

@Table(
        name = "bonus",
        fields = {
                @Field(name = OBJ_ID,
                @Field(name = ACCOUNT,
                @Field(name = BONUS_NAME,
                @Field(name = BONUS_VALUE,
                @Field(name = BONUS_EXPIRE_TIME,
        }
)
public class BonusTable extends DataBaseTable<BonusTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String ACCOUNT = "account";
    public static final String BONUS_NAME = "bonus_name";
    public static final String BONUS_VALUE = "bonus_value";
    public static final String BONUS_EXPIRE_TIME = "bonus_expire_time";
    public BonusTable() {
super(BonusTable.class);
}
    public IntegergetObj_id() {
        return get(OBJ_ID, Integer.class);
    }
    public StringgetAccount() {
        return get(ACCOUNT, String.class);
    }
    public StringgetBonus_name() {
        return get(BONUS_NAME, String.class);
    }
    public ObjectgetBonus_value() {
        return get(BONUS_VALUE, Object.class);
    }
    public IntegergetBonus_expire_time() {
        return get(BONUS_EXPIRE_TIME, Integer.class);
    }

    public void setObj_id() {
        set(OBJ_ID, value);
    }
    public void setAccount() {
        set(ACCOUNT, value);
    }
    public void setBonus_name() {
        set(BONUS_NAME, value);
    }
    public void setBonus_value() {
        set(BONUS_VALUE, value);
    }
    public void setBonus_expire_time() {
        set(BONUS_EXPIRE_TIME, value);
    }

}
