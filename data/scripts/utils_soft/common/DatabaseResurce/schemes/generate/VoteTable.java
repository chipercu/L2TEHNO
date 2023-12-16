package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.VoteTable.*;

@Table(
        name = "vote",
        fields = {
                @Field(name = ID),
                @Field(name = H_WI_D),
                @Field(name = VOTE),
        }
)
public class VoteTable extends DataBaseTable<VoteTable> {

    public static final String ID = "id";
    public static final String H_WI_D = "HWID";
    public static final String VOTE = "vote";

    public VoteTable() {
super(VoteTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getHWID() {
        return get(H_WI_D, String.class);
    }
    public Integer getVote() {
        return get(VOTE, Integer.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setHWID(String value) {
        set(H_WI_D, value);
    }
    public void setVote(Integer value) {
        set(VOTE, value);
    }

}
