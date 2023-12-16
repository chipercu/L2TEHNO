package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Siege_clansTable.*;

@Table(
        name = "siege_clans",
        fields = {
                @Field(name = UNIT_ID,
                @Field(name = CLAN_ID,
                @Field(name = TYPE,
        }
)
public class Siege_clansTable extends DataBaseTable<Siege_clansTable> {

    public static final String UNIT_ID = "unit_id";
    public static final String CLAN_ID = "clan_id";
    public static final String TYPE = "type";
    public Siege_clansTable() {
super(Siege_clansTable.class);
}
    public IntegergetUnit_id() {
        return get(UNIT_ID, Integer.class);
    }
    public IntegergetClan_id() {
        return get(CLAN_ID, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }

    public void setUnit_id() {
        set(UNIT_ID, value);
    }
    public void setClan_id() {
        set(CLAN_ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }

}
