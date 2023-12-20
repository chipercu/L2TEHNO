package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.RangResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "rang",
        primary_key = {CHAR_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = RANG_POINT , defValue = "0"),
        }
)
public class RangResource extends DataBaseTable<RangResource> {

    public static final String CHAR_ID = "charId";
    public static final String RANG_POINT = "rangPoint";

    public RangResource() {
        super(RangResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Long getRangPoint() {
        return get(RANG_POINT);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setRangPoint(Long value) {
        set(RANG_POINT, value);
    }

}
