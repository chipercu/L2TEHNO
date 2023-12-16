package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSecondaryPasswordTable.*;

@Table(
        name = "character_secondary_password",
        fields = {
                @Field(name = ACCOUNT_NAME),
                @Field(name = VAR),
                @Field(name = VALUE),
        }
)
public class CharacterSecondaryPasswordTable extends DataBaseTable<CharacterSecondaryPasswordTable> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String VAR = "var";
    public static final String VALUE = "value";

    public CharacterSecondaryPasswordTable() {
super(CharacterSecondaryPasswordTable.class);
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
