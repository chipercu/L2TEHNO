package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Bbs_lotteryTable.*;

@Table(
        name = "bbs_lottery",
        fields = {
                @Field(name = COUNT,
                @Field(name = TYPE,
                @Field(name = NAME,
        }
)
public class Bbs_lotteryTable extends DataBaseTable<Bbs_lotteryTable> {

    public static final String COUNT = "count";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public Bbs_lotteryTable() {
super(Bbs_lotteryTable.class);
}
    public ObjectgetCount() {
        return get(COUNT, Object.class);
    }
    public StringgetType() {
        return get(TYPE, String.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }

    public void setCount() {
        set(COUNT, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setName() {
        set(NAME, value);
    }

}
