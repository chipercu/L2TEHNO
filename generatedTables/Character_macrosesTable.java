package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_macrosesTable.*;

@Table(
        name = "character_macroses",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = ID,
                @Field(name = ICON,
                @Field(name = NAME,
                @Field(name = DESCR,
                @Field(name = ACRONYM,
                @Field(name = COMMANDS,
        }
)
public class Character_macrosesTable extends DataBaseTable<Character_macrosesTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String ID = "id";
    public static final String ICON = "icon";
    public static final String NAME = "name";
    public static final String DESCR = "descr";
    public static final String ACRONYM = "acronym";
    public static final String COMMANDS = "commands";
    public Character_macrosesTable() {
super(Character_macrosesTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetIcon() {
        return get(ICON, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetDescr() {
        return get(DESCR, String.class);
    }
    public StringgetAcronym() {
        return get(ACRONYM, String.class);
    }
    public StringgetCommands() {
        return get(COMMANDS, String.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setId() {
        set(ID, value);
    }
    public void setIcon() {
        set(ICON, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setDescr() {
        set(DESCR, value);
    }
    public void setAcronym() {
        set(ACRONYM, value);
    }
    public void setCommands() {
        set(COMMANDS, value);
    }

}
