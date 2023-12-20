package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.VoteResource.*;

@Table(
        name = "vote",
        primary_key = {ID,H_WI_D,VOTE},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = H_WI_D , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = VOTE , defValue = "0"),
        }
)
public class VoteResource extends DataBaseTable<VoteResource> {

    public static final String ID = "id";
    public static final String H_WI_D = "HWID";
    public static final String VOTE = "vote";

    public VoteResource() {
        super(VoteResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getHWID() {
        return get(H_WI_D);
    }
    public Integer getVote() {
        return get(VOTE);
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
