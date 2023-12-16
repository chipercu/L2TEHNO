package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Clan_privsTable.*;

@Table(
        name = "clan_privs",
        fields = {
                @Field(name = CLAN_ID,
                @Field(name = RANK,
                @Field(name = PRIVILLEGES,
        }
)
public class Clan_privsTable extends DataBaseTable<Clan_privsTable> {

    public static final String CLAN_ID = "clan_id";
    public static final String RANK = "rank";
    public static final String PRIVILLEGES = "privilleges";
    public Clan_privsTable() {
super(Clan_privsTable.class);
}
    public IntegergetClan_id() {
        return get(CLAN_ID, Integer.class);
    }
    public IntegergetRank() {
        return get(RANK, Integer.class);
    }
    public IntegergetPrivilleges() {
        return get(PRIVILLEGES, Integer.class);
    }

    public void setClan_id() {
        set(CLAN_ID, value);
    }
    public void setRank() {
        set(RANK, value);
    }
    public void setPrivilleges() {
        set(PRIVILLEGES, value);
    }

}
