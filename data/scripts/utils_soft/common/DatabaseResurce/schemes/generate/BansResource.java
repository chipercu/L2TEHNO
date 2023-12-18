package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.BansResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "bans",
        primary_key = {},
        fields = {
                @Field(name = ACCOUNT_NAME , type = VARCHAR , type_size = 45 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = OBJ__ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BANED , type = VARCHAR , type_size = 20 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = UNBAN , type = VARCHAR , type_size = 20 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = REASON , type = VARCHAR , type_size = 200 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = G_M , type = VARCHAR , type_size = 35 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = ENDBAN , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = KARMA , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
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
        return get(ACCOUNT_NAME, String.class);
    }
    public Integer getObjId() {
        return get(OBJ__ID, Integer.class);
    }
    public String getBaned() {
        return get(BANED, String.class);
    }
    public String getUnban() {
        return get(UNBAN, String.class);
    }
    public String getReason() {
        return get(REASON, String.class);
    }
    public String getGM() {
        return get(G_M, String.class);
    }
    public Integer getEndban() {
        return get(ENDBAN, Integer.class);
    }
    public Integer getKarma() {
        return get(KARMA, Integer.class);
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
