package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.BonusResource.*;

@Table(
        name = "bonus",
        primary_key = {OBJ_ID,BONUS_NAME},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = ACCOUNT , defValue = "-1"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 30 , name = BONUS_NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.FLOAT , type_size = 0 , name = BONUS_VALUE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = BONUS_EXPIRE_TIME , defValue = "0"),
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
        return get(OBJ_ID);
    }
    public String getAccount() {
        return get(ACCOUNT);
    }
    public String getBonusName() {
        return get(BONUS_NAME);
    }
    public Double getBonusValue() {
        return get(BONUS_VALUE);
    }
    public Integer getBonusExpireTime() {
        return get(BONUS_EXPIRE_TIME);
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
