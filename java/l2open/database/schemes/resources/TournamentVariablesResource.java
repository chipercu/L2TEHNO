package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.TournamentVariablesResource.*;

@Table(
        name = "tournament_variables",
        primary_key = {NAME},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 255 , name = VALUE , defValue = "NULL"),
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
