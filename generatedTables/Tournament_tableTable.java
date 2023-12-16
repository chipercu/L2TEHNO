package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Tournament_tableTable.*;

@Table(
        name = "tournament_table",
        fields = {
                @Field(name = CATEGORY,
                @Field(name = TEAM1ID,
                @Field(name = TEAM1NAME,
                @Field(name = TEAM2ID,
                @Field(name = TEAM2NAME,
        }
)
public class Tournament_tableTable extends DataBaseTable<Tournament_tableTable> {

    public static final String CATEGORY = "category";
    public static final String TEAM1ID = "team1id";
    public static final String TEAM1NAME = "team1name";
    public static final String TEAM2ID = "team2id";
    public static final String TEAM2NAME = "team2name";
    public Tournament_tableTable() {
super(Tournament_tableTable.class);
}
    public IntegergetCategory() {
        return get(CATEGORY, Integer.class);
    }
    public IntegergetTeam1id() {
        return get(TEAM1ID, Integer.class);
    }
    public StringgetTeam1name() {
        return get(TEAM1NAME, String.class);
    }
    public IntegergetTeam2id() {
        return get(TEAM2ID, Integer.class);
    }
    public StringgetTeam2name() {
        return get(TEAM2NAME, String.class);
    }

    public void setCategory() {
        set(CATEGORY, value);
    }
    public void setTeam1id() {
        set(TEAM1ID, value);
    }
    public void setTeam1name() {
        set(TEAM1NAME, value);
    }
    public void setTeam2id() {
        set(TEAM2ID, value);
    }
    public void setTeam2name() {
        set(TEAM2NAME, value);
    }

}
