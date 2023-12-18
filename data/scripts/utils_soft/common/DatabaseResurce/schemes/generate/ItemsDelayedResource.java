package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemsDelayedResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "items_delayed",
        primary_key = {PAYMENT_ID},
        fields = {
                @Field(name = PAYMENT_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = OWNER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = ITEM_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = COUNT , type = INT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = ENCHANT_LEVEL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ATTRIBUTE , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = ATTRIBUTE_LEVEL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = ELEM0 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM1 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM2 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM3 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM4 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM5 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FLAGS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PAYMENT_STATUS , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DESCRIPTION , type = VARCHAR , type_size = 255 , nullable = true , defValue = @DefValue(String = "")),
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


    public ItemsDelayedResource(String payment_id){
        super(ItemsDelayedResource.class);
        getSTAT_SET().set(PAYMENT_ID, payment_id);
    }

    public Integer getPaymentId() {
        return get(PAYMENT_ID, Integer.class);
    }
    public Integer getOwnerId() {
        return get(OWNER_ID, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Integer getCount() {
        return get(COUNT, Integer.class);
    }
    public Integer getEnchantLevel() {
        return get(ENCHANT_LEVEL, Integer.class);
    }
    public Integer getAttribute() {
        return get(ATTRIBUTE, Integer.class);
    }
    public Integer getAttributeLevel() {
        return get(ATTRIBUTE_LEVEL, Integer.class);
    }
    public Integer getElem0() {
        return get(ELEM0, Integer.class);
    }
    public Integer getElem1() {
        return get(ELEM1, Integer.class);
    }
    public Integer getElem2() {
        return get(ELEM2, Integer.class);
    }
    public Integer getElem3() {
        return get(ELEM3, Integer.class);
    }
    public Integer getElem4() {
        return get(ELEM4, Integer.class);
    }
    public Integer getElem5() {
        return get(ELEM5, Integer.class);
    }
    public Integer getFlags() {
        return get(FLAGS, Integer.class);
    }
    public Integer getPaymentStatus() {
        return get(PAYMENT_STATUS, Integer.class);
    }
    public String getDescription() {
        return get(DESCRIPTION, String.class);
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
