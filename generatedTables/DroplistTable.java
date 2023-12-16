package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.DroplistTable.*;

@Table(
        name = "droplist",
        fields = {
                @Field(name = MOBID,
                @Field(name = ITEMID,
                @Field(name = MIN,
                @Field(name = MAX,
                @Field(name = SWEEP,
                @Field(name = CHANCE,
                @Field(name = CATEGORY,
        }
)
public class DroplistTable extends DataBaseTable<DroplistTable> {

    public static final String MOBID = "mobId";
    public static final String ITEMID = "itemId";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String SWEEP = "sweep";
    public static final String CHANCE = "chance";
    public static final String CATEGORY = "category";
    public DroplistTable() {
super(DroplistTable.class);
}
    public IntegergetMobId() {
        return get(MOBID, Integer.class);
    }
    public IntegergetItemId() {
        return get(ITEMID, Integer.class);
    }
    public ObjectgetMin() {
        return get(MIN, Object.class);
    }
    public ObjectgetMax() {
        return get(MAX, Object.class);
    }
    public IntegergetSweep() {
        return get(SWEEP, Integer.class);
    }
    public IntegergetChance() {
        return get(CHANCE, Integer.class);
    }
    public IntegergetCategory() {
        return get(CATEGORY, Integer.class);
    }

    public void setMobId() {
        set(MOBID, value);
    }
    public void setItemId() {
        set(ITEMID, value);
    }
    public void setMin() {
        set(MIN, value);
    }
    public void setMax() {
        set(MAX, value);
    }
    public void setSweep() {
        set(SWEEP, value);
    }
    public void setChance() {
        set(CHANCE, value);
    }
    public void setCategory() {
        set(CATEGORY, value);
    }

}
