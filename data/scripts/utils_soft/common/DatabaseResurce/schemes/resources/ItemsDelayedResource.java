package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ItemsDelayedResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "items_delayed",
        primary_key = {PAYMENT_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = PAYMENT_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OWNER_ID , defValue = "null"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = COUNT , defValue = "1"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ENCHANT_LEVEL , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ATTRIBUTE , defValue = "-1"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ATTRIBUTE_LEVEL , defValue = "-1"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ELEM0 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ELEM1 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ELEM2 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ELEM3 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ELEM4 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ELEM5 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FLAGS , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PAYMENT_STATUS , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = DESCRIPTION , defValue = "NULL"),
        }
)
public class ItemsDelayedResource extends DataBaseTable<ItemsDelayedResource> {

    public static final String PAYMENT_ID = "payment_id";
    public static final String OWNER_ID = "owner_id";
    public static final String ITEM_ID = "item_id";
    public static final String COUNT = "count";
    public static final String ENCHANT_LEVEL = "enchant_level";
    public static final String ATTRIBUTE = "attribute";
    public static final String ATTRIBUTE_LEVEL = "attribute_level";
    public static final String ELEM0 = "elem0";
    public static final String ELEM1 = "elem1";
    public static final String ELEM2 = "elem2";
    public static final String ELEM3 = "elem3";
    public static final String ELEM4 = "elem4";
    public static final String ELEM5 = "elem5";
    public static final String FLAGS = "flags";
    public static final String PAYMENT_STATUS = "payment_status";
    public static final String DESCRIPTION = "description";

    public ItemsDelayedResource() {
        super(ItemsDelayedResource.class);
}

    public Integer getPaymentId() {
        return get(PAYMENT_ID);
    }
    public Integer getOwnerId() {
        return get(OWNER_ID);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Integer getCount() {
        return get(COUNT);
    }
    public Integer getEnchantLevel() {
        return get(ENCHANT_LEVEL);
    }
    public Integer getAttribute() {
        return get(ATTRIBUTE);
    }
    public Integer getAttributeLevel() {
        return get(ATTRIBUTE_LEVEL);
    }
    public Integer getElem0() {
        return get(ELEM0);
    }
    public Integer getElem1() {
        return get(ELEM1);
    }
    public Integer getElem2() {
        return get(ELEM2);
    }
    public Integer getElem3() {
        return get(ELEM3);
    }
    public Integer getElem4() {
        return get(ELEM4);
    }
    public Integer getElem5() {
        return get(ELEM5);
    }
    public Integer getFlags() {
        return get(FLAGS);
    }
    public Integer getPaymentStatus() {
        return get(PAYMENT_STATUS);
    }
    public String getDescription() {
        return get(DESCRIPTION);
    }

    public void setPaymentId(Integer value) {
        set(PAYMENT_ID, value);
    }
    public void setOwnerId(Integer value) {
        set(OWNER_ID, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setCount(Integer value) {
        set(COUNT, value);
    }
    public void setEnchantLevel(Integer value) {
        set(ENCHANT_LEVEL, value);
    }
    public void setAttribute(Integer value) {
        set(ATTRIBUTE, value);
    }
    public void setAttributeLevel(Integer value) {
        set(ATTRIBUTE_LEVEL, value);
    }
    public void setElem0(Integer value) {
        set(ELEM0, value);
    }
    public void setElem1(Integer value) {
        set(ELEM1, value);
    }
    public void setElem2(Integer value) {
        set(ELEM2, value);
    }
    public void setElem3(Integer value) {
        set(ELEM3, value);
    }
    public void setElem4(Integer value) {
        set(ELEM4, value);
    }
    public void setElem5(Integer value) {
        set(ELEM5, value);
    }
    public void setFlags(Integer value) {
        set(FLAGS, value);
    }
    public void setPaymentStatus(Integer value) {
        set(PAYMENT_STATUS, value);
    }
    public void setDescription(String value) {
        set(DESCRIPTION, value);
    }

}
