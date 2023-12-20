package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.ItemAuctionResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "item_auction",
        primary_key = {AUCTION_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = AUCTION_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = INSTANCE_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AUCTION_ITEM_ID , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = STARTING_TIME , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ENDING_TIME , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = AUCTION_STATE_ID , defValue = "null"),
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

    public Integer getAuctionId() {
        return get(AUCTION_ID);
    }
    public Integer getInstanceId() {
        return get(INSTANCE_ID);
    }
    public Integer getAuctionItemId() {
        return get(AUCTION_ITEM_ID);
    }
    public Long getStartingTime() {
        return get(STARTING_TIME);
    }
    public Long getEndingTime() {
        return get(ENDING_TIME);
    }
    public Integer getAuctionStateId() {
        return get(AUCTION_STATE_ID);
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
