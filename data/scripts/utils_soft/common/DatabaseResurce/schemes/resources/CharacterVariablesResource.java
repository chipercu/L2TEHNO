package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterVariablesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_variables",
        primary_key = {OBJ_ID,TYPE,NAME},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 86 , name = TYPE , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 86 , name = NAME , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 500 , name = VALUE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = EXPIRE_TIME , defValue = "0"),
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
        return get(OBJ_ID);
    }
    public String getType() {
        return get(TYPE);
    }
    public String getName() {
        return get(NAME);
    }
    public String getValue() {
        return get(VALUE);
    }
    public Long getExpireTime() {
        return get(EXPIRE_TIME);
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
