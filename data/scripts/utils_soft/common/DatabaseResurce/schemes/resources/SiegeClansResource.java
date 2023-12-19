package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SiegeClansResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_clans",
        primary_key = {UNIT_ID,CLAN_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = UNIT_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLAN_ID , defValue = "0"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = TYPE , defValue = "NULL"),
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
