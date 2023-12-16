package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.FishrewardTable.*;

@Table(
        name = "fishreward",
        fields = {
                @Field(name = FISHID),
                @Field(name = REWARDID),
                @Field(name = MIN),
                @Field(name = MAX),
                @Field(name = CHANCE),
        }
)
public class FishrewardTable extends DataBaseTable<FishrewardTable> {

    public static final String FISHID = "fishid";
    public static final String REWARDID = "rewardid";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String CHANCE = "chance";

    public FishrewardTable() {
super(FishrewardTable.class);
}

    public Integer getFishid() {
        return get(FISHID, Integer.class);
    }
    public Integer getRewardid() {
        return get(REWARDID, Integer.class);
    }
    public Integer getMin() {
        return get(MIN, Integer.class);
    }
    public Integer getMax() {
        return get(MAX, Integer.class);
    }
    public Integer getChance() {
        return get(CHANCE, Integer.class);
    }

    public void setFishid(Integer value) {
        set(FISHID, value);
    }
    public void setRewardid(Integer value) {
        set(REWARDID, value);
    }
    public void setMin(Integer value) {
        set(MIN, value);
    }
    public void setMax(Integer value) {
        set(MAX, value);
    }
    public void setChance(Integer value) {
        set(CHANCE, value);
    }

}
