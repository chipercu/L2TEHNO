package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CommunityPerformBuffsConfigResource.*;

@Table(
        name = "community_perform_buffs_config",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 100 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = VALUE , defValue = "NULL"),
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
