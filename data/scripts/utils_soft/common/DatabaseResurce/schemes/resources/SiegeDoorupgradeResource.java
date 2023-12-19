package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SiegeDoorupgradeResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_doorupgrade",
        primary_key = {DOOR_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = DOOR_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HP , defValue = "0"),
        }
)
public class SiegeDoorupgradeResource extends DataBaseTable<SiegeDoorupgradeResource> {

    public static final String DOOR_ID = "doorId";
    public static final String HP = "hp";

    public SiegeDoorupgradeResource() {
        super(SiegeDoorupgradeResource.class);
}

    public Integer getDoorId() {
        return get(DOOR_ID);
    }
    public Integer getHp() {
        return get(HP);
    }

    public void setDoorId(Integer value) {
        set(DOOR_ID, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }

}
