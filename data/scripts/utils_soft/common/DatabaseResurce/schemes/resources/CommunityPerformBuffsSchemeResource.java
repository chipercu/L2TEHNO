package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsSchemeResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "community_perform_buffs_scheme",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OWNER , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 100 , name = NAME , defValue = ""),
        }
)
public class CommunityPerformBuffsSchemeResource extends DataBaseTable<CommunityPerformBuffsSchemeResource> {

    public static final String ID = "id";
    public static final String OWNER = "owner";
    public static final String NAME = "name";

    public CommunityPerformBuffsSchemeResource() {
        super(CommunityPerformBuffsSchemeResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getOwner() {
        return get(OWNER);
    }
    public String getName() {
        return get(NAME);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setOwner(Integer value) {
        set(OWNER, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }

}
