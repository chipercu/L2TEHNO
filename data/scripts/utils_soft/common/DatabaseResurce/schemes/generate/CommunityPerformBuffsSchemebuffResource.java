package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunityPerformBuffsSchemebuffResource.*;

@Table(
        name = "community_perform_buffs_schemebuff",
        primary_key = {},
        fields = {
                @Field(name = SCHEME_ID , data_type = "int"),
                @Field(name = BUFF_ID , data_type = "int"),
                @Field(name = INDEX_ , data_type = "varchar" , defValue = @DefValue(String = "")),
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
        return get(SCHEME_ID, Integer.class);
    }
    public Integer getBuffId() {
        return get(BUFF_ID, Integer.class);
    }
    public String getIndex() {
        return get(INDEX_, String.class);
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
