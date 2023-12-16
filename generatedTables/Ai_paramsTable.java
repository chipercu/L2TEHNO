package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Ai_paramsTable.*;

@Table(
        name = "ai_params",
        fields = {
                @Field(name = NPC_ID,
                @Field(name = NAME,
                @Field(name = PARAM,
                @Field(name = VALUE,
        }
)
public class Ai_paramsTable extends DataBaseTable<Ai_paramsTable> {

    public static final String NPC_ID = "npc_id";
    public static final String NAME = "name";
    public static final String PARAM = "param";
    public static final String VALUE = "value";
    public Ai_paramsTable() {
super(Ai_paramsTable.class);
}
    public IntegergetNpc_id() {
        return get(NPC_ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetParam() {
        return get(PARAM, String.class);
    }
    public StringgetValue() {
        return get(VALUE, String.class);
    }

    public void setNpc_id() {
        set(NPC_ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setParam() {
        set(PARAM, value);
    }
    public void setValue() {
        set(VALUE, value);
    }

}
