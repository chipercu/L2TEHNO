package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SiegeDoorupgradeResource.*;

@Table(
        name = "siege_doorupgrade",
        primary_key = {DOOR_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = DOOR_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HP , defValue = "0"),
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
