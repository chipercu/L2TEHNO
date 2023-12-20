package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.AllyDataResource.*;

@Table(
        name = "ally_data",
        primary_key = {ALLY_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ALLY_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 45 , name = ALLY_NAME , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEADER_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = EXPELLED_MEMBER , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARBINARY , type_size = 192 , name = CREST , defValue = "NULL"),
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
