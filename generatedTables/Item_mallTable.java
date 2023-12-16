package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Item_mallTable.*;

@Table(
        name = "item_mall",
        fields = {
                @Field(name = ORD,
                @Field(name = NAME,
                @Field(name = ITEMID,
                @Field(name = COUNT,
                @Field(name = PRICE,
                @Field(name = ICATEGORY2,
                @Field(name = ONSALE,
                @Field(name = ISTARTSALE,
                @Field(name = IENDSALE,
                @Field(name = ISTARTHOUR,
                @Field(name = ISTARTMIN,
                @Field(name = IENDHOUR,
                @Field(name = IENDMIN,
                @Field(name = ISTOCK,
                @Field(name = IMAXSTOCK,
        }
)
public class Item_mallTable extends DataBaseTable<Item_mallTable> {

    public static final String ORD = "ord";
    public static final String NAME = "name";
    public static final String ITEMID = "itemId";
    public static final String COUNT = "count";
    public static final String PRICE = "price";
    public static final String ICATEGORY2 = "iCategory2";
    public static final String ONSALE = "onSale";
    public static final String ISTARTSALE = "iStartSale";
    public static final String IENDSALE = "iEndSale";
    public static final String ISTARTHOUR = "iStartHour";
    public static final String ISTARTMIN = "iStartMin";
    public static final String IENDHOUR = "iEndHour";
    public static final String IENDMIN = "iEndMin";
    public static final String ISTOCK = "iStock";
    public static final String IMAXSTOCK = "iMaxStock";
    public Item_mallTable() {
super(Item_mallTable.class);
}
    public IntegergetOrd() {
        return get(ORD, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetItemId() {
        return get(ITEMID, Integer.class);
    }
    public IntegergetCount() {
        return get(COUNT, Integer.class);
    }
    public IntegergetPrice() {
        return get(PRICE, Integer.class);
    }
    public IntegergetICategory2() {
        return get(ICATEGORY2, Integer.class);
    }
    public IntegergetOnSale() {
        return get(ONSALE, Integer.class);
    }
    public IntegergetIStartSale() {
        return get(ISTARTSALE, Integer.class);
    }
    public IntegergetIEndSale() {
        return get(IENDSALE, Integer.class);
    }
    public IntegergetIStartHour() {
        return get(ISTARTHOUR, Integer.class);
    }
    public IntegergetIStartMin() {
        return get(ISTARTMIN, Integer.class);
    }
    public IntegergetIEndHour() {
        return get(IENDHOUR, Integer.class);
    }
    public IntegergetIEndMin() {
        return get(IENDMIN, Integer.class);
    }
    public IntegergetIStock() {
        return get(ISTOCK, Integer.class);
    }
    public IntegergetIMaxStock() {
        return get(IMAXSTOCK, Integer.class);
    }

    public void setOrd() {
        set(ORD, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setItemId() {
        set(ITEMID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setPrice() {
        set(PRICE, value);
    }
    public void setICategory2() {
        set(ICATEGORY2, value);
    }
    public void setOnSale() {
        set(ONSALE, value);
    }
    public void setIStartSale() {
        set(ISTARTSALE, value);
    }
    public void setIEndSale() {
        set(IENDSALE, value);
    }
    public void setIStartHour() {
        set(ISTARTHOUR, value);
    }
    public void setIStartMin() {
        set(ISTARTMIN, value);
    }
    public void setIEndHour() {
        set(IENDHOUR, value);
    }
    public void setIEndMin() {
        set(IENDMIN, value);
    }
    public void setIStock() {
        set(ISTOCK, value);
    }
    public void setIMaxStock() {
        set(IMAXSTOCK, value);
    }

}
