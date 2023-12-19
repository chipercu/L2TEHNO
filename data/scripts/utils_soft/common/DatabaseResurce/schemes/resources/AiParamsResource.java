package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.AiParamsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "ai_params",
        primary_key = {NPC_ID,PARAM},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 45 , name = NAME , defValue = "NULL"),
                @Column(is_null = NO , type = VARCHAR , type_size = 25 , name = PARAM , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = VALUE , defValue = ""),
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

    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public String getParam() {
        return get(PARAM);
    }
    public String getValue() {
        return get(VALUE);
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
