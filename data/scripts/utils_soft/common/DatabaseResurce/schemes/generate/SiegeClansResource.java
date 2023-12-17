package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeClansResource.*;

@Table(
        name = "siege_clans",
        primary_key = {UNIT_ID,CLAN_ID},
        fields = {
                @Field(name = UNIT_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CLAN_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , data_type = "int"),
        }
)
public class SiegeClansResource extends DataBaseTable<SiegeClansResource> {

    public static final String UNIT_ID = "unit_id";
    public static final String CLAN_ID = "clan_id";
    public static final String TYPE = "type";

    public SiegeClansResource() {
super(SiegeClansResource.class);
}

    public Integer getUnitId() {
        return get(UNIT_ID, Integer.class);
    }
    public Integer getClanId() {
        return get(CLAN_ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }

    public void setUnitId(Integer value) {
        set(UNIT_ID, value);
    }
    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }

}
