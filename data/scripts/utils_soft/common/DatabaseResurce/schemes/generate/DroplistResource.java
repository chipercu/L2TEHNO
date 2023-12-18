package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.DroplistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "droplist",
        primary_key = {},
        fields = {
                @Field(name = MOB_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ITEM_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MIN , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = MAX , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = SWEEP , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CHANCE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CATEGORY , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
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
        return get(MOB_ID, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Long getMin() {
        return get(MIN, Long.class);
    }
    public Long getMax() {
        return get(MAX, Long.class);
    }
    public Integer getSweep() {
        return get(SWEEP, Integer.class);
    }
    public Integer getChance() {
        return get(CHANCE, Integer.class);
    }
    public Integer getCategory() {
        return get(CATEGORY, Integer.class);
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
