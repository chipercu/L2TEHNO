package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterMacrosesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_macroses",
        primary_key = {CHAR_OBJ_ID,ID},
        fields = {
                @Field(name = CHAR_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ICON , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = NAME , type = VARCHAR , type_size = 40 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = DESCR , type = VARCHAR , type_size = 80 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = ACRONYM , type = VARCHAR , type_size = 4 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = COMMANDS , type = VARCHAR , type_size = 1024 , nullable = true , defValue = @DefValue(String = "")),
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


    public CharacterMacrosesResource(String char_obj_id,String id){
        super(CharacterMacrosesResource.class);
        getSTAT_SET().set(CHAR_OBJ_ID, char_obj_id);
        getSTAT_SET().set(ID, id);
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
