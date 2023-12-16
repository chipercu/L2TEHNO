package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HennaTreesTable.*;

@Table(
        name = "henna_trees",
        fields = {
                @Field(name = CLASS_ID),
                @Field(name = SYMBOL_ID),
        }
)
public class HennaTreesTable extends DataBaseTable<HennaTreesTable> {

    public static final String CLASS_ID = "class_id";
    public static final String SYMBOL_ID = "symbol_id";

    public HennaTreesTable() {
super(HennaTreesTable.class);
}

    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public Integer getSymbolId() {
        return get(SYMBOL_ID, Integer.class);
    }

    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setSymbolId(Integer value) {
        set(SYMBOL_ID, value);
    }

}
