package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Castle_manor_productionTable.*;

@Table(
        name = "castle_manor_production",
        fields = {
                @Field(name = CASTLE_ID,
                @Field(name = SEED_ID,
                @Field(name = CAN_PRODUCE,
                @Field(name = START_PRODUCE,
                @Field(name = SEED_PRICE,
                @Field(name = PERIOD,
        }
)
public class Castle_manor_productionTable extends DataBaseTable<Castle_manor_productionTable> {

    public static final String CASTLE_ID = "castle_id";
    public static final String SEED_ID = "seed_id";
    public static final String CAN_PRODUCE = "can_produce";
    public static final String START_PRODUCE = "start_produce";
    public static final String SEED_PRICE = "seed_price";
    public static final String PERIOD = "period";
    public Castle_manor_productionTable() {
super(Castle_manor_productionTable.class);
}
    public IntegergetCastle_id() {
        return get(CASTLE_ID, Integer.class);
    }
    public IntegergetSeed_id() {
        return get(SEED_ID, Integer.class);
    }
    public ObjectgetCan_produce() {
        return get(CAN_PRODUCE, Object.class);
    }
    public ObjectgetStart_produce() {
        return get(START_PRODUCE, Object.class);
    }
    public ObjectgetSeed_price() {
        return get(SEED_PRICE, Object.class);
    }
    public IntegergetPeriod() {
        return get(PERIOD, Integer.class);
    }

    public void setCastle_id() {
        set(CASTLE_ID, value);
    }
    public void setSeed_id() {
        set(SEED_ID, value);
    }
    public void setCan_produce() {
        set(CAN_PRODUCE, value);
    }
    public void setStart_produce() {
        set(START_PRODUCE, value);
    }
    public void setSeed_price() {
        set(SEED_PRICE, value);
    }
    public void setPeriod() {
        set(PERIOD, value);
    }

}
