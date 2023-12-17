package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TournamentClassListResource.*;

@Table(
        name = "tournament_class_list",
        primary_key = {},
        fields = {
                @Field(name = CLASS_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = CLASS_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , data_type = "int" , defValue = @DefValue(Integer = 0)),
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
        return get(CLASS_NAME, String.class);
    }
    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
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
