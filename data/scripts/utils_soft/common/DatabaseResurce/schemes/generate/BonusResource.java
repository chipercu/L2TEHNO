package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.BonusResource.*;

@Table(
        name = "bonus",
        primary_key = {OBJ_ID,BONUS_NAME},
        fields = {
                @Field(name = OBJ_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ACCOUNT , data_type = "varchar" , defValue = @DefValue(String = "-1")),
                @Field(name = BONUS_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = BONUS_VALUE , data_type = "float" , defValue = @DefValue(Double = 0)),
                @Field(name = BONUS_EXPIRE_TIME , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class BonusResource extends DataBaseTable<BonusResource> {

    public static final String OBJ_ID = "obj_id";
    public static final String ACCOUNT = "account";
    public static final String BONUS_NAME = "bonus_name";
    public static final String BONUS_VALUE = "bonus_value";
    public static final String BONUS_EXPIRE_TIME = "bonus_expire_time";

    public BonusResource() {
super(BonusResource.class);
}

    public Integer getObjId() {
        return get(OBJ_ID, Integer.class);
    }
    public String getAccount() {
        return get(ACCOUNT, String.class);
    }
    public String getBonusName() {
        return get(BONUS_NAME, String.class);
    }
    public Double getBonusValue() {
        return get(BONUS_VALUE, Double.class);
    }
    public Integer getBonusExpireTime() {
        return get(BONUS_EXPIRE_TIME, Integer.class);
    }

    public void setObjId(Integer value) {
        set(OBJ_ID, value);
    }
    public void setAccount(String value) {
        set(ACCOUNT, value);
    }
    public void setBonusName(String value) {
        set(BONUS_NAME, value);
    }
    public void setBonusValue(Double value) {
        set(BONUS_VALUE, value);
    }
    public void setBonusExpireTime(Integer value) {
        set(BONUS_EXPIRE_TIME, value);
    }

}
