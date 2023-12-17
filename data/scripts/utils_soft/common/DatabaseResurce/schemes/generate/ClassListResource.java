package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClassListResource.*;

@Table(
        name = "class_list",
        primary_key = {ID},
        fields = {
                @Field(name = CLASS_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = PARENT_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = PARENT_ID2 , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class ClassListResource extends DataBaseTable<ClassListResource> {

    public static final String CLASS_NAME = "class_name";
    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String PARENT_ID2 = "parent_id2";

    public ClassListResource() {
super(ClassListResource.class);
}

    public String getClassName() {
        return get(CLASS_NAME, String.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getParentId() {
        return get(PARENT_ID, Integer.class);
    }
    public Integer getParentId2() {
        return get(PARENT_ID2, Integer.class);
    }

    public void setClassName(String value) {
        set(CLASS_NAME, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }
    public void setParentId(Integer value) {
        set(PARENT_ID, value);
    }
    public void setParentId2(Integer value) {
        set(PARENT_ID2, value);
    }

}
