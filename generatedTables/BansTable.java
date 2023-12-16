package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.BansTable.*;

@Table(
        name = "bans",
        fields = {
                @Field(name = ACCOUNT_NAME,
                @Field(name = OBJ_ID,
                @Field(name = BANED,
                @Field(name = UNBAN,
                @Field(name = REASON,
                @Field(name = GM,
                @Field(name = ENDBAN,
                @Field(name = KARMA,
        }
)
public class BansTable extends DataBaseTable<BansTable> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String OBJ_ID = "obj_Id";
    public static final String BANED = "baned";
    public static final String UNBAN = "unban";
    public static final String REASON = "reason";
    public static final String GM = "GM";
    public static final String ENDBAN = "endban";
    public static final String KARMA = "karma";
    public BansTable() {
super(BansTable.class);
}
    public StringgetAccount_name() {
        return get(ACCOUNT_NAME, String.class);
    }
    public IntegergetObj_Id() {
        return get(OBJ_ID, Integer.class);
    }
    public StringgetBaned() {
        return get(BANED, String.class);
    }
    public StringgetUnban() {
        return get(UNBAN, String.class);
    }
    public StringgetReason() {
        return get(REASON, String.class);
    }
    public StringgetGM() {
        return get(GM, String.class);
    }
    public IntegergetEndban() {
        return get(ENDBAN, Integer.class);
    }
    public IntegergetKarma() {
        return get(KARMA, Integer.class);
    }

    public void setAccount_name() {
        set(ACCOUNT_NAME, value);
    }
    public void setObj_Id() {
        set(OBJ_ID, value);
    }
    public void setBaned() {
        set(BANED, value);
    }
    public void setUnban() {
        set(UNBAN, value);
    }
    public void setReason() {
        set(REASON, value);
    }
    public void setGM() {
        set(GM, value);
    }
    public void setEndban() {
        set(ENDBAN, value);
    }
    public void setKarma() {
        set(KARMA, value);
    }

}
