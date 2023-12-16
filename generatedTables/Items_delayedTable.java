package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Items_delayedTable.*;

@Table(
        name = "items_delayed",
        fields = {
                @Field(name = PAYMENT_ID,
                @Field(name = OWNER_ID,
                @Field(name = ITEM_ID,
                @Field(name = COUNT,
                @Field(name = ENCHANT_LEVEL,
                @Field(name = ATTRIBUTE,
                @Field(name = ATTRIBUTE_LEVEL,
                @Field(name = ELEM0,
                @Field(name = ELEM1,
                @Field(name = ELEM2,
                @Field(name = ELEM3,
                @Field(name = ELEM4,
                @Field(name = ELEM5,
                @Field(name = FLAGS,
                @Field(name = PAYMENT_STATUS,
                @Field(name = DESCRIPTION,
        }
)
public class Items_delayedTable extends DataBaseTable<Items_delayedTable> {

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
    public Items_delayedTable() {
super(Items_delayedTable.class);
}
    public IntegergetPayment_id() {
        return get(PAYMENT_ID, Integer.class);
    }
    public IntegergetOwner_id() {
        return get(OWNER_ID, Integer.class);
    }
    public IntegergetItem_id() {
        return get(ITEM_ID, Integer.class);
    }
    public IntegergetCount() {
        return get(COUNT, Integer.class);
    }
    public IntegergetEnchant_level() {
        return get(ENCHANT_LEVEL, Integer.class);
    }
    public IntegergetAttribute() {
        return get(ATTRIBUTE, Integer.class);
    }
    public IntegergetAttribute_level() {
        return get(ATTRIBUTE_LEVEL, Integer.class);
    }
    public IntegergetElem0() {
        return get(ELEM0, Integer.class);
    }
    public IntegergetElem1() {
        return get(ELEM1, Integer.class);
    }
    public IntegergetElem2() {
        return get(ELEM2, Integer.class);
    }
    public IntegergetElem3() {
        return get(ELEM3, Integer.class);
    }
    public IntegergetElem4() {
        return get(ELEM4, Integer.class);
    }
    public IntegergetElem5() {
        return get(ELEM5, Integer.class);
    }
    public IntegergetFlags() {
        return get(FLAGS, Integer.class);
    }
    public IntegergetPayment_status() {
        return get(PAYMENT_STATUS, Integer.class);
    }
    public StringgetDescription() {
        return get(DESCRIPTION, String.class);
    }

    public void setPayment_id() {
        set(PAYMENT_ID, value);
    }
    public void setOwner_id() {
        set(OWNER_ID, value);
    }
    public void setItem_id() {
        set(ITEM_ID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setEnchant_level() {
        set(ENCHANT_LEVEL, value);
    }
    public void setAttribute() {
        set(ATTRIBUTE, value);
    }
    public void setAttribute_level() {
        set(ATTRIBUTE_LEVEL, value);
    }
    public void setElem0() {
        set(ELEM0, value);
    }
    public void setElem1() {
        set(ELEM1, value);
    }
    public void setElem2() {
        set(ELEM2, value);
    }
    public void setElem3() {
        set(ELEM3, value);
    }
    public void setElem4() {
        set(ELEM4, value);
    }
    public void setElem5() {
        set(ELEM5, value);
    }
    public void setFlags() {
        set(FLAGS, value);
    }
    public void setPayment_status() {
        set(PAYMENT_STATUS, value);
    }
    public void setDescription() {
        set(DESCRIPTION, value);
    }

}
