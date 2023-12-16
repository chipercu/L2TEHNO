package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterVariablesTable.*;

@Table(
        name = "character_variables",
        fields = {
                @Field(name = OBJ_ID),
                @Field(name = TYPE),
                @Field(name = NAME),
                @Field(name = VALUE),
                @Field(name = EXPIRE_TIME),
        }
)
public class CharacterVariablesTable extends DataBaseTable<CharacterVariablesTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String EXPIRE_TIME = "expire_time";

    public CharacterVariablesTable() {
super(CharacterVariablesTable.class);
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
