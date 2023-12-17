package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ServerVariablesResource.*;

@Table(
        name = "server_variables",
        primary_key = {NAME},
        fields = {
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = VALUE , data_type = "varchar" , defValue = @DefValue(String = "")),
        }
)
public class ServerVariablesResource extends DataBaseTable<ServerVariablesResource> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public ServerVariablesResource() {
super(ServerVariablesResource.class);
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
