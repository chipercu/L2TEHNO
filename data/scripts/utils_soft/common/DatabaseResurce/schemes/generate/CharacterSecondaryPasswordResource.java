package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSecondaryPasswordResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_secondary_password",
        primary_key = {ACCOUNT_NAME,VAR},
        fields = {
                @Field(name = ACCOUNT_NAME , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = VAR , type = VARCHAR , type_size = 20 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = VALUE , type = VARCHAR , type_size = 255 , nullable = true , defValue = @DefValue(String = "")),
        }
)
public class CharacterSecondaryPasswordResource extends DataBaseTable<CharacterSecondaryPasswordResource> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String VAR = "var";
    public static final String VALUE = "value";

    public CharacterSecondaryPasswordResource() {
super(CharacterSecondaryPasswordResource.class);
}


    public CharacterSecondaryPasswordResource(String account_name,String var){
        super(CharacterSecondaryPasswordResource.class);
        getSTAT_SET().set(ACCOUNT_NAME, account_name);
        getSTAT_SET().set(VAR, var);
    }

    public String getAccountName() {
        return get(ACCOUNT_NAME, String.class);
    }
    public String getVar() {
        return get(VAR, String.class);
    }
    public String getValue() {
        return get(VALUE, String.class);
    }

    public void setAccountName(String value) {
        set(ACCOUNT_NAME, value);
    }
    public void setVar(String value) {
        set(VAR, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }

}
