package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.VoteResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "vote",
        primary_key = {ID,H_WI_D,VOTE},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = H_WI_D , type = VARCHAR , type_size = 32 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = VOTE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class VoteResource extends DataBaseTable<VoteResource> {

    public static final String ID = "id";
    public static final String H_WI_D = "HWID";
    public static final String VOTE = "vote";

    public VoteResource() {
super(VoteResource.class);
}


    public VoteResource(String id,String HWID,String vote){
        super(VoteResource.class);
        getSTAT_SET().set(ID, id);
        getSTAT_SET().set(H_WI_D, HWID);
        getSTAT_SET().set(VOTE, vote);
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
