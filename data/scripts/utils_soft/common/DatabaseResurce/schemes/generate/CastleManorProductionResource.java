package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CastleManorProductionResource.*;

@Table(
        name = "castle_manor_production",
        primary_key = {CASTLE_ID,SEED_ID,PERIOD},
        fields = {
                @Field(name = CASTLE_ID , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = SEED_ID , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = CAN_PRODUCE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = START_PRODUCE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = SEED_PRICE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = PERIOD , data_type = "int" , defValue = @DefValue(Integer = 1)),
        }
)
public class CastleManorProductionResource extends DataBaseTable<CastleManorProductionResource> {

    public static final String CASTLE_ID = "castle_id";
    public static final String SEED_ID = "seed_id";
    public static final String CAN_PRODUCE = "can_produce";
    public static final String START_PRODUCE = "start_produce";
    public static final String SEED_PRICE = "seed_price";
    public static final String PERIOD = "period";

    public CastleManorProductionResource() {
super(CastleManorProductionResource.class);
}

    public Integer getCastleId() {
        return get(CASTLE_ID, Integer.class);
    }
    public Integer getSeedId() {
        return get(SEED_ID, Integer.class);
    }
    public Long getCanProduce() {
        return get(CAN_PRODUCE, Long.class);
    }
    public Long getStartProduce() {
        return get(START_PRODUCE, Long.class);
    }
    public Long getSeedPrice() {
        return get(SEED_PRICE, Long.class);
    }
    public Integer getPeriod() {
        return get(PERIOD, Integer.class);
    }

    public void setCastleId(Integer value) {
        set(CASTLE_ID, value);
    }
    public void setSeedId(Integer value) {
        set(SEED_ID, value);
    }
    public void setCanProduce(Long value) {
        set(CAN_PRODUCE, value);
    }
    public void setStartProduce(Long value) {
        set(START_PRODUCE, value);
    }
    public void setSeedPrice(Long value) {
        set(SEED_PRICE, value);
    }
    public void setPeriod(Integer value) {
        set(PERIOD, value);
    }

}
