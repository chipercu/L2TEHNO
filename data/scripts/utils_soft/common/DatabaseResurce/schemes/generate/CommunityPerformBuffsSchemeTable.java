package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunityPerformBuffsSchemeTable.*;

@Table(
        name = "community_perform_buffs_scheme",
        fields = {
                @Field(name = ID),
                @Field(name = OWNER),
                @Field(name = NAME),
        }
)
public class CommunityPerformBuffsSchemeTable extends DataBaseTable<CommunityPerformBuffsSchemeTable> {

    public static final String ID = "id";
    public static final String OWNER = "owner";
    public static final String NAME = "name";

    public CommunityPerformBuffsSchemeTable() {
super(CommunityPerformBuffsSchemeTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getOwner() {
        return get(OWNER, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
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
