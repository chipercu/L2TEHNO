package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.DroplistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "droplist",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = MOB_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_ID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = MIN , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = MAX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SWEEP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHANCE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CATEGORY , defValue = "0"),
        }
)
public class DroplistResource extends DataBaseTable<DroplistResource> {

    public static final String MOB_ID = "mobId";
    public static final String ITEM_ID = "itemId";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String SWEEP = "sweep";
    public static final String CHANCE = "chance";
    public static final String CATEGORY = "category";

    public DroplistResource() {
        super(DroplistResource.class);
}

    public Integer getMobId() {
        return get(MOB_ID);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Long getMin() {
        return get(MIN);
    }
    public Long getMax() {
        return get(MAX);
    }
    public Integer getSweep() {
        return get(SWEEP);
    }
    public Integer getChance() {
        return get(CHANCE);
    }
    public Integer getCategory() {
        return get(CATEGORY);
    }

    public void setMobId(Integer value) {
        set(MOB_ID, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setMin(Long value) {
        set(MIN, value);
    }
    public void setMax(Long value) {
        set(MAX, value);
    }
    public void setSweep(Integer value) {
        set(SWEEP, value);
    }
    public void setChance(Integer value) {
        set(CHANCE, value);
    }
    public void setCategory(Integer value) {
        set(CATEGORY, value);
    }

}
