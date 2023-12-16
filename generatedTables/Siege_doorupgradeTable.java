package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Siege_doorupgradeTable.*;

@Table(
        name = "siege_doorupgrade",
        fields = {
                @Field(name = DOORID,
                @Field(name = HP,
        }
)
public class Siege_doorupgradeTable extends DataBaseTable<Siege_doorupgradeTable> {

    public static final String DOORID = "doorId";
    public static final String HP = "hp";
    public Siege_doorupgradeTable() {
super(Siege_doorupgradeTable.class);
}
    public IntegergetDoorId() {
        return get(DOORID, Integer.class);
    }
    public IntegergetHp() {
        return get(HP, Integer.class);
    }

    public void setDoorId() {
        set(DOORID, value);
    }
    public void setHp() {
        set(HP, value);
    }

}
