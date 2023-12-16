package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_blocklistTable.*;

@Table(
        name = "character_blocklist",
        fields = {
                @Field(name = OBJ_ID,
                @Field(name = TARGET_ID,
        }
)
public class Character_blocklistTable extends DataBaseTable<Character_blocklistTable> {

    public static final String OBJ_ID = "obj_Id";
    public static final String TARGET_ID = "target_Id";
    public Character_blocklistTable() {
super(Character_blocklistTable.class);
}
    public IntegergetObj_Id() {
        return get(OBJ_ID, Integer.class);
    }
    public IntegergetTarget_Id() {
        return get(TARGET_ID, Integer.class);
    }

    public void setObj_Id() {
        set(OBJ_ID, value);
    }
    public void setTarget_Id() {
        set(TARGET_ID, value);
    }

}
