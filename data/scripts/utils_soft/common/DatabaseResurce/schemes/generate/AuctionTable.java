package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AuctionTable.*;

@Table(
        name = "auction",
        fields = {
                @Field(name = ID),
                @Field(name = SELLER_ID),
                @Field(name = SELLER_NAME),
                @Field(name = SELLER_CLAN_NAME),
                @Field(name = ITEM_NAME),
                @Field(name = STARTING_BID),
                @Field(name = CURRENT_BID),
                @Field(name = END_DATE),
        }
)
public class AuctionTable extends DataBaseTable<AuctionTable> {

    public static final String ID = "id";
    public static final String SELLER_ID = "sellerId";
    public static final String SELLER_NAME = "sellerName";
    public static final String SELLER_CLAN_NAME = "sellerClanName";
    public static final String ITEM_NAME = "itemName";
    public static final String STARTING_BID = "startingBid";
    public static final String CURRENT_BID = "currentBid";
    public static final String END_DATE = "endDate";

    public AuctionTable() {
super(AuctionTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getSellerId() {
        return get(SELLER_ID, Integer.class);
    }
    public String getSellerName() {
        return get(SELLER_NAME, String.class);
    }
    public String getSellerClanName() {
        return get(SELLER_CLAN_NAME, String.class);
    }
    public String getItemName() {
        return get(ITEM_NAME, String.class);
    }
    public Long getStartingBid() {
        return get(STARTING_BID, Long.class);
    }
    public Long getCurrentBid() {
        return get(CURRENT_BID, Long.class);
    }
    public Double getEndDate() {
        return get(END_DATE, Double.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setSellerId(Integer value) {
        set(SELLER_ID, value);
    }
    public void setSellerName(String value) {
        set(SELLER_NAME, value);
    }
    public void setSellerClanName(String value) {
        set(SELLER_CLAN_NAME, value);
    }
    public void setItemName(String value) {
        set(ITEM_NAME, value);
    }
    public void setStartingBid(Long value) {
        set(STARTING_BID, value);
    }
    public void setCurrentBid(Long value) {
        set(CURRENT_BID, value);
    }
    public void setEndDate(Double value) {
        set(END_DATE, value);
    }

}
