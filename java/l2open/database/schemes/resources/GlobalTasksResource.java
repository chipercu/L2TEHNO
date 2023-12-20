package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.GlobalTasksResource.*;

@Table(
        name = "global_tasks",
        primary_key = {ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = TASK , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = TYPE , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LAST_ACTIVATION , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 100 , name = PARAM1 , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 100 , name = PARAM2 , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = PARAM3 , defValue = ""),
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

    public Integer getId() {
        return get(ID);
    }
    public String getTask() {
        return get(TASK);
    }
    public String getType() {
        return get(TYPE);
    }
    public Integer getLastActivation() {
        return get(LAST_ACTIVATION);
    }
    public String getParam1() {
        return get(PARAM1);
    }
    public String getParam2() {
        return get(PARAM2);
    }
    public String getParam3() {
        return get(PARAM3);
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
