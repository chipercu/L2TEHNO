package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.GamesTable.*;

@Table(
        name = "games",
        fields = {
                @Field(name = ID,
                @Field(name = IDNR,
                @Field(name = NUMBER1,
                @Field(name = NUMBER2,
                @Field(name = PRIZE,
                @Field(name = NEWPRIZE,
                @Field(name = PRIZE1,
                @Field(name = PRIZE2,
                @Field(name = PRIZE3,
                @Field(name = ENDDATE,
                @Field(name = FINISHED,
        }
)
public class GamesTable extends DataBaseTable<GamesTable> {

    public static final String ID = "id";
    public static final String IDNR = "idnr";
    public static final String NUMBER1 = "number1";
    public static final String NUMBER2 = "number2";
    public static final String PRIZE = "prize";
    public static final String NEWPRIZE = "newprize";
    public static final String PRIZE1 = "prize1";
    public static final String PRIZE2 = "prize2";
    public static final String PRIZE3 = "prize3";
    public static final String ENDDATE = "enddate";
    public static final String FINISHED = "finished";
    public GamesTable() {
super(GamesTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetIdnr() {
        return get(IDNR, Integer.class);
    }
    public IntegergetNumber1() {
        return get(NUMBER1, Integer.class);
    }
    public IntegergetNumber2() {
        return get(NUMBER2, Integer.class);
    }
    public ObjectgetPrize() {
        return get(PRIZE, Object.class);
    }
    public ObjectgetNewprize() {
        return get(NEWPRIZE, Object.class);
    }
    public ObjectgetPrize1() {
        return get(PRIZE1, Object.class);
    }
    public ObjectgetPrize2() {
        return get(PRIZE2, Object.class);
    }
    public ObjectgetPrize3() {
        return get(PRIZE3, Object.class);
    }
    public DoublegetEnddate() {
        return get(ENDDATE, Double.class);
    }
    public IntegergetFinished() {
        return get(FINISHED, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setIdnr() {
        set(IDNR, value);
    }
    public void setNumber1() {
        set(NUMBER1, value);
    }
    public void setNumber2() {
        set(NUMBER2, value);
    }
    public void setPrize() {
        set(PRIZE, value);
    }
    public void setNewprize() {
        set(NEWPRIZE, value);
    }
    public void setPrize1() {
        set(PRIZE1, value);
    }
    public void setPrize2() {
        set(PRIZE2, value);
    }
    public void setPrize3() {
        set(PRIZE3, value);
    }
    public void setEnddate() {
        set(ENDDATE, value);
    }
    public void setFinished() {
        set(FINISHED, value);
    }

}
