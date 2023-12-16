package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.RecitemsTable.*;

@Table(
        name = "recitems",
        fields = {
                @Field(name = RID,
                @Field(name = ITEM,
                @Field(name = Q,
                @Field(name = HAS_RECIPE,
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
    public IntegergetRid() {
        return get(RID, Integer.class);
    }
    public IntegergetItem() {
        return get(ITEM, Integer.class);
    }
    public IntegergetQ() {
        return get(Q, Integer.class);
    }
    public IntegergetHas_recipe() {
        return get(HAS_RECIPE, Integer.class);
    }

    public void setRid() {
        set(RID, value);
    }
    public void setItem() {
        set(ITEM, value);
    }
    public void setQ() {
        set(Q, value);
    }
    public void setHas_recipe() {
        set(HAS_RECIPE, value);
    }

}
