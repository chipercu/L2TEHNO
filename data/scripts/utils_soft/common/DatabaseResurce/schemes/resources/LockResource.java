package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.LockResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

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
