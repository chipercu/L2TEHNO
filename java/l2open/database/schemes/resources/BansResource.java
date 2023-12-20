package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.BansResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "bans",
        primary_key = {},
        fields = {
                @Column(is_null = YES , type = VARCHAR , type_size = 45 , name = ACCOUNT_NAME , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJ__ID , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 20 , name = BANED , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 20 , name = UNBAN , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 200 , name = REASON , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 35 , name = G_M , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = ENDBAN , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = KARMA , defValue = "NULL"),
        }
)
public class BansResource extends DataBaseTable<BansResource> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String OBJ__ID = "obj_Id";
    public static final String BANED = "baned";
    public static final String UNBAN = "unban";
    public static final String REASON = "reason";
    public static final String G_M = "GM";
    public static final String ENDBAN = "endban";
    public static final String KARMA = "karma";

    public BansResource() {
        super(BansResource.class);
}

    public String getAccountName() {
        return get(ACCOUNT_NAME);
    }
    public Integer getObjId() {
        return get(OBJ__ID);
    }
    public String getBaned() {
        return get(BANED);
    }
    public String getUnban() {
        return get(UNBAN);
    }
    public String getReason() {
        return get(REASON);
    }
    public String getGM() {
        return get(G_M);
    }
    public Integer getEndban() {
        return get(ENDBAN);
    }
    public Integer getKarma() {
        return get(KARMA);
    }

    public void setAccountName(String value) {
        set(ACCOUNT_NAME, value);
    }
    public void setObjId(Integer value) {
        set(OBJ__ID, value);
    }
    public void setBaned(String value) {
        set(BANED, value);
    }
    public void setUnban(String value) {
        set(UNBAN, value);
    }
    public void setReason(String value) {
        set(REASON, value);
    }
    public void setGM(String value) {
        set(G_M, value);
    }
    public void setEndban(Integer value) {
        set(ENDBAN, value);
    }
    public void setKarma(Integer value) {
        set(KARMA, value);
    }

}
