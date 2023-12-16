package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.FishrewardTable.*;

@Table(
        name = "fishreward",
        fields = {
                @Field(name = FISHID,
                @Field(name = REWARDID,
                @Field(name = MIN,
                @Field(name = MAX,
                @Field(name = CHANCE,
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
    public IntegergetFishid() {
        return get(FISHID, Integer.class);
    }
    public IntegergetRewardid() {
        return get(REWARDID, Integer.class);
    }
    public IntegergetMin() {
        return get(MIN, Integer.class);
    }
    public IntegergetMax() {
        return get(MAX, Integer.class);
    }
    public IntegergetChance() {
        return get(CHANCE, Integer.class);
    }

    public void setFishid() {
        set(FISHID, value);
    }
    public void setRewardid() {
        set(REWARDID, value);
    }
    public void setMin() {
        set(MIN, value);
    }
    public void setMax() {
        set(MAX, value);
    }
    public void setChance() {
        set(CHANCE, value);
    }

}
