package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.CharacterMacrosesResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "character_macroses",
        primary_key = {CHAR_OBJ_ID,ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = ICON , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 40 , name = NAME , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 80 , name = DESCR , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 4 , name = ACRONYM , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 1024 , name = COMMANDS , defValue = "NULL"),
        }
)
public class CharacterMacrosesResource extends DataBaseTable<CharacterMacrosesResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String ID = "id";
    public static final String ICON = "icon";
    public static final String NAME = "name";
    public static final String DESCR = "descr";
    public static final String ACRONYM = "acronym";
    public static final String COMMANDS = "commands";

    public CharacterMacrosesResource() {
        super(CharacterMacrosesResource.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Integer getId() {
        return get(ID);
    }
    public Integer getIcon() {
        return get(ICON);
    }
    public String getName() {
        return get(NAME);
    }
    public String getDescr() {
        return get(DESCR);
    }
    public String getAcronym() {
        return get(ACRONYM);
    }
    public String getCommands() {
        return get(COMMANDS);
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
