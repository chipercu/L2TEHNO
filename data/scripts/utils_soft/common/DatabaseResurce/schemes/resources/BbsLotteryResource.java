package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.BbsLotteryResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "bbs_lottery",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 86 , name = TYPE , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 86 , name = NAME , defValue = "0"),
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
        return get(COUNT);
    }
    public String getType() {
        return get(TYPE);
    }
    public String getName() {
        return get(NAME);
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
