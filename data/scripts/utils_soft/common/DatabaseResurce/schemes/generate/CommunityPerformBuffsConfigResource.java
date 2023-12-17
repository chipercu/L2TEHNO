package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunityPerformBuffsConfigResource.*;

@Table(
        name = "community_perform_buffs_config",
        primary_key = {},
        fields = {
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = VALUE , data_type = "int"),
        }
)
public class CommunityPerformBuffsConfigResource extends DataBaseTable<CommunityPerformBuffsConfigResource> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public CommunityPerformBuffsConfigResource() {
super(CommunityPerformBuffsConfigResource.class);
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
