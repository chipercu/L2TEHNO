package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Seven_signs_festivalTable.*;

@Table(
        name = "seven_signs_festival",
        fields = {
                @Field(name = FESTIVALID,
                @Field(name = CABAL,
                @Field(name = CYCLE,
                @Field(name = DATE,
                @Field(name = SCORE,
                @Field(name = MEMBERS,
                @Field(name = NAMES,
        }
)
public class Seven_signs_festivalTable extends DataBaseTable<Seven_signs_festivalTable> {

    public static final String FESTIVALID = "festivalId";
    public static final String CABAL = "cabal";
    public static final String CYCLE = "cycle";
    public static final String DATE = "date";
    public static final String SCORE = "score";
    public static final String MEMBERS = "members";
    public static final String NAMES = "names";
    public Seven_signs_festivalTable() {
super(Seven_signs_festivalTable.class);
}
    public IntegergetFestivalId() {
        return get(FESTIVALID, Integer.class);
    }
    public StringgetCabal() {
        return get(CABAL, String.class);
    }
    public IntegergetCycle() {
        return get(CYCLE, Integer.class);
    }
    public ObjectgetDate() {
        return get(DATE, Object.class);
    }
    public IntegergetScore() {
        return get(SCORE, Integer.class);
    }
    public StringgetMembers() {
        return get(MEMBERS, String.class);
    }
    public ObjectgetNames() {
        return get(NAMES, Object.class);
    }

    public void setFestivalId() {
        set(FESTIVALID, value);
    }
    public void setCabal() {
        set(CABAL, value);
    }
    public void setCycle() {
        set(CYCLE, value);
    }
    public void setDate() {
        set(DATE, value);
    }
    public void setScore() {
        set(SCORE, value);
    }
    public void setMembers() {
        set(MEMBERS, value);
    }
    public void setNames() {
        set(NAMES, value);
    }

}
