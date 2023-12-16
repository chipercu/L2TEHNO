package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Item_attributesTable.*;

@Table(
        name = "item_attributes",
        fields = {
                @Field(name = ITEMID,
                @Field(name = AUGATTRIBUTES,
                @Field(name = AUGSKILLID,
                @Field(name = AUGSKILLLEVEL,
                @Field(name = ELEMTYPE,
                @Field(name = ELEMVALUE,
                @Field(name = ELEM0,
                @Field(name = ELEM1,
                @Field(name = ELEM2,
                @Field(name = ELEM3,
                @Field(name = ELEM4,
                @Field(name = ELEM5,
        }
)
public class Item_attributesTable extends DataBaseTable<Item_attributesTable> {

    public static final String ITEMID = "itemId";
    public static final String AUGATTRIBUTES = "augAttributes";
    public static final String AUGSKILLID = "augSkillId";
    public static final String AUGSKILLLEVEL = "augSkillLevel";
    public static final String ELEMTYPE = "elemType";
    public static final String ELEMVALUE = "elemValue";
    public static final String ELEM0 = "elem0";
    public static final String ELEM1 = "elem1";
    public static final String ELEM2 = "elem2";
    public static final String ELEM3 = "elem3";
    public static final String ELEM4 = "elem4";
    public static final String ELEM5 = "elem5";
    public Item_attributesTable() {
super(Item_attributesTable.class);
}
    public IntegergetItemId() {
        return get(ITEMID, Integer.class);
    }
    public IntegergetAugAttributes() {
        return get(AUGATTRIBUTES, Integer.class);
    }
    public IntegergetAugSkillId() {
        return get(AUGSKILLID, Integer.class);
    }
    public IntegergetAugSkillLevel() {
        return get(AUGSKILLLEVEL, Integer.class);
    }
    public IntegergetElemType() {
        return get(ELEMTYPE, Integer.class);
    }
    public IntegergetElemValue() {
        return get(ELEMVALUE, Integer.class);
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

    public void setItemId() {
        set(ITEMID, value);
    }
    public void setAugAttributes() {
        set(AUGATTRIBUTES, value);
    }
    public void setAugSkillId() {
        set(AUGSKILLID, value);
    }
    public void setAugSkillLevel() {
        set(AUGSKILLLEVEL, value);
    }
    public void setElemType() {
        set(ELEMTYPE, value);
    }
    public void setElemValue() {
        set(ELEMVALUE, value);
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

}
