package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.OlympiadNoblesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "olympiad_nobles",
        primary_key = {CHAR_ID},
        fields = {
                @Field(name = CHAR_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CLASS_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CHAR_NAME , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = OLYMPIAD_POINTS , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = OLYMPIAD_POINTS_PAST , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = OLYMPIAD_POINTS_PAST_STATIC , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = COMPETITIONS_DONE , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = COMPETITIONS_WIN , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = COMPETITIONS_LOOSE , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NONECLASS_COMPETITIONS , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CLASS_COMPETITIONS , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TEAM_COMPETITIONS , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class OlympiadNoblesResource extends DataBaseTable<OlympiadNoblesResource> {

    public static final String CHAR_ID = "char_id";
    public static final String CLASS_ID = "class_id";
    public static final String CHAR_NAME = "char_name";
    public static final String OLYMPIAD_POINTS = "olympiad_points";
    public static final String OLYMPIAD_POINTS_PAST = "olympiad_points_past";
    public static final String OLYMPIAD_POINTS_PAST_STATIC = "olympiad_points_past_static";
    public static final String COMPETITIONS_DONE = "competitions_done";
    public static final String COMPETITIONS_WIN = "competitions_win";
    public static final String COMPETITIONS_LOOSE = "competitions_loose";
    public static final String NONECLASS_COMPETITIONS = "noneclass_competitions";
    public static final String CLASS_COMPETITIONS = "class_competitions";
    public static final String TEAM_COMPETITIONS = "team_competitions";

    public OlympiadNoblesResource() {
super(OlympiadNoblesResource.class);
}


    public OlympiadNoblesResource(String char_id){
        super(OlympiadNoblesResource.class);
        getSTAT_SET().set(CHAR_ID, char_id);
    }

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public String getCharName() {
        return get(CHAR_NAME, String.class);
    }
    public Integer getOlympiadPoints() {
        return get(OLYMPIAD_POINTS, Integer.class);
    }
    public Integer getOlympiadPointsPast() {
        return get(OLYMPIAD_POINTS_PAST, Integer.class);
    }
    public Integer getOlympiadPointsPastStatic() {
        return get(OLYMPIAD_POINTS_PAST_STATIC, Integer.class);
    }
    public Integer getCompetitionsDone() {
        return get(COMPETITIONS_DONE, Integer.class);
    }
    public Integer getCompetitionsWin() {
        return get(COMPETITIONS_WIN, Integer.class);
    }
    public Integer getCompetitionsLoose() {
        return get(COMPETITIONS_LOOSE, Integer.class);
    }
    public Integer getNoneclassCompetitions() {
        return get(NONECLASS_COMPETITIONS, Integer.class);
    }
    public Integer getClassCompetitions() {
        return get(CLASS_COMPETITIONS, Integer.class);
    }
    public Integer getTeamCompetitions() {
        return get(TEAM_COMPETITIONS, Integer.class);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setCharName(String value) {
        set(CHAR_NAME, value);
    }
    public void setOlympiadPoints(Integer value) {
        set(OLYMPIAD_POINTS, value);
    }
    public void setOlympiadPointsPast(Integer value) {
        set(OLYMPIAD_POINTS_PAST, value);
    }
    public void setOlympiadPointsPastStatic(Integer value) {
        set(OLYMPIAD_POINTS_PAST_STATIC, value);
    }
    public void setCompetitionsDone(Integer value) {
        set(COMPETITIONS_DONE, value);
    }
    public void setCompetitionsWin(Integer value) {
        set(COMPETITIONS_WIN, value);
    }
    public void setCompetitionsLoose(Integer value) {
        set(COMPETITIONS_LOOSE, value);
    }
    public void setNoneclassCompetitions(Integer value) {
        set(NONECLASS_COMPETITIONS, value);
    }
    public void setClassCompetitions(Integer value) {
        set(CLASS_COMPETITIONS, value);
    }
    public void setTeamCompetitions(Integer value) {
        set(TEAM_COMPETITIONS, value);
    }

}
