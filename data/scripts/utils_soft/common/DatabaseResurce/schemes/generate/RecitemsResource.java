package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RecitemsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "recitems",
        primary_key = {RID,ITEM},
        fields = {
                @Field(name = RID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ITEM , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Q , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = HAS_RECIPE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public RecitemsResource(String rid,String item){
        super(RecitemsResource.class);
        getSTAT_SET().set(RID, rid);
        getSTAT_SET().set(ITEM, item);
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
