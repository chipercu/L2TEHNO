package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Ally_dataTable.*;

@Table(
        name = "ally_data",
        fields = {
                @Field(name = ALLY_ID,
                @Field(name = ALLY_NAME,
                @Field(name = LEADER_ID,
                @Field(name = EXPELLED_MEMBER,
                @Field(name = CREST,
        }
)
public class Ally_dataTable extends DataBaseTable<Ally_dataTable> {

    public static final String ALLY_ID = "ally_id";
    public static final String ALLY_NAME = "ally_name";
    public static final String LEADER_ID = "leader_id";
    public static final String EXPELLED_MEMBER = "expelled_member";
    public static final String CREST = "crest";
    public Ally_dataTable() {
super(Ally_dataTable.class);
}
    public IntegergetAlly_id() {
        return get(ALLY_ID, Integer.class);
    }
    public StringgetAlly_name() {
        return get(ALLY_NAME, String.class);
    }
    public IntegergetLeader_id() {
        return get(LEADER_ID, Integer.class);
    }
    public IntegergetExpelled_member() {
        return get(EXPELLED_MEMBER, Integer.class);
    }
    public ObjectgetCrest() {
        return get(CREST, Object.class);
    }

    public void setAlly_id() {
        set(ALLY_ID, value);
    }
    public void setAlly_name() {
        set(ALLY_NAME, value);
    }
    public void setLeader_id() {
        set(LEADER_ID, value);
    }
    public void setExpelled_member() {
        set(EXPELLED_MEMBER, value);
    }
    public void setCrest() {
        set(CREST, value);
    }

}
