package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemMallResource.*;

@Table(
        name = "item_mall",
        primary_key = {ORD,ITEM_ID},
        fields = {
                @Field(name = ORD , data_type = "int"),
                @Field(name = NAME , data_type = "varchar"),
                @Field(name = ITEM_ID , data_type = "int"),
                @Field(name = COUNT , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = PRICE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = I_CATEGORY2 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ON_SALE , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = I_START_SALE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = I_END_SALE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = I_START_HOUR , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = I_START_MIN , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = I_END_HOUR , data_type = "int" , defValue = @DefValue(Integer = 23)),
                @Field(name = I_END_MIN , data_type = "int" , defValue = @DefValue(Integer = 59)),
                @Field(name = I_STOCK , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = I_MAX_STOCK , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class ItemMallResource extends DataBaseTable<ItemMallResource> {

    public static final String ORD = "ord";
    public static final String NAME = "name";
    public static final String ITEM_ID = "itemId";
    public static final String COUNT = "count";
    public static final String PRICE = "price";
    public static final String I_CATEGORY2 = "iCategory2";
    public static final String ON_SALE = "onSale";
    public static final String I_START_SALE = "iStartSale";
    public static final String I_END_SALE = "iEndSale";
    public static final String I_START_HOUR = "iStartHour";
    public static final String I_START_MIN = "iStartMin";
    public static final String I_END_HOUR = "iEndHour";
    public static final String I_END_MIN = "iEndMin";
    public static final String I_STOCK = "iStock";
    public static final String I_MAX_STOCK = "iMaxStock";

    public ItemMallResource() {
super(ItemMallResource.class);
}

    public Integer getOrd() {
        return get(ORD, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Integer getCount() {
        return get(COUNT, Integer.class);
    }
    public Integer getPrice() {
        return get(PRICE, Integer.class);
    }
    public Integer getICategory2() {
        return get(I_CATEGORY2, Integer.class);
    }
    public Integer getOnSale() {
        return get(ON_SALE, Integer.class);
    }
    public Integer getIStartSale() {
        return get(I_START_SALE, Integer.class);
    }
    public Integer getIEndSale() {
        return get(I_END_SALE, Integer.class);
    }
    public Integer getIStartHour() {
        return get(I_START_HOUR, Integer.class);
    }
    public Integer getIStartMin() {
        return get(I_START_MIN, Integer.class);
    }
    public Integer getIEndHour() {
        return get(I_END_HOUR, Integer.class);
    }
    public Integer getIEndMin() {
        return get(I_END_MIN, Integer.class);
    }
    public Integer getIStock() {
        return get(I_STOCK, Integer.class);
    }
    public Integer getIMaxStock() {
        return get(I_MAX_STOCK, Integer.class);
    }

    public void setOrd(Integer value) {
        set(ORD, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setCount(Integer value) {
        set(COUNT, value);
    }
    public void setPrice(Integer value) {
        set(PRICE, value);
    }
    public void setICategory2(Integer value) {
        set(I_CATEGORY2, value);
    }
    public void setOnSale(Integer value) {
        set(ON_SALE, value);
    }
    public void setIStartSale(Integer value) {
        set(I_START_SALE, value);
    }
    public void setIEndSale(Integer value) {
        set(I_END_SALE, value);
    }
    public void setIStartHour(Integer value) {
        set(I_START_HOUR, value);
    }
    public void setIStartMin(Integer value) {
        set(I_START_MIN, value);
    }
    public void setIEndHour(Integer value) {
        set(I_END_HOUR, value);
    }
    public void setIEndMin(Integer value) {
        set(I_END_MIN, value);
    }
    public void setIStock(Integer value) {
        set(I_STOCK, value);
    }
    public void setIMaxStock(Integer value) {
        set(I_MAX_STOCK, value);
    }

}
