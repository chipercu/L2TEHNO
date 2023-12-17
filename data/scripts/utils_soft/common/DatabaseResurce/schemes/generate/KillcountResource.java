package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.KillcountResource.*;

@Table(
        name = "killcount",
        primary_key = {CHAR_ID,NPC_ID},
        fields = {
                @Field(name = CHAR_ID , data_type = "int"),
                @Field(name = NPC_ID , data_type = "smallint"),
                @Field(name = COUNT , data_type = "int" , defValue = @DefValue(Integer = 0)),
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
        return get(CHAR_ID, Integer.class);
    }
    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public Integer getCount() {
        return get(COUNT, Integer.class);
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
