package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemAuctionTable.*;

@Table(
        name = "item_auction",
        fields = {
                @Field(name = AUCTION_ID),
                @Field(name = INSTANCE_ID),
                @Field(name = AUCTION_ITEM_ID),
                @Field(name = STARTING_TIME),
                @Field(name = ENDING_TIME),
                @Field(name = AUCTION_STATE_ID),
        }
)
public class ItemAuctionTable extends DataBaseTable<ItemAuctionTable> {

    public static final String AUCTION_ID = "auctionId";
    public static final String INSTANCE_ID = "instanceId";
    public static final String AUCTION_ITEM_ID = "auctionItemId";
    public static final String STARTING_TIME = "startingTime";
    public static final String ENDING_TIME = "endingTime";
    public static final String AUCTION_STATE_ID = "auctionStateId";

    public ItemAuctionTable() {
super(ItemAuctionTable.class);
}

    public Integer getAuctionId() {
        return get(AUCTION_ID, Integer.class);
    }
    public Integer getInstanceId() {
        return get(INSTANCE_ID, Integer.class);
    }
    public Integer getAuctionItemId() {
        return get(AUCTION_ITEM_ID, Integer.class);
    }
    public Long getStartingTime() {
        return get(STARTING_TIME, Long.class);
    }
    public Long getEndingTime() {
        return get(ENDING_TIME, Long.class);
    }
    public Integer getAuctionStateId() {
        return get(AUCTION_STATE_ID, Integer.class);
    }

    public void setAuctionId(Integer value) {
        set(AUCTION_ID, value);
    }
    public void setInstanceId(Integer value) {
        set(INSTANCE_ID, value);
    }
    public void setAuctionItemId(Integer value) {
        set(AUCTION_ITEM_ID, value);
    }
    public void setStartingTime(Long value) {
        set(STARTING_TIME, value);
    }
    public void setEndingTime(Long value) {
        set(ENDING_TIME, value);
    }
    public void setAuctionStateId(Integer value) {
        set(AUCTION_STATE_ID, value);
    }

}
