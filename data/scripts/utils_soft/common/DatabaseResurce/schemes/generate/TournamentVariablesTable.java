package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TournamentVariablesTable.*;

@Table(
        name = "tournament_variables",
        fields = {
                @Field(name = NAME),
                @Field(name = VALUE),
        }
)
public class TournamentVariablesTable extends DataBaseTable<TournamentVariablesTable> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public TournamentVariablesTable() {
super(TournamentVariablesTable.class);
}

    public String getName() {
        return get(NAME, String.class);
    }
    public String getValue() {
        return get(VALUE, String.class);
    }

    public void setName(String value) {
        set(NAME, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }

}
