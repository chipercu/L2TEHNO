package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.TournamentClassListResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "tournament_class_list",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 19 , name = CLASS_NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = TYPE , defValue = "0"),
        }
)
public class TournamentClassListResource extends DataBaseTable<TournamentClassListResource> {

    public static final String CLASS_NAME = "class_name";
    public static final String CLASS_ID = "class_id";
    public static final String TYPE = "type";

    public TournamentClassListResource() {
        super(TournamentClassListResource.class);
}

    public String getClassName() {
        return get(CLASS_NAME);
    }
    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public Integer getType() {
        return get(TYPE);
    }

    public void setClassName(String value) {
        set(CLASS_NAME, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }

}
