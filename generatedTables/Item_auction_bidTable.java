package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Item_auction_bidTable.*;

@Table(
        name = "item_auction_bid",
        fields = {
                @Field(name = AUCTIONID,
                @Field(name = PLAYEROBJID,
                @Field(name = PLAYERBID,
        }
)
public class Item_auction_bidTable extends DataBaseTable<Item_auction_bidTable> {

    public static final String AUCTIONID = "auctionId";
    public static final String PLAYEROBJID = "playerObjId";
    public static final String PLAYERBID = "playerBid";
    public Item_auction_bidTable() {
super(Item_auction_bidTable.class);
}
    public IntegergetAuctionId() {
        return get(AUCTIONID, Integer.class);
    }
    public IntegergetPlayerObjId() {
        return get(PLAYEROBJID, Integer.class);
    }
    public ObjectgetPlayerBid() {
        return get(PLAYERBID, Object.class);
    }

    public void setAuctionId() {
        set(AUCTIONID, value);
    }
    public void setPlayerObjId() {
        set(PLAYEROBJID, value);
    }
    public void setPlayerBid() {
        set(PLAYERBID, value);
    }

}
