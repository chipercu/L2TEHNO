package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanWarsTable.*;

@Table(
        name = "clan_wars",
        fields = {
                @Field(name = CLAN1),
                @Field(name = CLAN2),
        }
)
public class ClanWarsTable extends DataBaseTable<ClanWarsTable> {

    public static final String CLAN1 = "clan1";
    public static final String CLAN2 = "clan2";

    public ClanWarsTable() {
super(ClanWarsTable.class);
}

    public Integer getClan1() {
        return get(CLAN1, Integer.class);
    }
    public Integer getClan2() {
        return get(CLAN2, Integer.class);
    }

    public void setClan1(Integer value) {
        set(CLAN1, value);
    }
    public void setClan2(Integer value) {
        set(CLAN2, value);
    }

}
