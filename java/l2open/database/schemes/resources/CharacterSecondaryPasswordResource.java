package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterSecondaryPasswordResource.*;

@Table(
        name = "character_secondary_password",
        primary_key = {ACCOUNT_NAME,VAR},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = ACCOUNT_NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 20 , name = VAR , defValue = ""),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 255 , name = VALUE , defValue = "NULL"),
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
