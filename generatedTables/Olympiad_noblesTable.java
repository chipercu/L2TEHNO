package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Olympiad_noblesTable.*;

@Table(
        name = "olympiad_nobles",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = CLASS_ID,
                @Field(name = CHAR_NAME,
                @Field(name = OLYMPIAD_POINTS,
                @Field(name = OLYMPIAD_POINTS_PAST,
                @Field(name = OLYMPIAD_POINTS_PAST_STATIC,
                @Field(name = COMPETITIONS_DONE,
                @Field(name = COMPETITIONS_WIN,
                @Field(name = COMPETITIONS_LOOSE,
                @Field(name = NONECLASS_COMPETITIONS,
                @Field(name = CLASS_COMPETITIONS,
                @Field(name = TEAM_COMPETITIONS,
        }
)
public class Olympiad_noblesTable extends DataBaseTable<Olympiad_noblesTable> {

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
    public Olympiad_noblesTable() {
super(Olympiad_noblesTable.class);
}
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }
    public StringgetChar_name() {
        return get(CHAR_NAME, String.class);
    }
    public IntegergetOlympiad_points() {
        return get(OLYMPIAD_POINTS, Integer.class);
    }
    public IntegergetOlympiad_points_past() {
        return get(OLYMPIAD_POINTS_PAST, Integer.class);
    }
    public IntegergetOlympiad_points_past_static() {
        return get(OLYMPIAD_POINTS_PAST_STATIC, Integer.class);
    }
    public IntegergetCompetitions_done() {
        return get(COMPETITIONS_DONE, Integer.class);
    }
    public IntegergetCompetitions_win() {
        return get(COMPETITIONS_WIN, Integer.class);
    }
    public IntegergetCompetitions_loose() {
        return get(COMPETITIONS_LOOSE, Integer.class);
    }
    public IntegergetNoneclass_competitions() {
        return get(NONECLASS_COMPETITIONS, Integer.class);
    }
    public IntegergetClass_competitions() {
        return get(CLASS_COMPETITIONS, Integer.class);
    }
    public IntegergetTeam_competitions() {
        return get(TEAM_COMPETITIONS, Integer.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setClass_id() {
        set(CLASS_ID, value);
    }
    public void setChar_name() {
        set(CHAR_NAME, value);
    }
    public void setOlympiad_points() {
        set(OLYMPIAD_POINTS, value);
    }
    public void setOlympiad_points_past() {
        set(OLYMPIAD_POINTS_PAST, value);
    }
    public void setOlympiad_points_past_static() {
        set(OLYMPIAD_POINTS_PAST_STATIC, value);
    }
    public void setCompetitions_done() {
        set(COMPETITIONS_DONE, value);
    }
    public void setCompetitions_win() {
        set(COMPETITIONS_WIN, value);
    }
    public void setCompetitions_loose() {
        set(COMPETITIONS_LOOSE, value);
    }
    public void setNoneclass_competitions() {
        set(NONECLASS_COMPETITIONS, value);
    }
    public void setClass_competitions() {
        set(CLASS_COMPETITIONS, value);
    }
    public void setTeam_competitions() {
        set(TEAM_COMPETITIONS, value);
    }

}
