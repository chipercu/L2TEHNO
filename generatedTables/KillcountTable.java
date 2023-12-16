package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.KillcountTable.*;

@Table(
        name = "killcount",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = NPC_ID,
                @Field(name = COUNT,
        }
)
public class KillcountTable extends DataBaseTable<KillcountTable> {

    public static final String CHAR_ID = "char_id";
    public static final String NPC_ID = "npc_id";
    public static final String COUNT = "count";
    public KillcountTable() {
super(KillcountTable.class);
}
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetNpc_id() {
        return get(NPC_ID, Integer.class);
    }
    public IntegergetCount() {
        return get(COUNT, Integer.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setNpc_id() {
        set(NPC_ID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }

}
