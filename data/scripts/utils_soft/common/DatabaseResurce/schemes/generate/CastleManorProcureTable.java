package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CastleManorProcureTable.*;

@Table(
        name = "castle_manor_procure",
        fields = {
                @Field(name = CASTLE_ID),
                @Field(name = CROP_ID),
                @Field(name = CAN_BUY),
                @Field(name = START_BUY),
                @Field(name = PRICE),
                @Field(name = REWARD_TYPE),
                @Field(name = PERIOD),
        }
)
public class CastleManorProcureTable extends DataBaseTable<CastleManorProcureTable> {

    public static final String CASTLE_ID = "castle_id";
    public static final String CROP_ID = "crop_id";
    public static final String CAN_BUY = "can_buy";
    public static final String START_BUY = "start_buy";
    public static final String PRICE = "price";
    public static final String REWARD_TYPE = "reward_type";
    public static final String PERIOD = "period";

    public CastleManorProcureTable() {
super(CastleManorProcureTable.class);
}

    public Integer getCastleId() {
        return get(CASTLE_ID, Integer.class);
    }
    public Integer getCropId() {
        return get(CROP_ID, Integer.class);
    }
    public Long getCanBuy() {
        return get(CAN_BUY, Long.class);
    }
    public Long getStartBuy() {
        return get(START_BUY, Long.class);
    }
    public Long getPrice() {
        return get(PRICE, Long.class);
    }
    public Integer getRewardType() {
        return get(REWARD_TYPE, Integer.class);
    }
    public Integer getPeriod() {
        return get(PERIOD, Integer.class);
    }

    public void setCastleId(Integer value) {
        set(CASTLE_ID, value);
    }
    public void setCropId(Integer value) {
        set(CROP_ID, value);
    }
    public void setCanBuy(Long value) {
        set(CAN_BUY, value);
    }
    public void setStartBuy(Long value) {
        set(START_BUY, value);
    }
    public void setPrice(Long value) {
        set(PRICE, value);
    }
    public void setRewardType(Integer value) {
        set(REWARD_TYPE, value);
    }
    public void setPeriod(Integer value) {
        set(PERIOD, value);
    }

}
