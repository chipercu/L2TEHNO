package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.AllyDataResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "ally_data",
        primary_key = {ALLY_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ALLY_ID , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 45 , name = ALLY_NAME , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEADER_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EXPELLED_MEMBER , defValue = "0"),
                @Column(is_null = YES , type = VARBINARY , type_size = 192 , name = CREST , defValue = "NULL"),
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
        return get(ALLY_ID);
    }
    public String getAllyName() {
        return get(ALLY_NAME);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID);
    }
    public Integer getExpelledMember() {
        return get(EXPELLED_MEMBER);
    }
    public String getCrest() {
        return get(CREST);
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
