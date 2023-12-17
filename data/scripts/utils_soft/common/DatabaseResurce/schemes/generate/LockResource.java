package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LockResource.*;

@Table(
        name = "lock",
        primary_key = {LOGIN,STRING},
        fields = {
                @Field(name = LOGIN , data_type = "varchar"),
                @Field(name = TYPE , data_type = "enum"),
                @Field(name = STRING , data_type = "varchar"),
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
        return get(LOGIN, String.class);
    }
    public String getType() {
        return get(TYPE, String.class);
    }
    public String getString() {
        return get(STRING, String.class);
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
