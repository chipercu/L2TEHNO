package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_recipebookTable.*;

@Table(
        name = "character_recipebook",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = ID,
        }
)
public class Character_recipebookTable extends DataBaseTable<Character_recipebookTable> {

    public static final String CHAR_ID = "char_id";
    public static final String ID = "id";
    public Character_recipebookTable() {
super(Character_recipebookTable.class);
}
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setId() {
        set(ID, value);
    }

}
