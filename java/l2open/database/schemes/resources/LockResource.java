package l2open.database.schemes.resources;

import l2open.database.anotations.Column;
import l2open.database.anotations.Table;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.LockResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "lock",
        primary_key = {LOGIN,STRING},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = LOGIN , defValue = "null"),
                @Column(is_null = NO , type = ENUM , type_size = 4 , name = TYPE , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 32 , name = STRING , defValue = "null"),
        }
)
public class LockResource extends DataBaseTable<LockResource> {

    public static final String LOGIN = "login";
    public static final String TYPE = "type";
    public static final String STRING = "string";

    public LockResource() {
        super(LockResource.class);
}

    public String getLogin() {
        return get(LOGIN);
    }
    public String getType() {
        return get(TYPE);
    }
    public String getString() {
        return get(STRING);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setType(String value) {
        set(TYPE, value);
    }
    public void setString(String value) {
        set(STRING, value);
    }

}
