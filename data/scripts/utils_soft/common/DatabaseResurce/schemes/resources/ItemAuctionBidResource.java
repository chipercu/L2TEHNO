package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ItemAuctionBidResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "item_auction_bid",
        primary_key = {AUCTION_ID,PLAYER_OBJ_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = AUCTION_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PLAYER_OBJ_ID , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PLAYER_BID , defValue = "null"),
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
