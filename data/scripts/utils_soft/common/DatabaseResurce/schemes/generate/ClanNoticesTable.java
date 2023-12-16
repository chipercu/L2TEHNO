package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanNoticesTable.*;

@Table(
        name = "clan_notices",
        fields = {
                @Field(name = CLAN_ID),
                @Field(name = NOTICE),
                @Field(name = ENABLED),
        }
)
public class ClanNoticesTable extends DataBaseTable<ClanNoticesTable> {

    public static final String CLAN_ID = "clanID";
    public static final String NOTICE = "notice";
    public static final String ENABLED = "enabled";

    public ClanNoticesTable() {
super(ClanNoticesTable.class);
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
