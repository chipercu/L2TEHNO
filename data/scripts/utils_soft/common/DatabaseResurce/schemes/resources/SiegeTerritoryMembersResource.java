package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SiegeTerritoryMembersResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_territory_members",
        primary_key = {OBJ__ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJ__ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SIDE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = TYPE , defValue = "0"),
        }
)
public class SiegeTerritoryMembersResource extends DataBaseTable<SiegeTerritoryMembersResource> {

    public static final String OBJ__ID = "obj_Id";
    public static final String SIDE = "side";
    public static final String TYPE = "type";

    public SiegeTerritoryMembersResource() {
        super(SiegeTerritoryMembersResource.class);
}

    public Integer getObjId() {
        return get(OBJ__ID);
    }
    public Integer getSide() {
        return get(SIDE);
    }
    public Integer getType() {
        return get(TYPE);
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
