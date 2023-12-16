package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.DroplistTable.*;

@Table(
        name = "droplist",
        fields = {
                @Field(name = MOB_ID),
                @Field(name = ITEM_ID),
                @Field(name = MIN),
                @Field(name = MAX),
                @Field(name = SWEEP),
                @Field(name = CHANCE),
                @Field(name = CATEGORY),
        }
)
public class DroplistTable extends DataBaseTable<DroplistTable> {

    public static final String MOB_ID = "mobId";
    public static final String ITEM_ID = "itemId";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String SWEEP = "sweep";
    public static final String CHANCE = "chance";
    public static final String CATEGORY = "category";

    public DroplistTable() {
super(DroplistTable.class);
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
