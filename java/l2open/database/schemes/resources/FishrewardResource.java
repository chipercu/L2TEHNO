package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.FishrewardResource.*;

@Table(
        name = "fishreward",
        primary_key = {FISHID,REWARDID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FISHID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = REWARDID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MIN , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MAX , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHANCE , defValue = "0"),
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
