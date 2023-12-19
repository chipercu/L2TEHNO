package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ClanWarsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_wars",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLAN1 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLAN2 , defValue = "0"),
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
