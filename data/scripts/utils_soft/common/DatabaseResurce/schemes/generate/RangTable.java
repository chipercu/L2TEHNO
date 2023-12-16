package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RangTable.*;

@Table(
        name = "rang",
        fields = {
                @Field(name = CHAR_ID),
                @Field(name = RANG_POINT),
        }
)
public class RangTable extends DataBaseTable<RangTable> {

    public static final String CHAR_ID = "charId";
    public static final String RANG_POINT = "rangPoint";

    public RangTable() {
super(RangTable.class);
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
