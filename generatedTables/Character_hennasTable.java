package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_hennasTable.*;

@Table(
        name = "character_hennas",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = SYMBOL_ID,
                @Field(name = SLOT,
                @Field(name = CLASS_INDEX,
        }
)
public class Character_hennasTable extends DataBaseTable<Character_hennasTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SYMBOL_ID = "symbol_id";
    public static final String SLOT = "slot";
    public static final String CLASS_INDEX = "class_index";
    public Character_hennasTable() {
super(Character_hennasTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetSymbol_id() {
        return get(SYMBOL_ID, Integer.class);
    }
    public IntegergetSlot() {
        return get(SLOT, Integer.class);
    }
    public IntegergetClass_index() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setSymbol_id() {
        set(SYMBOL_ID, value);
    }
    public void setSlot() {
        set(SLOT, value);
    }
    public void setClass_index() {
        set(CLASS_INDEX, value);
    }

}
