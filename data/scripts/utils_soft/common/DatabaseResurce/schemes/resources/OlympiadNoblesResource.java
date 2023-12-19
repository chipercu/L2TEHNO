package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.OlympiadNoblesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "olympiad_nobles",
        primary_key = {CHAR_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = CHAR_NAME , defValue = ""),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = OLYMPIAD_POINTS , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = OLYMPIAD_POINTS_PAST , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = OLYMPIAD_POINTS_PAST_STATIC , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = COMPETITIONS_DONE , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = COMPETITIONS_WIN , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = COMPETITIONS_LOOSE , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = NONECLASS_COMPETITIONS , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CLASS_COMPETITIONS , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = TEAM_COMPETITIONS , defValue = "0"),
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

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public String getCharName() {
        return get(CHAR_NAME);
    }
    public Integer getOlympiadPoints() {
        return get(OLYMPIAD_POINTS);
    }
    public Integer getOlympiadPointsPast() {
        return get(OLYMPIAD_POINTS_PAST);
    }
    public Integer getOlympiadPointsPastStatic() {
        return get(OLYMPIAD_POINTS_PAST_STATIC);
    }
    public Integer getCompetitionsDone() {
        return get(COMPETITIONS_DONE);
    }
    public Integer getCompetitionsWin() {
        return get(COMPETITIONS_WIN);
    }
    public Integer getCompetitionsLoose() {
        return get(COMPETITIONS_LOOSE);
    }
    public Integer getNoneclassCompetitions() {
        return get(NONECLASS_COMPETITIONS);
    }
    public Integer getClassCompetitions() {
        return get(CLASS_COMPETITIONS);
    }
    public Integer getTeamCompetitions() {
        return get(TEAM_COMPETITIONS);
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
