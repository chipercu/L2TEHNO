package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterVariablesResource.*;

@Table(
        name = "character_variables",
        primary_key = {OBJ_ID,TYPE,NAME},
        fields = {
                @Field(name = OBJ_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , data_type = "varchar" , defValue = @DefValue(String = "0")),
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "0")),
                @Field(name = VALUE , data_type = "varchar" , defValue = @DefValue(String = "0")),
                @Field(name = EXPIRE_TIME , data_type = "bigint" , defValue = @DefValue(Long = 0)),
        }
)
public class CharacterVariablesResource extends DataBaseTable<CharacterVariablesResource> {

    public static final String OBJ_ID = "obj_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String EXPIRE_TIME = "expire_time";

    public CharacterVariablesResource() {
super(CharacterVariablesResource.class);
}

    public Integer getObjId() {
        return get(OBJ_ID, Integer.class);
    }
    public String getType() {
        return get(TYPE, String.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getValue() {
        return get(VALUE, String.class);
    }
    public Long getExpireTime() {
        return get(EXPIRE_TIME, Long.class);
    }

    public void setObjId(Integer value) {
        set(OBJ_ID, value);
    }
    public void setType(String value) {
        set(TYPE, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }
    public void setExpireTime(Long value) {
        set(EXPIRE_TIME, value);
    }

}
