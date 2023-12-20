package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ClanWarsResource.*;

@Table(
        name = "clan_wars",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLAN1 , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLAN2 , defValue = "0"),
        }
)
public class ClanWarsResource extends DataBaseTable<ClanWarsResource> {

    public static final String CLAN1 = "clan1";
    public static final String CLAN2 = "clan2";

    public ClanWarsResource() {
        super(ClanWarsResource.class);
}

    public Integer getClan1() {
        return get(CLAN1);
    }
    public Integer getClan2() {
        return get(CLAN2);
    }

    public void setClan1(Integer value) {
        set(CLAN1, value);
    }
    public void setClan2(Integer value) {
        set(CLAN2, value);
    }

}
