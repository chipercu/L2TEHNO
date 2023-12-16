package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Tournament_teamsTable.*;

@Table(
        name = "tournament_teams",
        fields = {
                @Field(name = OBJ_ID,
                @Field(name = TYPE,
                @Field(name = TEAM_ID,
                @Field(name = TEAM_NAME,
                @Field(name = LEADER,
                @Field(name = CATEGORY,
                @Field(name = WINS,
                @Field(name = LOSTS,
                @Field(name = STATUS,
        }
)
public class Tournament_teamsTable extends DataBaseTable<Tournament_teamsTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String TYPE = "type";
    public static final String TEAM_ID = "team_id";
    public static final String TEAM_NAME = "team_name";
    public static final String LEADER = "leader";
    public static final String CATEGORY = "category";
    public static final String WINS = "wins";
    public static final String LOSTS = "losts";
    public static final String STATUS = "status";
    public Tournament_teamsTable() {
super(Tournament_teamsTable.class);
}
    public IntegergetObj_id() {
        return get(OBJ_ID, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }
    public IntegergetTeam_id() {
        return get(TEAM_ID, Integer.class);
    }
    public StringgetTeam_name() {
        return get(TEAM_NAME, String.class);
    }
    public IntegergetLeader() {
        return get(LEADER, Integer.class);
    }
    public IntegergetCategory() {
        return get(CATEGORY, Integer.class);
    }
    public IntegergetWins() {
        return get(WINS, Integer.class);
    }
    public IntegergetLosts() {
        return get(LOSTS, Integer.class);
    }
    public IntegergetStatus() {
        return get(STATUS, Integer.class);
    }

    public void setObj_id() {
        set(OBJ_ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setTeam_id() {
        set(TEAM_ID, value);
    }
    public void setTeam_name() {
        set(TEAM_NAME, value);
    }
    public void setLeader() {
        set(LEADER, value);
    }
    public void setCategory() {
        set(CATEGORY, value);
    }
    public void setWins() {
        set(WINS, value);
    }
    public void setLosts() {
        set(LOSTS, value);
    }
    public void setStatus() {
        set(STATUS, value);
    }

}
