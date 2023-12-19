package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.HennaTreesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "henna_trees",
        primary_key = {CLASS_ID,SYMBOL_ID},
        fields = {
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = SYMBOL_ID , defValue = "0"),
        }
)
public class HennaTreesResource extends DataBaseTable<HennaTreesResource> {

    public static final String CLASS_ID = "class_id";
    public static final String SYMBOL_ID = "symbol_id";

    public HennaTreesResource() {
        super(HennaTreesResource.class);
}

    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public Integer getSymbolId() {
        return get(SYMBOL_ID);
    }

    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setSymbolId(Integer value) {
        set(SYMBOL_ID, value);
    }

}
