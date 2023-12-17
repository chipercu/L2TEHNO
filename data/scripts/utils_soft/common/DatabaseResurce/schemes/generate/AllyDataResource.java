package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AllyDataResource.*;

@Table(
        name = "ally_data",
        primary_key = {ALLY_ID},
        fields = {
                @Field(name = ALLY_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ALLY_NAME , data_type = "varchar"),
                @Field(name = LEADER_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = EXPELLED_MEMBER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CREST , data_type = "varbinary"),
        }
)
public class AllyDataResource extends DataBaseTable<AllyDataResource> {

    public static final String ALLY_ID = "ally_id";
    public static final String ALLY_NAME = "ally_name";
    public static final String LEADER_ID = "leader_id";
    public static final String EXPELLED_MEMBER = "expelled_member";
    public static final String CREST = "crest";

    public AllyDataResource() {
super(AllyDataResource.class);
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
    public String getCrest() {
        return get(CREST, String.class);
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
    public void setCrest(String value) {
        set(CREST, value);
    }

}
