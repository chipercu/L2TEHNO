package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AiParamsTable.*;

@Table(
        name = "ai_params",
        fields = {
                @Field(name = NPC_ID),
                @Field(name = NAME),
                @Field(name = PARAM),
                @Field(name = VALUE),
        }
)
public class AiParamsTable extends DataBaseTable<AiParamsTable> {

    public static final String NPC_ID = "npc_id";
    public static final String NAME = "name";
    public static final String PARAM = "param";
    public static final String VALUE = "value";

    public AiParamsTable() {
super(AiParamsTable.class);
}

    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getParam() {
        return get(PARAM, String.class);
    }
    public String getValue() {
        return get(VALUE, String.class);
    }

    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setParam(String value) {
        set(PARAM, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }

}
