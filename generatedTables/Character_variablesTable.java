package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_variablesTable.*;

@Table(
        name = "character_variables",
        fields = {
                @Field(name = OBJ_ID,
                @Field(name = TYPE,
                @Field(name = NAME,
                @Field(name = VALUE,
                @Field(name = EXPIRE_TIME,
        }
)
public class Character_variablesTable extends DataBaseTable<Character_variablesTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String EXPIRE_TIME = "expire_time";
    public Character_variablesTable() {
super(Character_variablesTable.class);
}
    public IntegergetObj_id() {
        return get(OBJ_ID, Integer.class);
    }
    public StringgetType() {
        return get(TYPE, String.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetValue() {
        return get(VALUE, String.class);
    }
    public ObjectgetExpire_time() {
        return get(EXPIRE_TIME, Object.class);
    }

    public void setObj_id() {
        set(OBJ_ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setValue() {
        set(VALUE, value);
    }
    public void setExpire_time() {
        set(EXPIRE_TIME, value);
    }

}
