package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.AuctionBidResource.*;

@Table(
        name = "auction_bid",
        primary_key = {AUCTION_ID,BIDDER_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = AUCTION_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = BIDDER_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = BIDDER_NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = CLAN_NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = MAX_BID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = TIME_BID , defValue = "0"),
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

    public Integer getId() {
        return get(ID);
    }
    public Integer getAuctionId() {
        return get(AUCTION_ID);
    }
    public Integer getBidderId() {
        return get(BIDDER_ID);
    }
    public String getBidderName() {
        return get(BIDDER_NAME);
    }
    public String getClanName() {
        return get(CLAN_NAME);
    }
    public Long getMaxBid() {
        return get(MAX_BID);
    }
    public Long getTimeBid() {
        return get(TIME_BID);
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
