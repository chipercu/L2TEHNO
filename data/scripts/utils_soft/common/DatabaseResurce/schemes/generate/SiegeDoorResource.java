package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeDoorResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_door",
        primary_key = {ID},
        fields = {
                @Field(name = UNIT_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class SiegeDoorResource extends DataBaseTable<SiegeDoorResource> {

    public static final String UNIT_ID = "unitId";
    public static final String ID = "id";

    public SiegeDoorResource() {
super(SiegeDoorResource.class);
}


    public SiegeDoorResource(String id){
        super(SiegeDoorResource.class);
        getSTAT_SET().set(ID, id);
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
