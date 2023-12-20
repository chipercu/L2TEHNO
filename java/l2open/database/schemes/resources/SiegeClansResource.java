package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SiegeClansResource.*;

@Table(
        name = "siege_clans",
        primary_key = {UNIT_ID,CLAN_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = UNIT_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLAN_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = TYPE , defValue = "NULL"),
        }
)
public class SiegeClansResource extends DataBaseTable<SiegeClansResource> {

    public static final String UNIT_ID = "unit_id";
    public static final String CLAN_ID = "clan_id";
    public static final String TYPE = "type";

    public SiegeClansResource() {
        super(SiegeClansResource.class);
}

    public Integer getUnitId() {
        return get(UNIT_ID);
    }
    public Integer getClanId() {
        return get(CLAN_ID);
    }
    public Integer getType() {
        return get(TYPE);
    }

    public void setUnitId(Integer value) {
        set(UNIT_ID, value);
    }
    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }

}
