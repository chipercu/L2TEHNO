package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SiegeDoorResource.*;

@Table(
        name = "siege_door",
        primary_key = {ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = UNIT_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "0"),
        }
)
public class SiegeDoorResource extends DataBaseTable<SiegeDoorResource> {

    public static final String UNIT_ID = "unitId";
    public static final String ID = "id";

    public SiegeDoorResource() {
        super(SiegeDoorResource.class);
}

    public Integer getUnitId() {
        return get(UNIT_ID);
    }
    public Integer getId() {
        return get(ID);
    }

    public void setUnitId(Integer value) {
        set(UNIT_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }

}
