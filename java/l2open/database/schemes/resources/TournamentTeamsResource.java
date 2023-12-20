package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.TournamentTeamsResource.*;

@Table(
        name = "tournament_teams",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = TYPE , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TEAM_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 255 , name = TEAM_NAME , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = LEADER , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = CATEGORY , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = WINS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOSTS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = STATUS , defValue = "0"),
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
        return get(OBJ_ID);
    }
    public Integer getType() {
        return get(TYPE);
    }
    public Integer getTeamId() {
        return get(TEAM_ID);
    }
    public String getTeamName() {
        return get(TEAM_NAME);
    }
    public Integer getLeader() {
        return get(LEADER);
    }
    public Integer getCategory() {
        return get(CATEGORY);
    }
    public Integer getWins() {
        return get(WINS);
    }
    public Integer getLosts() {
        return get(LOSTS);
    }
    public Integer getStatus() {
        return get(STATUS);
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
