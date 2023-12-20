package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ClanNoticesResource.*;

@Table(
        name = "clan_notices",
        primary_key = {CLAN_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLAN_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 512 , name = NOTICE , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 5 , name = ENABLED , defValue = "null"),
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
