package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AllyDataResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "ally_data",
        primary_key = {ALLY_ID},
        fields = {
                @Field(name = ALLY_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ALLY_NAME , type = VARCHAR , type_size = 45 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = LEADER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = EXPELLED_MEMBER , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CREST , type = VARBINARY , type_size = 192 , nullable = true , defValue = @DefValue(String = "")),
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


    public AllyDataResource(String ally_id){
        super(AllyDataResource.class);
        getSTAT_SET().set(ALLY_ID, ally_id);
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
