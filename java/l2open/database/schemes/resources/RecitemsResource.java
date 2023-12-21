package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.RecitemsResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "recitems",
        primary_key = {RID,ITEM},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = RID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Q , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HAS_RECIPE , defValue = "0"),
        }
)
public class RecitemsResource extends DataBaseTable<RecitemsResource> {

    public static final String RID = "rid";
    public static final String ITEM = "item";
    public static final String Q = "q";
    public static final String HAS_RECIPE = "has_recipe";

    public RecitemsResource() {
        super(RecitemsResource.class);
}

    public Integer getRid() {
        return get(RID);
    }
    public Integer getItem() {
        return get(ITEM);
    }
    public Integer getQ() {
        return get(Q);
    }
    public Integer getHasRecipe() {
        return get(HAS_RECIPE);
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