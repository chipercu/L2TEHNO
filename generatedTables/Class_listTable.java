package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Class_listTable.*;

@Table(
        name = "class_list",
        fields = {
                @Field(name = CLASS_NAME,
                @Field(name = ID,
                @Field(name = PARENT_ID,
                @Field(name = PARENT_ID2,
        }
)
public class Class_listTable extends DataBaseTable<Class_listTable> {

    public static final String CLASS_NAME = "class_name";
    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String PARENT_ID2 = "parent_id2";
    public Class_listTable() {
super(Class_listTable.class);
}
    public StringgetClass_name() {
        return get(CLASS_NAME, String.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetParent_id() {
        return get(PARENT_ID, Integer.class);
    }
    public IntegergetParent_id2() {
        return get(PARENT_ID2, Integer.class);
    }

    public void setClass_name() {
        set(CLASS_NAME, value);
    }
    public void setId() {
        set(ID, value);
    }
    public void setParent_id() {
        set(PARENT_ID, value);
    }
    public void setParent_id2() {
        set(PARENT_ID2, value);
    }

}
