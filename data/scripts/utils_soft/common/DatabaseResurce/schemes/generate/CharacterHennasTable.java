package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterHennasTable.*;

@Table(
        name = "character_hennas",
        fields = {
                @Field(name = CHAR_OBJ_ID),
                @Field(name = SYMBOL_ID),
                @Field(name = SLOT),
                @Field(name = CLASS_INDEX),
        }
)
public class CharacterHennasTable extends DataBaseTable<CharacterHennasTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SYMBOL_ID = "symbol_id";
    public static final String SLOT = "slot";
    public static final String CLASS_INDEX = "class_index";

    public CharacterHennasTable() {
super(CharacterHennasTable.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getSymbolId() {
        return get(SYMBOL_ID, Integer.class);
    }
    public Integer getSlot() {
        return get(SLOT, Integer.class);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setSymbolId(Integer value) {
        set(SYMBOL_ID, value);
    }
    public void setSlot(Integer value) {
        set(SLOT, value);
    }
    public void setClassIndex(Integer value) {
        set(CLASS_INDEX, value);
    }

}
