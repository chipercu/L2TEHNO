package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunityPerformBuffsConfigTable.*;

@Table(
        name = "community_perform_buffs_config",
        fields = {
                @Field(name = NAME),
                @Field(name = VALUE),
        }
)
public class CommunityPerformBuffsConfigTable extends DataBaseTable<CommunityPerformBuffsConfigTable> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public CommunityPerformBuffsConfigTable() {
super(CommunityPerformBuffsConfigTable.class);
}

    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getValue() {
        return get(VALUE, Integer.class);
    }

    public void setName(String value) {
        set(NAME, value);
    }
    public void setValue(Integer value) {
        set(VALUE, value);
    }

}
