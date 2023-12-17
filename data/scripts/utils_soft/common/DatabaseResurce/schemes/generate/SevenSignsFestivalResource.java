package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SevenSignsFestivalResource.*;

@Table(
        name = "seven_signs_festival",
        primary_key = {FESTIVAL_ID,CABAL,CYCLE},
        fields = {
                @Field(name = FESTIVAL_ID , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = CABAL , data_type = "varchar"),
                @Field(name = CYCLE , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = DATE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = SCORE , data_type = "mediumint" , defValue = @DefValue(Integer = 0)),
                @Field(name = MEMBERS , data_type = "varchar"),
                @Field(name = NAMES , data_type = "tinytext"),
        }
)
public class SevenSignsFestivalResource extends DataBaseTable<SevenSignsFestivalResource> {

    public static final String FESTIVAL_ID = "festivalId";
    public static final String CABAL = "cabal";
    public static final String CYCLE = "cycle";
    public static final String DATE = "date";
    public static final String SCORE = "score";
    public static final String MEMBERS = "members";
    public static final String NAMES = "names";

    public SevenSignsFestivalResource() {
super(SevenSignsFestivalResource.class);
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
    public String getNames() {
        return get(NAMES, String.class);
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
    public void setNames(String value) {
        set(NAMES, value);
    }

}
