package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterHennasResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_hennas",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SYMBOL_ID , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SLOT , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CLASS_INDEX , defValue = "0"),
        }
)
public class CharacterHennasResource extends DataBaseTable<CharacterHennasResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SYMBOL_ID = "symbol_id";
    public static final String SLOT = "slot";
    public static final String CLASS_INDEX = "class_index";

    public CharacterHennasResource() {
        super(CharacterHennasResource.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Integer getSymbolId() {
        return get(SYMBOL_ID);
    }
    public Integer getSlot() {
        return get(SLOT);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX);
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
