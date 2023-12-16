package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Olympiad_historyTable.*;

@Table(
        name = "olympiad_history",
        fields = {
                @Field(name = OBJECT_ID_1,
                @Field(name = OBJECT_ID_2,
                @Field(name = CLASS_ID_1,
                @Field(name = CLASS_ID_2,
                @Field(name = NAME_1,
                @Field(name = NAME_2,
                @Field(name = GAME_START_TIME,
                @Field(name = GAME_TIME,
                @Field(name = GAME_STATUS,
                @Field(name = GAME_TYPE,
                @Field(name = OLD,
        }
)
public class Olympiad_historyTable extends DataBaseTable<Olympiad_historyTable> {

    public static final String OBJECT_ID_1 = "object_id_1";
    public static final String OBJECT_ID_2 = "object_id_2";
    public static final String CLASS_ID_1 = "class_id_1";
    public static final String CLASS_ID_2 = "class_id_2";
    public static final String NAME_1 = "name_1";
    public static final String NAME_2 = "name_2";
    public static final String GAME_START_TIME = "game_start_time";
    public static final String GAME_TIME = "game_time";
    public static final String GAME_STATUS = "game_status";
    public static final String GAME_TYPE = "game_type";
    public static final String OLD = "old";
    public Olympiad_historyTable() {
super(Olympiad_historyTable.class);
}
    public IntegergetObject_id_1() {
        return get(OBJECT_ID_1, Integer.class);
    }
    public IntegergetObject_id_2() {
        return get(OBJECT_ID_2, Integer.class);
    }
    public IntegergetClass_id_1() {
        return get(CLASS_ID_1, Integer.class);
    }
    public IntegergetClass_id_2() {
        return get(CLASS_ID_2, Integer.class);
    }
    public StringgetName_1() {
        return get(NAME_1, String.class);
    }
    public StringgetName_2() {
        return get(NAME_2, String.class);
    }
    public ObjectgetGame_start_time() {
        return get(GAME_START_TIME, Object.class);
    }
    public IntegergetGame_time() {
        return get(GAME_TIME, Integer.class);
    }
    public IntegergetGame_status() {
        return get(GAME_STATUS, Integer.class);
    }
    public IntegergetGame_type() {
        return get(GAME_TYPE, Integer.class);
    }
    public IntegergetOld() {
        return get(OLD, Integer.class);
    }

    public void setObject_id_1() {
        set(OBJECT_ID_1, value);
    }
    public void setObject_id_2() {
        set(OBJECT_ID_2, value);
    }
    public void setClass_id_1() {
        set(CLASS_ID_1, value);
    }
    public void setClass_id_2() {
        set(CLASS_ID_2, value);
    }
    public void setName_1() {
        set(NAME_1, value);
    }
    public void setName_2() {
        set(NAME_2, value);
    }
    public void setGame_start_time() {
        set(GAME_START_TIME, value);
    }
    public void setGame_time() {
        set(GAME_TIME, value);
    }
    public void setGame_status() {
        set(GAME_STATUS, value);
    }
    public void setGame_type() {
        set(GAME_TYPE, value);
    }
    public void setOld() {
        set(OLD, value);
    }

}
