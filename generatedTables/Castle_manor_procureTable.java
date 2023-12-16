package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Castle_manor_procureTable.*;

@Table(
        name = "castle_manor_procure",
        fields = {
                @Field(name = CASTLE_ID,
                @Field(name = CROP_ID,
                @Field(name = CAN_BUY,
                @Field(name = START_BUY,
                @Field(name = PRICE,
                @Field(name = REWARD_TYPE,
                @Field(name = PERIOD,
        }
)
public class Castle_manor_procureTable extends DataBaseTable<Castle_manor_procureTable> {

    public static final String CASTLE_ID = "castle_id";
    public static final String CROP_ID = "crop_id";
    public static final String CAN_BUY = "can_buy";
    public static final String START_BUY = "start_buy";
    public static final String PRICE = "price";
    public static final String REWARD_TYPE = "reward_type";
    public static final String PERIOD = "period";
    public Castle_manor_procureTable() {
super(Castle_manor_procureTable.class);
}
    public IntegergetCastle_id() {
        return get(CASTLE_ID, Integer.class);
    }
    public IntegergetCrop_id() {
        return get(CROP_ID, Integer.class);
    }
    public ObjectgetCan_buy() {
        return get(CAN_BUY, Object.class);
    }
    public ObjectgetStart_buy() {
        return get(START_BUY, Object.class);
    }
    public ObjectgetPrice() {
        return get(PRICE, Object.class);
    }
    public IntegergetReward_type() {
        return get(REWARD_TYPE, Integer.class);
    }
    public IntegergetPeriod() {
        return get(PERIOD, Integer.class);
    }

    public void setCastle_id() {
        set(CASTLE_ID, value);
    }
    public void setCrop_id() {
        set(CROP_ID, value);
    }
    public void setCan_buy() {
        set(CAN_BUY, value);
    }
    public void setStart_buy() {
        set(START_BUY, value);
    }
    public void setPrice() {
        set(PRICE, value);
    }
    public void setReward_type() {
        set(REWARD_TYPE, value);
    }
    public void setPeriod() {
        set(PERIOD, value);
    }

}
