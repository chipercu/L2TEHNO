package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterShortcutsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_shortcuts",
        primary_key = {CHAR_OBJ_ID,SLOT,PAGE,CLASS_INDEX},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SLOT , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PAGE , defValue = "0"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = TYPE , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = SHORTCUT_ID , defValue = "NULL"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = LEVEL , defValue = "NULL"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CLASS_INDEX , defValue = "0"),
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

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Integer getSlot() {
        return get(SLOT);
    }
    public Integer getPage() {
        return get(PAGE);
    }
    public Integer getType() {
        return get(TYPE);
    }
    public Integer getShortcutId() {
        return get(SHORTCUT_ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX);
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
