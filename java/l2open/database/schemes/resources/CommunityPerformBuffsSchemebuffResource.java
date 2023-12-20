package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.CommunityPerformBuffsSchemebuffResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "community_perform_buffs_schemebuff",
        primary_key = {SCHEME_ID, BUFF_ID, INDEX_},
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
