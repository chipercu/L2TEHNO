package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.BbsLotteryResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

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
