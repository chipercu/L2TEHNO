package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_secondary_passwordTable.*;

@Table(
        name = "character_secondary_password",
        fields = {
                @Field(name = ACCOUNT_NAME,
                @Field(name = VAR,
                @Field(name = VALUE,
        }
)
public class Character_secondary_passwordTable extends DataBaseTable<Character_secondary_passwordTable> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String VAR = "var";
    public static final String VALUE = "value";
    public Character_secondary_passwordTable() {
super(Character_secondary_passwordTable.class);
}
    public StringgetAccount_name() {
        return get(ACCOUNT_NAME, String.class);
    }
    public StringgetVar() {
        return get(VAR, String.class);
    }
    public StringgetValue() {
        return get(VALUE, String.class);
    }

    public void setAccount_name() {
        set(ACCOUNT_NAME, value);
    }
    public void setVar() {
        set(VAR, value);
    }
    public void setValue() {
        set(VALUE, value);
    }

}
