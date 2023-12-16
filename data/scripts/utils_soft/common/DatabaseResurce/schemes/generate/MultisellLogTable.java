package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MultisellLogTable.*;

@Table(
        name = "multisell_log",
        fields = {
                @Field(name = ID),
                @Field(name = DATE),
                @Field(name = ITEM_ID),
                @Field(name = COUNT),
                @Field(name = D_ITEM_ID),
                @Field(name = D_COUNT),
                @Field(name = NAME),
        }
)
public class MultisellLogTable extends DataBaseTable<MultisellLogTable> {

    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String ITEM_ID = "itemId";
    public static final String COUNT = "count";
    public static final String D_ITEM_ID = "dItemId";
    public static final String D_COUNT = "dCount";
    public static final String NAME = "name";

    public MultisellLogTable() {
super(MultisellLogTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getDate() {
        return get(DATE, String.class);
    }
    public String getItemId() {
        return get(ITEM_ID, String.class);
    }
    public String getCount() {
        return get(COUNT, String.class);
    }
    public String getDItemId() {
        return get(D_ITEM_ID, String.class);
    }
    public String getDCount() {
        return get(D_COUNT, String.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setDate(String value) {
        set(DATE, value);
    }
    public void setItemId(String value) {
        set(ITEM_ID, value);
    }
    public void setCount(String value) {
        set(COUNT, value);
    }
    public void setDItemId(String value) {
        set(D_ITEM_ID, value);
    }
    public void setDCount(String value) {
        set(D_COUNT, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }

}
