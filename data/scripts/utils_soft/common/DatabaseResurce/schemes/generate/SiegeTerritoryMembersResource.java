package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeTerritoryMembersResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_territory_members",
        primary_key = {OBJ__ID},
        fields = {
                @Field(name = OBJ__ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SIDE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class SiegeTerritoryMembersResource extends DataBaseTable<SiegeTerritoryMembersResource> {

    public static final String OBJ__ID = "obj_Id";
    public static final String SIDE = "side";
    public static final String TYPE = "type";

    public SiegeTerritoryMembersResource() {
super(SiegeTerritoryMembersResource.class);
}


    public SiegeTerritoryMembersResource(String obj_Id){
        super(SiegeTerritoryMembersResource.class);
        getSTAT_SET().set(OBJ__ID, obj_Id);
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
