package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.KillcountResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "killcount",
        primary_key = {CHAR_ID,NPC_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "null"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = NPC_ID , defValue = "null"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = COUNT , defValue = "0"),
        }
)
public class KillcountResource extends DataBaseTable<KillcountResource> {

    public static final String CHAR_ID = "char_id";
    public static final String NPC_ID = "npc_id";
    public static final String COUNT = "count";

    public KillcountResource() {
        super(KillcountResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getCount() {
        return get(COUNT);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setCount(Integer value) {
        set(COUNT, value);
    }

}
