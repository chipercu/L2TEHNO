package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterHennasResource.*;

@Table(
        name = "character_hennas",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SYMBOL_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SLOT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = CLASS_INDEX , defValue = "0"),
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
