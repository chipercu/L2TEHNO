package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.FishrewardResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "fishreward",
        primary_key = {FISHID,REWARDID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = FISHID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = REWARDID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MIN , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MAX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHANCE , defValue = "0"),
        }
)
public class FishrewardResource extends DataBaseTable<FishrewardResource> {

    public static final String FISHID = "fishid";
    public static final String REWARDID = "rewardid";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String CHANCE = "chance";

    public FishrewardResource() {
        super(FishrewardResource.class);
}

    public Integer getFishid() {
        return get(FISHID);
    }
    public Integer getRewardid() {
        return get(REWARDID);
    }
    public Integer getMin() {
        return get(MIN);
    }
    public Integer getMax() {
        return get(MAX);
    }
    public Integer getChance() {
        return get(CHANCE);
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
