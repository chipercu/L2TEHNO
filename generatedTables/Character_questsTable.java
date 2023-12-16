package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_questsTable.*;

@Table(
        name = "character_quests",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = NAME,
                @Field(name = VAR,
                @Field(name = VALUE,
        }
)
public class Character_questsTable extends DataBaseTable<Character_questsTable> {

    public static final String CHAR_ID = "char_id";
    public static final String NAME = "name";
    public static final String VAR = "var";
    public static final String VALUE = "value";
    public Character_questsTable() {
super(Character_questsTable.class);
}
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetVar() {
        return get(VAR, String.class);
    }
    public StringgetValue() {
        return get(VALUE, String.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setVar() {
        set(VAR, value);
    }
    public void setValue() {
        set(VALUE, value);
    }

}
