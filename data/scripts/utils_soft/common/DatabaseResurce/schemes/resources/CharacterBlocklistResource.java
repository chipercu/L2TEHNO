package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterBlocklistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_blocklist",
        primary_key = {OBJ__ID,TARGET__ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJ__ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = TARGET__ID , defValue = "null"),
        }
)
public class CharacterBlocklistResource extends DataBaseTable<CharacterBlocklistResource> {

    public static final String OBJ__ID = "obj_Id";
    public static final String TARGET__ID = "target_Id";

    public CharacterBlocklistResource() {
        super(CharacterBlocklistResource.class);
}

    public Integer getObjId() {
        return get(OBJ__ID);
    }
    public Integer getTargetId() {
        return get(TARGET__ID);
    }

    public void setObjId(Integer value) {
        set(OBJ__ID, value);
    }
    public void setTargetId(Integer value) {
        set(TARGET__ID, value);
    }

}
