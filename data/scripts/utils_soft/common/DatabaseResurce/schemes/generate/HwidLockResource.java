package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HwidLockResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "hwid_lock",
        primary_key = {LOGIN},
        fields = {
                @Field(name = LOGIN , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = HWID , type = TEXT , type_size = 65535 , nullable = false , defValue = @DefValue(String = "")),
        }
)
public class HwidLockResource extends DataBaseTable<HwidLockResource> {

    public static final String LOGIN = "login";
    public static final String HWID = "hwid";

    public HwidLockResource() {
super(HwidLockResource.class);
}


    public HwidLockResource(String login){
        super(HwidLockResource.class);
        getSTAT_SET().set(LOGIN, login);
    }

    public String getLogin() {
        return get(LOGIN, String.class);
    }
    public String getHwid() {
        return get(HWID, String.class);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setHwid(String value) {
        set(HWID, value);
    }

}
