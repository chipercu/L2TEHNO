package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AllyDataTable.*;

@Table(
        name = "ally_data",
        fields = {
                @Field(name = ALLY_ID),
                @Field(name = ALLY_NAME),
                @Field(name = LEADER_ID),
                @Field(name = EXPELLED_MEMBER),
                @Field(name = CREST),
        }
)
public class AllyDataTable extends DataBaseTable<AllyDataTable> {

    public static final String ALLY_ID = "ally_id";
    public static final String ALLY_NAME = "ally_name";
    public static final String LEADER_ID = "leader_id";
    public static final String EXPELLED_MEMBER = "expelled_member";
    public static final String CREST = "crest";

    public AllyDataTable() {
super(AllyDataTable.class);
}

    public Integer getAllyId() {
        return get(ALLY_ID, Integer.class);
    }
    public String getAllyName() {
        return get(ALLY_NAME, String.class);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID, Integer.class);
    }
    public Integer getExpelledMember() {
        return get(EXPELLED_MEMBER, Integer.class);
    }
    public Object getCrest() {
        return get(CREST, Object.class);
    }

    public void setAllyId(Integer value) {
        set(ALLY_ID, value);
    }
    public void setAllyName(String value) {
        set(ALLY_NAME, value);
    }
    public void setLeaderId(Integer value) {
        set(LEADER_ID, value);
    }
    public void setExpelledMember(Integer value) {
        set(EXPELLED_MEMBER, value);
    }
    public void setCrest(Object value) {
        set(CREST, value);
    }

}
