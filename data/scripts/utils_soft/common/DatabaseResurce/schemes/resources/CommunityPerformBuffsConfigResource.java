package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsConfigResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "community_perform_buffs_config",
        primary_key = {},
        fields = {
                @Column(is_null = YES , type = VARCHAR , type_size = 100 , name = NAME , defValue = ""),
                @Column(is_null = YES , type = INT , type_size = 0 , name = VALUE , defValue = "NULL"),
        }
)
public class CommunityPerformBuffsConfigResource extends DataBaseTable<CommunityPerformBuffsConfigResource> {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public CommunityPerformBuffsConfigResource() {
        super(CommunityPerformBuffsConfigResource.class);
}

    public String getName() {
        return get(NAME);
    }
    public Integer getValue() {
        return get(VALUE);
    }

    public void setName(String value) {
        set(NAME, value);
    }
    public void setValue(Integer value) {
        set(VALUE, value);
    }

}
