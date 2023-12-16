package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Clan_subpledgesTable.*;

@Table(
        name = "clan_subpledges",
        fields = {
                @Field(name = CLAN_ID,
                @Field(name = TYPE,
                @Field(name = NAME,
                @Field(name = LEADER_ID,
        }
)
public class Clan_subpledgesTable extends DataBaseTable<Clan_subpledgesTable> {

    public static final String CLAN_ID = "clan_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LEADER_ID = "leader_id";
    public Clan_subpledgesTable() {
super(Clan_subpledgesTable.class);
}
    public IntegergetClan_id() {
        return get(CLAN_ID, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetLeader_id() {
        return get(LEADER_ID, Integer.class);
    }

    public void setClan_id() {
        set(CLAN_ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setLeader_id() {
        set(LEADER_ID, value);
    }

}
