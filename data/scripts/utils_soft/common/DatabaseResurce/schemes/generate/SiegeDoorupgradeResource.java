package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeDoorupgradeResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_doorupgrade",
        primary_key = {DOOR_ID},
        fields = {
                @Field(name = DOOR_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HP , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class SiegeDoorupgradeResource extends DataBaseTable<SiegeDoorupgradeResource> {

    public static final String DOOR_ID = "doorId";
    public static final String HP = "hp";

    public SiegeDoorupgradeResource() {
super(SiegeDoorupgradeResource.class);
}


    public SiegeDoorupgradeResource(String doorId){
        super(SiegeDoorupgradeResource.class);
        getSTAT_SET().set(DOOR_ID, doorId);
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
