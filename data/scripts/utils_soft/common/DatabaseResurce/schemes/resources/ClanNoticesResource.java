package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ClanNoticesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_notices",
        primary_key = {CLAN_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLAN_ID , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 512 , name = NOTICE , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 5 , name = ENABLED , defValue = "null"),
        }
)
public class ClanNoticesResource extends DataBaseTable<ClanNoticesResource> {

    public static final String CLAN_ID = "clanID";
    public static final String NOTICE = "notice";
    public static final String ENABLED = "enabled";

    public ClanNoticesResource() {
        super(ClanNoticesResource.class);
}

    public Integer getClanID() {
        return get(CLAN_ID);
    }
    public String getNotice() {
        return get(NOTICE);
    }
    public String getEnabled() {
        return get(ENABLED);
    }

    public void setClanID(Integer value) {
        set(CLAN_ID, value);
    }
    public void setNotice(String value) {
        set(NOTICE, value);
    }
    public void setEnabled(String value) {
        set(ENABLED, value);
    }

}
