package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Item_auctionTable.*;

@Table(
        name = "item_auction",
        fields = {
                @Field(name = AUCTIONID,
                @Field(name = INSTANCEID,
                @Field(name = AUCTIONITEMID,
                @Field(name = STARTINGTIME,
                @Field(name = ENDINGTIME,
                @Field(name = AUCTIONSTATEID,
        }
)
public class Item_auctionTable extends DataBaseTable<Item_auctionTable> {

    public static final String AUCTIONID = "auctionId";
    public static final String INSTANCEID = "instanceId";
    public static final String AUCTIONITEMID = "auctionItemId";
    public static final String STARTINGTIME = "startingTime";
    public static final String ENDINGTIME = "endingTime";
    public static final String AUCTIONSTATEID = "auctionStateId";
    public Item_auctionTable() {
super(Item_auctionTable.class);
}
    public IntegergetAuctionId() {
        return get(AUCTIONID, Integer.class);
    }
    public IntegergetInstanceId() {
        return get(INSTANCEID, Integer.class);
    }
    public IntegergetAuctionItemId() {
        return get(AUCTIONITEMID, Integer.class);
    }
    public ObjectgetStartingTime() {
        return get(STARTINGTIME, Object.class);
    }
    public ObjectgetEndingTime() {
        return get(ENDINGTIME, Object.class);
    }
    public IntegergetAuctionStateId() {
        return get(AUCTIONSTATEID, Integer.class);
    }

    public void setAuctionId() {
        set(AUCTIONID, value);
    }
    public void setInstanceId() {
        set(INSTANCEID, value);
    }
    public void setAuctionItemId() {
        set(AUCTIONITEMID, value);
    }
    public void setStartingTime() {
        set(STARTINGTIME, value);
    }
    public void setEndingTime() {
        set(ENDINGTIME, value);
    }
    public void setAuctionStateId() {
        set(AUCTIONSTATEID, value);
    }

}
