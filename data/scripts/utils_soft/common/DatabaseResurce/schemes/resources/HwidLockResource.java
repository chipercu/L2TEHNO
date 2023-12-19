package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.HwidLockResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "hwid_lock",
        primary_key = {LOGIN},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = LOGIN , defValue = "null"),
                @Column(is_null = NO , type = TEXT , type_size = 65535 , name = HWID , defValue = "null"),
        }
)
public class HwidLockResource extends DataBaseTable<HwidLockResource> {

    public static final String LOGIN = "login";
    public static final String HWID = "hwid";

    public HwidLockResource() {
        super(HwidLockResource.class);
}

    public String getLogin() {
        return get(LOGIN);
    }
    public String getHwid() {
        return get(HWID);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setHwid(String value) {
        set(HWID, value);
    }

}
