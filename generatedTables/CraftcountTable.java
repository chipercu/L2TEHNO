package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.CraftcountTable.*;

@Table(
        name = "craftcount",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = ITEM_ID,
                @Field(name = COUNT,
        }
)
public class CraftcountTable extends DataBaseTable<CraftcountTable> {

    public static final String CHAR_ID = "char_id";
    public static final String ITEM_ID = "item_id";
    public static final String COUNT = "count";
    public CraftcountTable() {
super(CraftcountTable.class);
}
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetItem_id() {
        return get(ITEM_ID, Integer.class);
    }
    public ObjectgetCount() {
        return get(COUNT, Object.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setItem_id() {
        set(ITEM_ID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }

}
