package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LockResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "lock",
        primary_key = {LOGIN,STRING},
        fields = {
                @Field(name = LOGIN , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = TYPE , type = ENUM , type_size = 4 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = STRING , type = VARCHAR , type_size = 32 , nullable = false , defValue = @DefValue(String = "")),
        }
)
public class LockResource extends DataBaseTable<LockResource> {

    public static final String LOGIN = "login";
    public static final String TYPE = "type";
    public static final String STRING = "string";

    public LockResource() {
super(LockResource.class);
}


    public LockResource(String login,String string){
        super(LockResource.class);
        getSTAT_SET().set(LOGIN, login);
        getSTAT_SET().set(STRING, string);
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
