package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CommunityPerformBuffsSchemeResource.*;

@Table(
        name = "community_perform_buffs_scheme",
        primary_key = {ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OWNER , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 100 , name = NAME , defValue = ""),
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
