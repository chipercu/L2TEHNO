package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeTerritoryMembersTable.*;

@Table(
        name = "siege_territory_members",
        fields = {
                @Field(name = OBJ__ID),
                @Field(name = SIDE),
                @Field(name = TYPE),
        }
)
public class SiegeTerritoryMembersTable extends DataBaseTable<SiegeTerritoryMembersTable> {

    public static final String OBJ__ID = "obj_Id";
    public static final String SIDE = "side";
    public static final String TYPE = "type";

    public SiegeTerritoryMembersTable() {
super(SiegeTerritoryMembersTable.class);
}

    public Integer getObjId() {
        return get(OBJ__ID, Integer.class);
    }
    public Integer getSide() {
        return get(SIDE, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }

    public void setObjId(Integer value) {
        set(OBJ__ID, value);
    }
    public void setSide(Integer value) {
        set(SIDE, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }

}
