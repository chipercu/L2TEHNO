package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSecondaryPasswordResource.*;

@Table(
        name = "character_secondary_password",
        primary_key = {ACCOUNT_NAME,VAR},
        fields = {
                @Field(name = ACCOUNT_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = VAR , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = VALUE , data_type = "varchar"),
        }
)
public class CharacterSecondaryPasswordResource extends DataBaseTable<CharacterSecondaryPasswordResource> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String VAR = "var";
    public static final String VALUE = "value";

    public CharacterSecondaryPasswordResource() {
super(CharacterSecondaryPasswordResource.class);
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
