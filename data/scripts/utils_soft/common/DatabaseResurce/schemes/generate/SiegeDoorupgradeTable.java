package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeDoorupgradeTable.*;

@Table(
        name = "siege_doorupgrade",
        fields = {
                @Field(name = DOOR_ID),
                @Field(name = HP),
        }
)
public class SiegeDoorupgradeTable extends DataBaseTable<SiegeDoorupgradeTable> {

    public static final String DOOR_ID = "doorId";
    public static final String HP = "hp";

    public SiegeDoorupgradeTable() {
super(SiegeDoorupgradeTable.class);
}

    public Integer getDoorId() {
        return get(DOOR_ID, Integer.class);
    }
    public Integer getHp() {
        return get(HP, Integer.class);
    }

    public void setDoorId(Integer value) {
        set(DOOR_ID, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }

}
