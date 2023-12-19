package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SiegeDoorResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_door",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = UNIT_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
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
