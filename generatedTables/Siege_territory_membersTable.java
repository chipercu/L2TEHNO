package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Siege_territory_membersTable.*;

@Table(
        name = "siege_territory_members",
        fields = {
                @Field(name = OBJ_ID,
                @Field(name = SIDE,
                @Field(name = TYPE,
        }
)
public class Siege_territory_membersTable extends DataBaseTable<Siege_territory_membersTable> {

    public static final String OBJ_ID = "obj_Id";
    public static final String SIDE = "side";
    public static final String TYPE = "type";
    public Siege_territory_membersTable() {
super(Siege_territory_membersTable.class);
}
    public IntegergetObj_Id() {
        return get(OBJ_ID, Integer.class);
    }
    public IntegergetSide() {
        return get(SIDE, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }

    public void setObj_Id() {
        set(OBJ_ID, value);
    }
    public void setSide() {
        set(SIDE, value);
    }
    public void setType() {
        set(TYPE, value);
    }

}
