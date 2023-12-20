package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.DropcountResource.*;

@Table(
        name = "dropcount",
        primary_key = {CHAR_ID,ITEM_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.BIGINT , type_size = 0 , name = COUNT , defValue = "0"),
        }
)
public class DropcountResource extends DataBaseTable<DropcountResource> {

    public static final String CHAR_ID = "char_id";
    public static final String ITEM_ID = "item_id";
    public static final String COUNT = "count";

    public DropcountResource() {
        super(DropcountResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Long getCount() {
        return get(COUNT);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setCount(Long value) {
        set(COUNT, value);
    }

}
