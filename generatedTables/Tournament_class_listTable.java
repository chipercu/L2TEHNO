package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Tournament_class_listTable.*;

@Table(
        name = "tournament_class_list",
        fields = {
                @Field(name = CLASS_NAME,
                @Field(name = CLASS_ID,
                @Field(name = TYPE,
        }
)
public class Tournament_class_listTable extends DataBaseTable<Tournament_class_listTable> {

    public static final String CLASS_NAME = "class_name";
    public static final String CLASS_ID = "class_id";
    public static final String TYPE = "type";
    public Tournament_class_listTable() {
super(Tournament_class_listTable.class);
}
    public StringgetClass_name() {
        return get(CLASS_NAME, String.class);
    }
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }

    public void setClass_name() {
        set(CLASS_NAME, value);
    }
    public void setClass_id() {
        set(CLASS_ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }

}
