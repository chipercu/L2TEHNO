package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Tournament_variablesTable.*;

@Table(
        name = "tournament_variables",
        fields = {
                @Field(name = NAME,
                @Field(name = VALUE,
        }
)
public class Tournament_variablesTable extends DataBaseTable<Tournament_variablesTable> {

    public static final String NAME = "name";
    public static final String VALUE = "value";
    public Tournament_variablesTable() {
super(Tournament_variablesTable.class);
}
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetValue() {
        return get(VALUE, String.class);
    }

    public void setName() {
        set(NAME, value);
    }
    public void setValue() {
        set(VALUE, value);
    }

}
