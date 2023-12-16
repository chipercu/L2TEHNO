package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Auction_bidTable.*;

@Table(
        name = "auction_bid",
        fields = {
                @Field(name = ID,
                @Field(name = AUCTIONID,
                @Field(name = BIDDERID,
                @Field(name = BIDDERNAME,
                @Field(name = CLAN_NAME,
                @Field(name = MAXBID,
                @Field(name = TIME_BID,
        }
)
public class Auction_bidTable extends DataBaseTable<Auction_bidTable> {

    public static final String ID = "id";
    public static final String AUCTIONID = "auctionId";
    public static final String BIDDERID = "bidderId";
    public static final String BIDDERNAME = "bidderName";
    public static final String CLAN_NAME = "clan_name";
    public static final String MAXBID = "maxBid";
    public static final String TIME_BID = "time_bid";
    public Auction_bidTable() {
super(Auction_bidTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetAuctionId() {
        return get(AUCTIONID, Integer.class);
    }
    public IntegergetBidderId() {
        return get(BIDDERID, Integer.class);
    }
    public StringgetBidderName() {
        return get(BIDDERNAME, String.class);
    }
    public StringgetClan_name() {
        return get(CLAN_NAME, String.class);
    }
    public ObjectgetMaxBid() {
        return get(MAXBID, Object.class);
    }
    public ObjectgetTime_bid() {
        return get(TIME_BID, Object.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setAuctionId() {
        set(AUCTIONID, value);
    }
    public void setBidderId() {
        set(BIDDERID, value);
    }
    public void setBidderName() {
        set(BIDDERNAME, value);
    }
    public void setClan_name() {
        set(CLAN_NAME, value);
    }
    public void setMaxBid() {
        set(MAXBID, value);
    }
    public void setTime_bid() {
        set(TIME_BID, value);
    }

}
