package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.GlobalTasksTable.*;

@Table(
        name = "global_tasks",
        fields = {
                @Field(name = ID),
                @Field(name = TASK),
                @Field(name = TYPE),
                @Field(name = LAST_ACTIVATION),
                @Field(name = PARAM1),
                @Field(name = PARAM2),
                @Field(name = PARAM3),
        }
)
public class GlobalTasksTable extends DataBaseTable<GlobalTasksTable> {

    public static final String ID = "id";
    public static final String TASK = "task";
    public static final String TYPE = "type";
    public static final String LAST_ACTIVATION = "last_activation";
    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    public static final String PARAM3 = "param3";

    public GlobalTasksTable() {
super(GlobalTasksTable.class);
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
