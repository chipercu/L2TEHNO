package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.BbsLotteryTable.*;

@Table(
        name = "bbs_lottery",
        fields = {
                @Field(name = COUNT),
                @Field(name = TYPE),
                @Field(name = NAME),
        }
)
public class BbsLotteryTable extends DataBaseTable<BbsLotteryTable> {

    public static final String COUNT = "count";
    public static final String TYPE = "type";
    public static final String NAME = "name";

    public BbsLotteryTable() {
super(BbsLotteryTable.class);
}

    public Long getCount() {
        return get(COUNT, Long.class);
    }
    public String getType() {
        return get(TYPE, String.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }

    public void setCount(Long value) {
        set(COUNT, value);
    }
    public void setType(String value) {
        set(TYPE, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }

}
