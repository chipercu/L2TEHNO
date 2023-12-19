package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CastleManorProductionResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "castle_manor_production",
        primary_key = {CASTLE_ID,SEED_ID,PERIOD},
        fields = {
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CASTLE_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = SEED_ID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = CAN_PRODUCE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = START_PRODUCE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = SEED_PRICE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PERIOD , defValue = "1"),
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
        return get(CASTLE_ID);
    }
    public Integer getSeedId() {
        return get(SEED_ID);
    }
    public Long getCanProduce() {
        return get(CAN_PRODUCE);
    }
    public Long getStartProduce() {
        return get(START_PRODUCE);
    }
    public Long getSeedPrice() {
        return get(SEED_PRICE);
    }
    public Integer getPeriod() {
        return get(PERIOD);
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
