package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsSchemebuffResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "community_perform_buffs_schemebuff",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = SCHEME_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BUFF_ID , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 100 , name = INDEX_ , defValue = ""),
        }
)
public class CommunityPerformBuffsSchemebuffResource extends DataBaseTable<CommunityPerformBuffsSchemebuffResource> {

    public static final String SCHEME_ID = "scheme_id";
    public static final String BUFF_ID = "buff_id";
    public static final String INDEX_ = "index_";

    public CommunityPerformBuffsSchemebuffResource() {
        super(CommunityPerformBuffsSchemebuffResource.class);
}

    public Integer getSchemeId() {
        return get(SCHEME_ID);
    }
    public Integer getBuffId() {
        return get(BUFF_ID);
    }
    public String getIndex() {
        return get(INDEX_);
    }

    public void setSchemeId(Integer value) {
        set(SCHEME_ID, value);
    }
    public void setBuffId(Integer value) {
        set(BUFF_ID, value);
    }
    public void setIndex(String value) {
        set(INDEX_, value);
    }

}
