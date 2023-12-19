package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ItemMallResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "item_mall",
        primary_key = {ORD,ITEM_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ORD , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = NAME , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = COUNT , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PRICE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_CATEGORY2 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ON_SALE , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_START_SALE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_END_SALE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_START_HOUR , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_START_MIN , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_END_HOUR , defValue = "23"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_END_MIN , defValue = "59"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_STOCK , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I_MAX_STOCK , defValue = "0"),
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
        return get(ORD);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Integer getCount() {
        return get(COUNT);
    }
    public Integer getPrice() {
        return get(PRICE);
    }
    public Integer getICategory2() {
        return get(I_CATEGORY2);
    }
    public Integer getOnSale() {
        return get(ON_SALE);
    }
    public Integer getIStartSale() {
        return get(I_START_SALE);
    }
    public Integer getIEndSale() {
        return get(I_END_SALE);
    }
    public Integer getIStartHour() {
        return get(I_START_HOUR);
    }
    public Integer getIStartMin() {
        return get(I_START_MIN);
    }
    public Integer getIEndHour() {
        return get(I_END_HOUR);
    }
    public Integer getIEndMin() {
        return get(I_END_MIN);
    }
    public Integer getIStock() {
        return get(I_STOCK);
    }
    public Integer getIMaxStock() {
        return get(I_MAX_STOCK);
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
