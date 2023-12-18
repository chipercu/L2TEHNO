package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.BbsLotteryResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "bbs_lottery",
        primary_key = {},
        fields = {
                @Field(name = COUNT , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = TYPE , type = VARCHAR , type_size = 86 , nullable = false , defValue = @DefValue(String = "0")),
                @Field(name = NAME , type = VARCHAR , type_size = 86 , nullable = false , defValue = @DefValue(String = "0")),
        }
)
public class BbsLotteryResource extends DataBaseTable<BbsLotteryResource> {

    public static final String COUNT = "count";
    public static final String TYPE = "type";
    public static final String NAME = "name";

    public BbsLotteryResource() {
super(BbsLotteryResource.class);
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
