package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Siege_doorTable.*;

@Table(
        name = "siege_door",
        fields = {
                @Field(name = UNITID,
                @Field(name = ID,
        }
)
public class Siege_doorTable extends DataBaseTable<Siege_doorTable> {

    public static final String UNITID = "unitId";
    public static final String ID = "id";
    public Siege_doorTable() {
super(Siege_doorTable.class);
}
    public IntegergetUnitId() {
        return get(UNITID, Integer.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }

    public void setUnitId() {
        set(UNITID, value);
    }
    public void setId() {
        set(ID, value);
    }

}
