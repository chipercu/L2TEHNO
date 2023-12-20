package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.OlympiadNoblesResource.*;

@Table(
        name = "olympiad_nobles",
        primary_key = {CHAR_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = CHAR_NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = OLYMPIAD_POINTS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = OLYMPIAD_POINTS_PAST , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = OLYMPIAD_POINTS_PAST_STATIC , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = COMPETITIONS_DONE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = COMPETITIONS_WIN , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = COMPETITIONS_LOOSE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = NONECLASS_COMPETITIONS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = CLASS_COMPETITIONS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = TEAM_COMPETITIONS , defValue = "0"),
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
