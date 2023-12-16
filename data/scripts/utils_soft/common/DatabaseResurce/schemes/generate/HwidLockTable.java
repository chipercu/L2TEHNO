package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HwidLockTable.*;

@Table(
        name = "hwid_lock",
        fields = {
                @Field(name = LOGIN),
                @Field(name = HWID),
        }
)
public class HwidLockTable extends DataBaseTable<HwidLockTable> {

    public static final String LOGIN = "login";
    public static final String HWID = "hwid";

    public HwidLockTable() {
super(HwidLockTable.class);
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
