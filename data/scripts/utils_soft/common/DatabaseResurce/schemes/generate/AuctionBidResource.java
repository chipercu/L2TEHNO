package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AuctionBidResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "auction_bid",
        primary_key = {AUCTION_ID,BIDDER_ID},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = AUCTION_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BIDDER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BIDDER_NAME , type = VARCHAR , type_size = 50 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = CLAN_NAME , type = VARCHAR , type_size = 50 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = MAX_BID , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = TIME_BID , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
        }
)
public class AuctionBidResource extends DataBaseTable<AuctionBidResource> {

    public static final String ID = "id";
    public static final String AUCTION_ID = "auctionId";
    public static final String BIDDER_ID = "bidderId";
    public static final String BIDDER_NAME = "bidderName";
    public static final String CLAN_NAME = "clan_name";
    public static final String MAX_BID = "maxBid";
    public static final String TIME_BID = "time_bid";

    public AuctionBidResource() {
super(AuctionBidResource.class);
}


    public AuctionBidResource(String auctionId,String bidderId){
        super(AuctionBidResource.class);
        getSTAT_SET().set(AUCTION_ID, auctionId);
        getSTAT_SET().set(BIDDER_ID, bidderId);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getAuctionId() {
        return get(AUCTION_ID, Integer.class);
    }
    public Integer getBidderId() {
        return get(BIDDER_ID, Integer.class);
    }
    public String getBidderName() {
        return get(BIDDER_NAME, String.class);
    }
    public String getClanName() {
        return get(CLAN_NAME, String.class);
    }
    public Long getMaxBid() {
        return get(MAX_BID, Long.class);
    }
    public Long getTimeBid() {
        return get(TIME_BID, Long.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setAuctionId(Integer value) {
        set(AUCTION_ID, value);
    }
    public void setBidderId(Integer value) {
        set(BIDDER_ID, value);
    }
    public void setBidderName(String value) {
        set(BIDDER_NAME, value);
    }
    public void setClanName(String value) {
        set(CLAN_NAME, value);
    }
    public void setMaxBid(Long value) {
        set(MAX_BID, value);
    }
    public void setTimeBid(Long value) {
        set(TIME_BID, value);
    }

}
