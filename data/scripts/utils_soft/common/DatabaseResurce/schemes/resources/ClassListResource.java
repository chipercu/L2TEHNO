package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ClassListResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "class_list",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 19 , name = CLASS_NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PARENT_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PARENT_ID2 , defValue = "0"),
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
        return get(CLASS_NAME);
    }
    public Integer getId() {
        return get(ID);
    }
    public Integer getParentId() {
        return get(PARENT_ID);
    }
    public Integer getParentId2() {
        return get(PARENT_ID2);
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
