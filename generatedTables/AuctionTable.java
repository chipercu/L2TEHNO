package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.AuctionTable.*;

@Table(
        name = "auction",
        fields = {
                @Field(name = ID,
                @Field(name = SELLERID,
                @Field(name = SELLERNAME,
                @Field(name = SELLERCLANNAME,
                @Field(name = ITEMNAME,
                @Field(name = STARTINGBID,
                @Field(name = CURRENTBID,
                @Field(name = ENDDATE,
        }
)
public class AuctionTable extends DataBaseTable<AuctionTable> {

    public static final String ID = "id";
    public static final String SELLERID = "sellerId";
    public static final String SELLERNAME = "sellerName";
    public static final String SELLERCLANNAME = "sellerClanName";
    public static final String ITEMNAME = "itemName";
    public static final String STARTINGBID = "startingBid";
    public static final String CURRENTBID = "currentBid";
    public static final String ENDDATE = "endDate";
    public AuctionTable() {
super(AuctionTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetSellerId() {
        return get(SELLERID, Integer.class);
    }
    public StringgetSellerName() {
        return get(SELLERNAME, String.class);
    }
    public StringgetSellerClanName() {
        return get(SELLERCLANNAME, String.class);
    }
    public StringgetItemName() {
        return get(ITEMNAME, String.class);
    }
    public ObjectgetStartingBid() {
        return get(STARTINGBID, Object.class);
    }
    public ObjectgetCurrentBid() {
        return get(CURRENTBID, Object.class);
    }
    public DoublegetEndDate() {
        return get(ENDDATE, Double.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setSellerId() {
        set(SELLERID, value);
    }
    public void setSellerName() {
        set(SELLERNAME, value);
    }
    public void setSellerClanName() {
        set(SELLERCLANNAME, value);
    }
    public void setItemName() {
        set(ITEMNAME, value);
    }
    public void setStartingBid() {
        set(STARTINGBID, value);
    }
    public void setCurrentBid() {
        set(CURRENTBID, value);
    }
    public void setEndDate() {
        set(ENDDATE, value);
    }

}
