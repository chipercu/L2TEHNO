package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.GlobalTasksResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "global_tasks",
        primary_key = {ID},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = TASK , type = VARCHAR , type_size = 50 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = TYPE , type = VARCHAR , type_size = 50 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = LAST_ACTIVATION , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PARAM1 , type = VARCHAR , type_size = 100 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = PARAM2 , type = VARCHAR , type_size = 100 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = PARAM3 , type = VARCHAR , type_size = 255 , nullable = false , defValue = @DefValue(String = "")),
        }
)
public class GlobalTasksResource extends DataBaseTable<GlobalTasksResource> {

    public static final String ID = "id";
    public static final String TASK = "task";
    public static final String TYPE = "type";
    public static final String LAST_ACTIVATION = "last_activation";
    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    public static final String PARAM3 = "param3";

    public GlobalTasksResource() {
super(GlobalTasksResource.class);
}


    public GlobalTasksResource(String id){
        super(GlobalTasksResource.class);
        getSTAT_SET().set(ID, id);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getTask() {
        return get(TASK, String.class);
    }
    public String getType() {
        return get(TYPE, String.class);
    }
    public Integer getLastActivation() {
        return get(LAST_ACTIVATION, Integer.class);
    }
    public String getParam1() {
        return get(PARAM1, String.class);
    }
    public String getParam2() {
        return get(PARAM2, String.class);
    }
    public String getParam3() {
        return get(PARAM3, String.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setTask(String value) {
        set(TASK, value);
    }
    public void setType(String value) {
        set(TYPE, value);
    }
    public void setLastActivation(Integer value) {
        set(LAST_ACTIVATION, value);
    }
    public void setParam1(String value) {
        set(PARAM1, value);
    }
    public void setParam2(String value) {
        set(PARAM2, value);
    }
    public void setParam3(String value) {
        set(PARAM3, value);
    }

}
