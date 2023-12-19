package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ClanPrivsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_privs",
        primary_key = {CLAN_ID,RANK},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLAN_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RANK , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PRIVILLEGES , defValue = "0"),
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
        return get(CLAN_ID);
    }
    public Integer getRank() {
        return get(RANK);
    }
    public Integer getPrivilleges() {
        return get(PRIVILLEGES);
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
