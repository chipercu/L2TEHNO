package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemAuctionResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "item_auction",
        primary_key = {AUCTION_ID},
        fields = {
                @Field(name = AUCTION_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = INSTANCE_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = AUCTION_ITEM_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = STARTING_TIME , type = BIGINT , nullable = false , defValue = @DefValue(Long = null)),
                @Field(name = ENDING_TIME , type = BIGINT , nullable = false , defValue = @DefValue(Long = null)),
                @Field(name = AUCTION_STATE_ID , type = TINYINT , nullable = false , defValue = @DefValue(Integer = null)),
        }
)
public class ItemAuctionResource extends DataBaseTable<ItemAuctionResource> {

    public static final String AUCTION_ID = "auctionId";
    public static final String INSTANCE_ID = "instanceId";
    public static final String AUCTION_ITEM_ID = "auctionItemId";
    public static final String STARTING_TIME = "startingTime";
    public static final String ENDING_TIME = "endingTime";
    public static final String AUCTION_STATE_ID = "auctionStateId";

    public ItemAuctionResource() {
super(ItemAuctionResource.class);
}


    public ItemAuctionResource(String auctionId){
        super(ItemAuctionResource.class);
        getSTAT_SET().set(AUCTION_ID, auctionId);
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
