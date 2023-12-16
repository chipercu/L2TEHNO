package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Server_variablesTable.*;

@Table(
        name = "server_variables",
        fields = {
                @Field(name = NAME,
                @Field(name = VALUE,
        }
)
public class Server_variablesTable extends DataBaseTable<Server_variablesTable> {

    public static final String NAME = "name";
    public static final String VALUE = "value";
    public Server_variablesTable() {
super(Server_variablesTable.class);
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
