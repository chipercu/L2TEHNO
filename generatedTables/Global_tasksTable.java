package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Global_tasksTable.*;

@Table(
        name = "global_tasks",
        fields = {
                @Field(name = ID,
                @Field(name = TASK,
                @Field(name = TYPE,
                @Field(name = LAST_ACTIVATION,
                @Field(name = PARAM1,
                @Field(name = PARAM2,
                @Field(name = PARAM3,
        }
)
public class Global_tasksTable extends DataBaseTable<Global_tasksTable> {

    public static final String ID = "id";
    public static final String TASK = "task";
    public static final String TYPE = "type";
    public static final String LAST_ACTIVATION = "last_activation";
    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    public static final String PARAM3 = "param3";
    public Global_tasksTable() {
super(Global_tasksTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetTask() {
        return get(TASK, String.class);
    }
    public StringgetType() {
        return get(TYPE, String.class);
    }
    public IntegergetLast_activation() {
        return get(LAST_ACTIVATION, Integer.class);
    }
    public StringgetParam1() {
        return get(PARAM1, String.class);
    }
    public StringgetParam2() {
        return get(PARAM2, String.class);
    }
    public StringgetParam3() {
        return get(PARAM3, String.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setTask() {
        set(TASK, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setLast_activation() {
        set(LAST_ACTIVATION, value);
    }
    public void setParam1() {
        set(PARAM1, value);
    }
    public void setParam2() {
        set(PARAM2, value);
    }
    public void setParam3() {
        set(PARAM3, value);
    }

}
