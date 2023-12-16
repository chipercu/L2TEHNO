package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TournamentTeamsTable.*;

@Table(
        name = "tournament_teams",
        fields = {
                @Field(name = OBJ_ID),
                @Field(name = TYPE),
                @Field(name = TEAM_ID),
                @Field(name = TEAM_NAME),
                @Field(name = LEADER),
                @Field(name = CATEGORY),
                @Field(name = WINS),
                @Field(name = LOSTS),
                @Field(name = STATUS),
        }
)
public class TournamentTeamsTable extends DataBaseTable<TournamentTeamsTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String TYPE = "type";
    public static final String TEAM_ID = "team_id";
    public static final String TEAM_NAME = "team_name";
    public static final String LEADER = "leader";
    public static final String CATEGORY = "category";
    public static final String WINS = "wins";
    public static final String LOSTS = "losts";
    public static final String STATUS = "status";

    public TournamentTeamsTable() {
super(TournamentTeamsTable.class);
}

    public Integer getObjId() {
        return get(OBJ_ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public Integer getTeamId() {
        return get(TEAM_ID, Integer.class);
    }
    public String getTeamName() {
        return get(TEAM_NAME, String.class);
    }
    public Integer getLeader() {
        return get(LEADER, Integer.class);
    }
    public Integer getCategory() {
        return get(CATEGORY, Integer.class);
    }
    public Integer getWins() {
        return get(WINS, Integer.class);
    }
    public Integer getLosts() {
        return get(LOSTS, Integer.class);
    }
    public Integer getStatus() {
        return get(STATUS, Integer.class);
    }

    public void setObjId(Integer value) {
        set(OBJ_ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setTeamId(Integer value) {
        set(TEAM_ID, value);
    }
    public void setTeamName(String value) {
        set(TEAM_NAME, value);
    }
    public void setLeader(Integer value) {
        set(LEADER, value);
    }
    public void setCategory(Integer value) {
        set(CATEGORY, value);
    }
    public void setWins(Integer value) {
        set(WINS, value);
    }
    public void setLosts(Integer value) {
        set(LOSTS, value);
    }
    public void setStatus(Integer value) {
        set(STATUS, value);
    }

}
