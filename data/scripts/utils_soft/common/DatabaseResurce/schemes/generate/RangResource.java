package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RangResource.*;

@Table(
        name = "rang",
        primary_key = {CHAR_ID},
        fields = {
                @Field(name = CHAR_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = RANG_POINT , data_type = "bigint" , defValue = @DefValue(Long = 0)),
        }
)
public class RangResource extends DataBaseTable<RangResource> {

    public static final String CHAR_ID = "charId";
    public static final String RANG_POINT = "rangPoint";

    public RangResource() {
super(RangResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Long getRangPoint() {
        return get(RANG_POINT, Long.class);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setRangPoint(Long value) {
        set(RANG_POINT, value);
    }

}
