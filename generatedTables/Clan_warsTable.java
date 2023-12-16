package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Clan_warsTable.*;

@Table(
        name = "clan_wars",
        fields = {
                @Field(name = CLAN1,
                @Field(name = CLAN2,
        }
)
public class Clan_warsTable extends DataBaseTable<Clan_warsTable> {

    public static final String CLAN1 = "clan1";
    public static final String CLAN2 = "clan2";
    public Clan_warsTable() {
super(Clan_warsTable.class);
}
    public IntegergetClan1() {
        return get(CLAN1, Integer.class);
    }
    public IntegergetClan2() {
        return get(CLAN2, Integer.class);
    }

    public void setClan1() {
        set(CLAN1, value);
    }
    public void setClan2() {
        set(CLAN2, value);
    }

}
