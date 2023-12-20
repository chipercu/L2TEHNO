package l2open.database.schemes.resources;

import l2open.database.anotations.Column;
import l2open.database.anotations.Table;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.ClanPrivsResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

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
