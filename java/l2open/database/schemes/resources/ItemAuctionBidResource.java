package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ItemAuctionBidResource.*;

@Table(
        name = "item_auction_bid",
        primary_key = {AUCTION_ID,PLAYER_OBJ_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = AUCTION_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = PLAYER_OBJ_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = PLAYER_BID , defValue = "null"),
        }
)
public class ItemAuctionBidResource extends DataBaseTable<ItemAuctionBidResource> {

    public static final String AUCTION_ID = "auctionId";
    public static final String PLAYER_OBJ_ID = "playerObjId";
    public static final String PLAYER_BID = "playerBid";

    public ItemAuctionBidResource() {
        super(ItemAuctionBidResource.class);
}

    public Integer getAuctionId() {
        return get(AUCTION_ID);
    }
    public Integer getPlayerObjId() {
        return get(PLAYER_OBJ_ID);
    }
    public Long getPlayerBid() {
        return get(PLAYER_BID);
    }

    public void setAuctionId(Integer value) {
        set(AUCTION_ID, value);
    }
    public void setPlayerObjId(Integer value) {
        set(PLAYER_OBJ_ID, value);
    }
    public void setPlayerBid(Long value) {
        set(PLAYER_BID, value);
    }

}
