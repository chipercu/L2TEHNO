package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanNoticesResource.*;

@Table(
        name = "clan_notices",
        primary_key = {CLAN_ID},
        fields = {
                @Field(name = CLAN_ID , data_type = "int"),
                @Field(name = NOTICE , data_type = "varchar"),
                @Field(name = ENABLED , data_type = "varchar"),
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
        return get(CLAN_ID, Integer.class);
    }
    public String getNotice() {
        return get(NOTICE, String.class);
    }
    public String getEnabled() {
        return get(ENABLED, String.class);
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
