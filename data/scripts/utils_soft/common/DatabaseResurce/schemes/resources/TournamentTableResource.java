package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.TournamentTableResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "tournament_table",
        primary_key = {},
        fields = {
                @Column(is_null = YES , type = INT , type_size = 0 , name = CATEGORY , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = TEAM1ID , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = TEAM1NAME , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = TEAM2ID , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = TEAM2NAME , defValue = "NULL"),
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
        return get(CATEGORY);
    }
    public Integer getTeam1id() {
        return get(TEAM1ID);
    }
    public String getTeam1name() {
        return get(TEAM1NAME);
    }
    public Integer getTeam2id() {
        return get(TEAM2ID);
    }
    public String getTeam2name() {
        return get(TEAM2NAME);
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
