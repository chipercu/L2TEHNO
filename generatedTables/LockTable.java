package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.LockTable.*;

@Table(
        name = "lock",
        fields = {
                @Field(name = LOGIN,
                @Field(name = TYPE,
                @Field(name = STRING,
        }
)
public class LockTable extends DataBaseTable<LockTable> {

    public static final String LOGIN = "login";
    public static final String TYPE = "type";
    public static final String STRING = "string";
    public LockTable() {
super(LockTable.class);
}
    public StringgetLogin() {
        return get(LOGIN, String.class);
    }
    public ObjectgetType() {
        return get(TYPE, Object.class);
    }
    public StringgetString() {
        return get(STRING, String.class);
    }

    public void setLogin() {
        set(LOGIN, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setString() {
        set(STRING, value);
    }

}
