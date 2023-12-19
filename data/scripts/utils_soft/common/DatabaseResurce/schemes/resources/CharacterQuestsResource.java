package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterQuestsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_quests",
        primary_key = {CHAR_ID,NAME,VAR},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 40 , name = NAME , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 20 , name = VAR , defValue = ""),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = VALUE , defValue = "NULL"),
        }
)
public class CharacterQuestsResource extends DataBaseTable<CharacterQuestsResource> {

    public static final String CHAR_ID = "char_id";
    public static final String NAME = "name";
    public static final String VAR = "var";
    public static final String VALUE = "value";

    public CharacterQuestsResource() {
        super(CharacterQuestsResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public String getVar() {
        return get(VAR);
    }
    public String getValue() {
        return get(VALUE);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setVar(String value) {
        set(VAR, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }

}
