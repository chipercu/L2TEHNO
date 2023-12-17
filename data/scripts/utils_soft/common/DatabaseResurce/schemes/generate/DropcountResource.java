package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.DropcountResource.*;

@Table(
        name = "dropcount",
        primary_key = {CHAR_ID,ITEM_ID},
        fields = {
                @Field(name = CHAR_ID , data_type = "int"),
                @Field(name = ITEM_ID , data_type = "smallint"),
                @Field(name = COUNT , data_type = "bigint" , defValue = @DefValue(Long = 0)),
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
        return get(CHAR_ID, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Long getCount() {
        return get(COUNT, Long.class);
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
