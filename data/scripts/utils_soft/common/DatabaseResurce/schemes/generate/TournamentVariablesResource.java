package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TournamentVariablesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "tournament_variables",
        primary_key = {NAME},
        fields = {
                @Field(name = NAME , type = VARCHAR , type_size = 255 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = VALUE , type = VARCHAR , type_size = 255 , nullable = true , defValue = @DefValue(String = "")),
        }
)
public class TournamentVariablesResource extends DataBaseTable<TournamentVariablesResource> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public TournamentVariablesResource() {
super(TournamentVariablesResource.class);
}


    public TournamentVariablesResource(String name){
        super(TournamentVariablesResource.class);
        getSTAT_SET().set(NAME, name);
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
