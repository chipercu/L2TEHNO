package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Community_perform_buffs_configTable.*;

@Table(
        name = "community_perform_buffs_config",
        fields = {
                @Field(name = NAME,
                @Field(name = VALUE,
        }
)
public class Community_perform_buffs_configTable extends DataBaseTable<Community_perform_buffs_configTable> {

    public static final String NAME = "name";
    public static final String VALUE = "value";
    public Community_perform_buffs_configTable() {
super(Community_perform_buffs_configTable.class);
}
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetValue() {
        return get(VALUE, Integer.class);
    }

    public void setName() {
        set(NAME, value);
    }
    public void setValue() {
        set(VALUE, value);
    }

}
