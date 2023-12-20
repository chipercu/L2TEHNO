package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.HwidLockResource.*;

@Table(
        name = "hwid_lock",
        primary_key = {LOGIN},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = LOGIN , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = HWID , defValue = "null"),
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
