package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CraftcountResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "craftcount",
        primary_key = {CHAR_ID,ITEM_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "null"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = YES , type = BIGINT , type_size = 0 , name = COUNT , defValue = "0"),
        }
)
public class CraftcountResource extends DataBaseTable<CraftcountResource> {

    public static final String CHAR_ID = "char_id";
    public static final String ITEM_ID = "item_id";
    public static final String COUNT = "count";

    public CraftcountResource() {
        super(CraftcountResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Long getCount() {
        return get(COUNT);
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
