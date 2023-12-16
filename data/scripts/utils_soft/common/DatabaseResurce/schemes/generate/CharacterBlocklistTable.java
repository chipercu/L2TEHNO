package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterBlocklistTable.*;

@Table(
        name = "character_blocklist",
        fields = {
                @Field(name = OBJ__ID),
                @Field(name = TARGET__ID),
        }
)
public class CharacterBlocklistTable extends DataBaseTable<CharacterBlocklistTable> {

    public static final String OBJ__ID = "obj_Id";
    public static final String TARGET__ID = "target_Id";

    public CharacterBlocklistTable() {
super(CharacterBlocklistTable.class);
}

    public Integer getObjId() {
        return get(OBJ__ID, Integer.class);
    }
    public Integer getTargetId() {
        return get(TARGET__ID, Integer.class);
    }

    public void setObjId(Integer value) {
        set(OBJ__ID, value);
    }
    public void setTargetId(Integer value) {
        set(TARGET__ID, value);
    }

}
