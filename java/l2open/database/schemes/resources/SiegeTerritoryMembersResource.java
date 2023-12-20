package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SiegeTerritoryMembersResource.*;

@Table(
        name = "siege_territory_members",
        primary_key = {OBJ__ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJ__ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SIDE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TYPE , defValue = "0"),
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
