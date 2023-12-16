package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeDoorTable.*;

@Table(
        name = "siege_door",
        fields = {
                @Field(name = UNIT_ID),
                @Field(name = ID),
        }
)
public class SiegeDoorTable extends DataBaseTable<SiegeDoorTable> {

    public static final String UNIT_ID = "unitId";
    public static final String ID = "id";

    public SiegeDoorTable() {
super(SiegeDoorTable.class);
}

    public Integer getUnitId() {
        return get(UNIT_ID, Integer.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }

    public void setUnitId(Integer value) {
        set(UNIT_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }

}
