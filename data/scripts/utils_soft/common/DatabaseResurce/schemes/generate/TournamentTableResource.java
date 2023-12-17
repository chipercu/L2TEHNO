package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TournamentTableResource.*;

@Table(
        name = "tournament_table",
        primary_key = {},
        fields = {
                @Field(name = CATEGORY , data_type = "int"),
                @Field(name = TEAM1ID , data_type = "int"),
                @Field(name = TEAM1NAME , data_type = "varchar"),
                @Field(name = TEAM2ID , data_type = "int"),
                @Field(name = TEAM2NAME , data_type = "varchar"),
        }
)
public class TournamentTableResource extends DataBaseTable<TournamentTableResource> {

    public static final String CATEGORY = "category";
    public static final String TEAM1ID = "team1id";
    public static final String TEAM1NAME = "team1name";
    public static final String TEAM2ID = "team2id";
    public static final String TEAM2NAME = "team2name";

    public TournamentTableResource() {
super(TournamentTableResource.class);
}

    public Integer getCategory() {
        return get(CATEGORY, Integer.class);
    }
    public Integer getTeam1id() {
        return get(TEAM1ID, Integer.class);
    }
    public String getTeam1name() {
        return get(TEAM1NAME, String.class);
    }
    public Integer getTeam2id() {
        return get(TEAM2ID, Integer.class);
    }
    public String getTeam2name() {
        return get(TEAM2NAME, String.class);
    }

    public void setCategory(Integer value) {
        set(CATEGORY, value);
    }
    public void setTeam1id(Integer value) {
        set(TEAM1ID, value);
    }
    public void setTeam1name(String value) {
        set(TEAM1NAME, value);
    }
    public void setTeam2id(Integer value) {
        set(TEAM2ID, value);
    }
    public void setTeam2name(String value) {
        set(TEAM2NAME, value);
    }

}
