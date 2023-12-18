package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterShortcutsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_shortcuts",
        primary_key = {CHAR_OBJ_ID,SLOT,PAGE,CLASS_INDEX},
        fields = {
                @Field(name = CHAR_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SLOT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PAGE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = SHORTCUT_ID , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = LEVEL , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CLASS_INDEX , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class CharacterShortcutsResource extends DataBaseTable<CharacterShortcutsResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SLOT = "slot";
    public static final String PAGE = "page";
    public static final String TYPE = "type";
    public static final String SHORTCUT_ID = "shortcut_id";
    public static final String LEVEL = "level";
    public static final String CLASS_INDEX = "class_index";

    public CharacterShortcutsResource() {
super(CharacterShortcutsResource.class);
}


    public CharacterShortcutsResource(String char_obj_id,String slot,String page,String class_index){
        super(CharacterShortcutsResource.class);
        getSTAT_SET().set(CHAR_OBJ_ID, char_obj_id);
        getSTAT_SET().set(SLOT, slot);
        getSTAT_SET().set(PAGE, page);
        getSTAT_SET().set(CLASS_INDEX, class_index);
    }

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getSlot() {
        return get(SLOT, Integer.class);
    }
    public Integer getPage() {
        return get(PAGE, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public Integer getShortcutId() {
        return get(SHORTCUT_ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setSlot(Integer value) {
        set(SLOT, value);
    }
    public void setPage(Integer value) {
        set(PAGE, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setShortcutId(Integer value) {
        set(SHORTCUT_ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setClassIndex(Integer value) {
        set(CLASS_INDEX, value);
    }

}
