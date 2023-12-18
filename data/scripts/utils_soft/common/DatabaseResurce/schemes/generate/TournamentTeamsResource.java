package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TournamentTeamsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "tournament_teams",
        primary_key = {},
        fields = {
                @Field(name = OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = TEAM_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = TEAM_NAME , type = VARCHAR , type_size = 255 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = LEADER , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CATEGORY , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = WINS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LOSTS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = STATUS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class TournamentTeamsResource extends DataBaseTable<TournamentTeamsResource> {

    public static final String OBJ_ID = "obj_id";
    public static final String TYPE = "type";
    public static final String TEAM_ID = "team_id";
    public static final String TEAM_NAME = "team_name";
    public static final String LEADER = "leader";
    public static final String CATEGORY = "category";
    public static final String WINS = "wins";
    public static final String LOSTS = "losts";
    public static final String STATUS = "status";

    public TournamentTeamsResource() {
super(TournamentTeamsResource.class);
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
