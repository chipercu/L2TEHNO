package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterQuestsResource.*;

@Table(
        name = "character_quests",
        primary_key = {CHAR_ID,NAME,VAR},
        fields = {
                @Field(name = CHAR_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = VAR , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = VALUE , data_type = "varchar"),
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
        return get(CHAR_ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getVar() {
        return get(VAR, String.class);
    }
    public String getValue() {
        return get(VALUE, String.class);
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
