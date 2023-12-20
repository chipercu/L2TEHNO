package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.AiParamsResource.*;

@Table(
        name = "ai_params",
        primary_key = {NPC_ID,PARAM},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 45 , name = NAME , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 25 , name = PARAM , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = VALUE , defValue = ""),
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
