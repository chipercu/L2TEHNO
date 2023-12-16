package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Heroes_diaryTable.*;

@Table(
        name = "heroes_diary",
        fields = {
                @Field(name = CHARID,
                @Field(name = TIME,
                @Field(name = ACTION,
                @Field(name = PARAM,
        }
)
public class Heroes_diaryTable extends DataBaseTable<Heroes_diaryTable> {

    public static final String CHARID = "charId";
    public static final String TIME = "time";
    public static final String ACTION = "action";
    public static final String PARAM = "param";
    public Heroes_diaryTable() {
super(Heroes_diaryTable.class);
}
    public IntegergetCharId() {
        return get(CHARID, Integer.class);
    }
    public ObjectgetTime() {
        return get(TIME, Object.class);
    }
    public IntegergetAction() {
        return get(ACTION, Integer.class);
    }
    public IntegergetParam() {
        return get(PARAM, Integer.class);
    }

    public void setCharId() {
        set(CHARID, value);
    }
    public void setTime() {
        set(TIME, value);
    }
    public void setAction() {
        set(ACTION, value);
    }
    public void setParam() {
        set(PARAM, value);
    }

}
