package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CastleManorProcureResource.*;

@Table(
        name = "castle_manor_procure",
        primary_key = {CASTLE_ID,CROP_ID,PERIOD},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = CASTLE_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = CROP_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = CAN_BUY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = START_BUY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = PRICE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = REWARD_TYPE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = PERIOD , defValue = "1"),
        }
)
public class CastleManorProcureResource extends DataBaseTable<CastleManorProcureResource> {

    public static final String CASTLE_ID = "castle_id";
    public static final String CROP_ID = "crop_id";
    public static final String CAN_BUY = "can_buy";
    public static final String START_BUY = "start_buy";
    public static final String PRICE = "price";
    public static final String REWARD_TYPE = "reward_type";
    public static final String PERIOD = "period";

    public CastleManorProcureResource() {
        super(CastleManorProcureResource.class);
}

    public Integer getCastleId() {
        return get(CASTLE_ID);
    }
    public Integer getCropId() {
        return get(CROP_ID);
    }
    public Long getCanBuy() {
        return get(CAN_BUY);
    }
    public Long getStartBuy() {
        return get(START_BUY);
    }
    public Long getPrice() {
        return get(PRICE);
    }
    public Integer getRewardType() {
        return get(REWARD_TYPE);
    }
    public Integer getPeriod() {
        return get(PERIOD);
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
