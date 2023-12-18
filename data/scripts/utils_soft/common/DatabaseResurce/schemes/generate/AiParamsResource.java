package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AiParamsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "ai_params",
        primary_key = {NPC_ID,PARAM},
        fields = {
                @Field(name = NPC_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , type = VARCHAR , type_size = 45 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = PARAM , type = VARCHAR , type_size = 25 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = VALUE , type = VARCHAR , type_size = 255 , nullable = false , defValue = @DefValue(String = "")),
        }
)
public class AiParamsResource extends DataBaseTable<AiParamsResource> {

    public static final String NPC_ID = "npc_id";
    public static final String NAME = "name";
    public static final String PARAM = "param";
    public static final String VALUE = "value";

    public AiParamsResource() {
super(AiParamsResource.class);
}


    public AiParamsResource(String npc_id,String param){
        super(AiParamsResource.class);
        getSTAT_SET().set(NPC_ID, npc_id);
        getSTAT_SET().set(PARAM, param);
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
