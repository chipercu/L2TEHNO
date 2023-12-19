package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.TournamentVariablesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "tournament_variables",
        primary_key = {NAME},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = NAME , defValue = ""),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = VALUE , defValue = "NULL"),
        }
)
public class TournamentVariablesResource extends DataBaseTable<TournamentVariablesResource> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public TournamentVariablesResource() {
        super(TournamentVariablesResource.class);
}

    public String getName() {
        return get(NAME);
    }
    public String getValue() {
        return get(VALUE);
    }

    public void setName(String value) {
        set(NAME, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }

}
