package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterMacrosesTable.*;

@Table(
        name = "character_macroses",
        fields = {
                @Field(name = CHAR_OBJ_ID),
                @Field(name = ID),
                @Field(name = ICON),
                @Field(name = NAME),
                @Field(name = DESCR),
                @Field(name = ACRONYM),
                @Field(name = COMMANDS),
        }
)
public class CharacterMacrosesTable extends DataBaseTable<CharacterMacrosesTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String ID = "id";
    public static final String ICON = "icon";
    public static final String NAME = "name";
    public static final String DESCR = "descr";
    public static final String ACRONYM = "acronym";
    public static final String COMMANDS = "commands";

    public CharacterMacrosesTable() {
super(CharacterMacrosesTable.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getIcon() {
        return get(ICON, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getDescr() {
        return get(DESCR, String.class);
    }
    public String getAcronym() {
        return get(ACRONYM, String.class);
    }
    public String getCommands() {
        return get(COMMANDS, String.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }
    public void setIcon(Integer value) {
        set(ICON, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setDescr(String value) {
        set(DESCR, value);
    }
    public void setAcronym(String value) {
        set(ACRONYM, value);
    }
    public void setCommands(String value) {
        set(COMMANDS, value);
    }

}
