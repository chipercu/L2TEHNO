package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Henna_treesTable.*;

@Table(
        name = "henna_trees",
        fields = {
                @Field(name = CLASS_ID,
                @Field(name = SYMBOL_ID,
        }
)
public class Henna_treesTable extends DataBaseTable<Henna_treesTable> {

    public static final String CLASS_ID = "class_id";
    public static final String SYMBOL_ID = "symbol_id";
    public Henna_treesTable() {
super(Henna_treesTable.class);
}
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }
    public IntegergetSymbol_id() {
        return get(SYMBOL_ID, Integer.class);
    }

    public void setClass_id() {
        set(CLASS_ID, value);
    }
    public void setSymbol_id() {
        set(SYMBOL_ID, value);
    }

}
