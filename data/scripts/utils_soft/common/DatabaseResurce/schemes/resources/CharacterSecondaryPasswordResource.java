package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterSecondaryPasswordResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_secondary_password",
        primary_key = {ACCOUNT_NAME,VAR},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = ACCOUNT_NAME , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 20 , name = VAR , defValue = ""),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = VALUE , defValue = "NULL"),
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
        return get(ACCOUNT_NAME);
    }
    public String getVar() {
        return get(VAR);
    }
    public String getValue() {
        return get(VALUE);
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
