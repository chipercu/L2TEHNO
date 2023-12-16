package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_shortcutsTable.*;

@Table(
        name = "character_shortcuts",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = SLOT,
                @Field(name = PAGE,
                @Field(name = TYPE,
                @Field(name = SHORTCUT_ID,
                @Field(name = LEVEL,
                @Field(name = CLASS_INDEX,
        }
)
public class Character_shortcutsTable extends DataBaseTable<Character_shortcutsTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SLOT = "slot";
    public static final String PAGE = "page";
    public static final String TYPE = "type";
    public static final String SHORTCUT_ID = "shortcut_id";
    public static final String LEVEL = "level";
    public static final String CLASS_INDEX = "class_index";
    public Character_shortcutsTable() {
super(Character_shortcutsTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetSlot() {
        return get(SLOT, Integer.class);
    }
    public IntegergetPage() {
        return get(PAGE, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }
    public IntegergetShortcut_id() {
        return get(SHORTCUT_ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public IntegergetClass_index() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setSlot() {
        set(SLOT, value);
    }
    public void setPage() {
        set(PAGE, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setShortcut_id() {
        set(SHORTCUT_ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setClass_index() {
        set(CLASS_INDEX, value);
    }

}
