package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Community_perform_buffs_schemebuffTable.*;

@Table(
        name = "community_perform_buffs_schemebuff",
        fields = {
                @Field(name = SCHEME_ID,
                @Field(name = BUFF_ID,
                @Field(name = INDEX_,
        }
)
public class Community_perform_buffs_schemebuffTable extends DataBaseTable<Community_perform_buffs_schemebuffTable> {

    public static final String SCHEME_ID = "scheme_id";
    public static final String BUFF_ID = "buff_id";
    public static final String INDEX_ = "index_";
    public Community_perform_buffs_schemebuffTable() {
super(Community_perform_buffs_schemebuffTable.class);
}
    public IntegergetScheme_id() {
        return get(SCHEME_ID, Integer.class);
    }
    public IntegergetBuff_id() {
        return get(BUFF_ID, Integer.class);
    }
    public StringgetIndex_() {
        return get(INDEX_, String.class);
    }

    public void setScheme_id() {
        set(SCHEME_ID, value);
    }
    public void setBuff_id() {
        set(BUFF_ID, value);
    }
    public void setIndex_() {
        set(INDEX_, value);
    }

}
