package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LockTable.*;

@Table(
        name = "lock",
        fields = {
                @Field(name = LOGIN),
                @Field(name = TYPE),
                @Field(name = STRING),
        }
)
public class LockTable extends DataBaseTable<LockTable> {

    public static final String LOGIN = "login";
    public static final String TYPE = "type";
    public static final String STRING = "string";

    public LockTable() {
super(LockTable.class);
}

    public String getLogin() {
        return get(LOGIN, String.class);
    }
    public Object getType() {
        return get(TYPE, Object.class);
    }
    public String getString() {
        return get(STRING, String.class);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setType(Object value) {
        set(TYPE, value);
    }
    public void setString(String value) {
        set(STRING, value);
    }

}
