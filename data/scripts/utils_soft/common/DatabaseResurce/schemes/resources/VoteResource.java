package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.VoteResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "vote",
        primary_key = {ID,H_WI_D,VOTE},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 32 , name = H_WI_D , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = VOTE , defValue = "0"),
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
