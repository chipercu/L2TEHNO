package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Community_perform_buffs_schemeTable.*;

@Table(
        name = "community_perform_buffs_scheme",
        fields = {
                @Field(name = ID,
                @Field(name = OWNER,
                @Field(name = NAME,
        }
)
public class Community_perform_buffs_schemeTable extends DataBaseTable<Community_perform_buffs_schemeTable> {

    public static final String ID = "id";
    public static final String OWNER = "owner";
    public static final String NAME = "name";
    public Community_perform_buffs_schemeTable() {
super(Community_perform_buffs_schemeTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetOwner() {
        return get(OWNER, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setOwner() {
        set(OWNER, value);
    }
    public void setName() {
        set(NAME, value);
    }

}
