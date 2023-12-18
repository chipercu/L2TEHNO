package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemAuctionBidResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "item_auction_bid",
        primary_key = {AUCTION_ID,PLAYER_OBJ_ID},
        fields = {
                @Field(name = AUCTION_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = PLAYER_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = PLAYER_BID , type = BIGINT , nullable = false , defValue = @DefValue(Long = null)),
        }
)
public class ItemAuctionBidResource extends DataBaseTable<ItemAuctionBidResource> {

    public static final String AUCTION_ID = "auctionId";
    public static final String PLAYER_OBJ_ID = "playerObjId";
    public static final String PLAYER_BID = "playerBid";

    public ItemAuctionBidResource() {
super(ItemAuctionBidResource.class);
}


    public ItemAuctionBidResource(String auctionId,String playerObjId){
        super(ItemAuctionBidResource.class);
        getSTAT_SET().set(AUCTION_ID, auctionId);
        getSTAT_SET().set(PLAYER_OBJ_ID, playerObjId);
    }

    public Integer getAuctionId() {
        return get(AUCTION_ID, Integer.class);
    }
    public Integer getPlayerObjId() {
        return get(PLAYER_OBJ_ID, Integer.class);
    }
    public Long getPlayerBid() {
        return get(PLAYER_BID, Long.class);
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
