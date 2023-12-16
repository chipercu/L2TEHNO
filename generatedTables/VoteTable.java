package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.VoteTable.*;

@Table(
        name = "vote",
        fields = {
                @Field(name = ID,
                @Field(name = HWID,
                @Field(name = VOTE,
        }
)
public class VoteTable extends DataBaseTable<VoteTable> {

    public static final String ID = "id";
    public static final String HWID = "HWID";
    public static final String VOTE = "vote";
    public VoteTable() {
super(VoteTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetHWID() {
        return get(HWID, String.class);
    }
    public IntegergetVote() {
        return get(VOTE, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setHWID() {
        set(HWID, value);
    }
    public void setVote() {
        set(VOTE, value);
    }

}
