package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeDoorupgradeResource.*;

@Table(
        name = "siege_doorupgrade",
        primary_key = {DOOR_ID},
        fields = {
                @Field(name = DOOR_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = HP , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class SiegeDoorupgradeResource extends DataBaseTable<SiegeDoorupgradeResource> {

    public static final String DOOR_ID = "doorId";
    public static final String HP = "hp";

    public SiegeDoorupgradeResource() {
super(SiegeDoorupgradeResource.class);
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
