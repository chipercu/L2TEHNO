package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SevenSignsFestivalTable.*;

@Table(
        name = "seven_signs_festival",
        fields = {
                @Field(name = FESTIVAL_ID),
                @Field(name = CABAL),
                @Field(name = CYCLE),
                @Field(name = DATE),
                @Field(name = SCORE),
                @Field(name = MEMBERS),
                @Field(name = NAMES),
        }
)
public class SevenSignsFestivalTable extends DataBaseTable<SevenSignsFestivalTable> {

    public static final String FESTIVAL_ID = "festivalId";
    public static final String CABAL = "cabal";
    public static final String CYCLE = "cycle";
    public static final String DATE = "date";
    public static final String SCORE = "score";
    public static final String MEMBERS = "members";
    public static final String NAMES = "names";

    public SevenSignsFestivalTable() {
super(SevenSignsFestivalTable.class);
}

    public Integer getFestivalId() {
        return get(FESTIVAL_ID, Integer.class);
    }
    public String getCabal() {
        return get(CABAL, String.class);
    }
    public Integer getCycle() {
        return get(CYCLE, Integer.class);
    }
    public Long getDate() {
        return get(DATE, Long.class);
    }
    public Integer getScore() {
        return get(SCORE, Integer.class);
    }
    public String getMembers() {
        return get(MEMBERS, String.class);
    }
    public Object getNames() {
        return get(NAMES, Object.class);
    }

    public void setFestivalId(Integer value) {
        set(FESTIVAL_ID, value);
    }
    public void setCabal(String value) {
        set(CABAL, value);
    }
    public void setCycle(Integer value) {
        set(CYCLE, value);
    }
    public void setDate(Long value) {
        set(DATE, value);
    }
    public void setScore(Integer value) {
        set(SCORE, value);
    }
    public void setMembers(String value) {
        set(MEMBERS, value);
    }
    public void setNames(Object value) {
        set(NAMES, value);
    }

}
