package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RecitemsTable.*;

@Table(
        name = "recitems",
        fields = {
                @Field(name = RID),
                @Field(name = ITEM),
                @Field(name = Q),
                @Field(name = HAS_RECIPE),
        }
)
public class RecitemsTable extends DataBaseTable<RecitemsTable> {

    public static final String RID = "rid";
    public static final String ITEM = "item";
    public static final String Q = "q";
    public static final String HAS_RECIPE = "has_recipe";

    public RecitemsTable() {
super(RecitemsTable.class);
}

    public Integer getRid() {
        return get(RID, Integer.class);
    }
    public Integer getItem() {
        return get(ITEM, Integer.class);
    }
    public Integer getQ() {
        return get(Q, Integer.class);
    }
    public Integer getHasRecipe() {
        return get(HAS_RECIPE, Integer.class);
    }

    public void setRid(Integer value) {
        set(RID, value);
    }
    public void setItem(Integer value) {
        set(ITEM, value);
    }
    public void setQ(Integer value) {
        set(Q, value);
    }
    public void setHasRecipe(Integer value) {
        set(HAS_RECIPE, value);
    }

}
