package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanSubpledgesTable.*;

@Table(
        name = "clan_subpledges",
        fields = {
                @Field(name = CLAN_ID),
                @Field(name = TYPE),
                @Field(name = NAME),
                @Field(name = LEADER_ID),
        }
)
public class ClanSubpledgesTable extends DataBaseTable<ClanSubpledgesTable> {

    public static final String CLAN_ID = "clan_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LEADER_ID = "leader_id";

    public ClanSubpledgesTable() {
super(ClanSubpledgesTable.class);
}

    public Integer getClanId() {
        return get(CLAN_ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID, Integer.class);
    }

    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setLeaderId(Integer value) {
        set(LEADER_ID, value);
    }

}
