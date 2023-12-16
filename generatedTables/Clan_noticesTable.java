package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Clan_noticesTable.*;

@Table(
        name = "clan_notices",
        fields = {
                @Field(name = CLANID,
                @Field(name = NOTICE,
                @Field(name = ENABLED,
        }
)
public class Clan_noticesTable extends DataBaseTable<Clan_noticesTable> {

    public static final String CLANID = "clanID";
    public static final String NOTICE = "notice";
    public static final String ENABLED = "enabled";
    public Clan_noticesTable() {
super(Clan_noticesTable.class);
}
    public IntegergetClanID() {
        return get(CLANID, Integer.class);
    }
    public StringgetNotice() {
        return get(NOTICE, String.class);
    }
    public StringgetEnabled() {
        return get(ENABLED, String.class);
    }

    public void setClanID() {
        set(CLANID, value);
    }
    public void setNotice() {
        set(NOTICE, value);
    }
    public void setEnabled() {
        set(ENABLED, value);
    }

}
