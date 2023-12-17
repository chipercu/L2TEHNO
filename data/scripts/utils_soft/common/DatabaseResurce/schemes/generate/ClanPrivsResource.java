package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanPrivsResource.*;

@Table(
        name = "clan_privs",
        primary_key = {CLAN_ID,RANK},
        fields = {
                @Field(name = CLAN_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = RANK , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = PRIVILLEGES , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class ClanPrivsResource extends DataBaseTable<ClanPrivsResource> {

    public static final String CLAN_ID = "clan_id";
    public static final String RANK = "rank";
    public static final String PRIVILLEGES = "privilleges";

    public ClanPrivsResource() {
super(ClanPrivsResource.class);
}

    public Integer getClanId() {
        return get(CLAN_ID, Integer.class);
    }
    public Integer getRank() {
        return get(RANK, Integer.class);
    }
    public Integer getPrivilleges() {
        return get(PRIVILLEGES, Integer.class);
    }

    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setRank(Integer value) {
        set(RANK, value);
    }
    public void setPrivilleges(Integer value) {
        set(PRIVILLEGES, value);
    }

}
