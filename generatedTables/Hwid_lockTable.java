package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Hwid_lockTable.*;

@Table(
        name = "hwid_lock",
        fields = {
                @Field(name = LOGIN,
                @Field(name = HWID,
        }
)
public class Hwid_lockTable extends DataBaseTable<Hwid_lockTable> {

    public static final String LOGIN = "login";
    public static final String HWID = "hwid";
    public Hwid_lockTable() {
super(Hwid_lockTable.class);
}
    public StringgetLogin() {
        return get(LOGIN, String.class);
    }
    public StringgetHwid() {
        return get(HWID, String.class);
    }

    public void setLogin() {
        set(LOGIN, value);
    }
    public void setHwid() {
        set(HWID, value);
    }

}
