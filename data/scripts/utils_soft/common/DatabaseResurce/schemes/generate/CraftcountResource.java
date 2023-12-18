package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CraftcountResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "craftcount",
        primary_key = {CHAR_ID,ITEM_ID},
        fields = {
                @Field(name = CHAR_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = ITEM_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = COUNT , type = BIGINT , nullable = true , defValue = @DefValue(Long = 0)),
        }
)
public class CraftcountResource extends DataBaseTable<CraftcountResource> {

    public static final String CHAR_ID = "char_id";
    public static final String ITEM_ID = "item_id";
    public static final String COUNT = "count";

    public CraftcountResource() {
super(CraftcountResource.class);
}


    public CraftcountResource(String char_id,String item_id){
        super(CraftcountResource.class);
        getSTAT_SET().set(CHAR_ID, char_id);
        getSTAT_SET().set(ITEM_ID, item_id);
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
