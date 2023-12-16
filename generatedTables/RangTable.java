package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.RangTable.*;

@Table(
        name = "rang",
        fields = {
                @Field(name = CHARID,
                @Field(name = RANGPOINT,
        }
)
public class RangTable extends DataBaseTable<RangTable> {

    public static final String CHARID = "charId";
    public static final String RANGPOINT = "rangPoint";
    public RangTable() {
super(RangTable.class);
}
    public IntegergetCharId() {
        return get(CHARID, Integer.class);
    }
    public ObjectgetRangPoint() {
        return get(RANGPOINT, Object.class);
    }

    public void setCharId() {
        set(CHARID, value);
    }
    public void setRangPoint() {
        set(RANGPOINT, value);
    }

}
