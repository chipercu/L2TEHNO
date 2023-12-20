package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterBlocklistResource.*;

@Table(
        name = "character_blocklist",
        primary_key = {OBJ__ID,TARGET__ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJ__ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TARGET__ID , defValue = "null"),
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
