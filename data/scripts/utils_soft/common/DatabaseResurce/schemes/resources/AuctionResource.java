package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.AuctionResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "auction",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SELLER_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = SELLER_NAME , defValue = "NPC"),
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = SELLER_CLAN_NAME , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 40 , name = ITEM_NAME , defValue = ""),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = STARTING_BID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = CURRENT_BID , defValue = "0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = END_DATE , defValue = "0"),
        }
)
public class AuctionResource extends DataBaseTable<AuctionResource> {

    public static final String ID = "id";
    public static final String SELLER_ID = "sellerId";
    public static final String SELLER_NAME = "sellerName";
    public static final String SELLER_CLAN_NAME = "sellerClanName";
    public static final String ITEM_NAME = "itemName";
    public static final String STARTING_BID = "startingBid";
    public static final String CURRENT_BID = "currentBid";
    public static final String END_DATE = "endDate";

    public AuctionResource() {
        super(AuctionResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getSellerId() {
        return get(SELLER_ID);
    }
    public String getSellerName() {
        return get(SELLER_NAME);
    }
    public String getSellerClanName() {
        return get(SELLER_CLAN_NAME);
    }
    public String getItemName() {
        return get(ITEM_NAME);
    }
    public Long getStartingBid() {
        return get(STARTING_BID);
    }
    public Long getCurrentBid() {
        return get(CURRENT_BID);
    }
    public Double getEndDate() {
        return get(END_DATE);
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
