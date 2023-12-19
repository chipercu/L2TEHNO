package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ItemAttributesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "item_attributes",
        primary_key = {ITEM_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AUG_ATTRIBUTES , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AUG_SKILL_ID , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AUG_SKILL_LEVEL , defValue = "-1"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ELEM_TYPE , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM_VALUE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM0 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM1 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM2 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM3 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM4 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM5 , defValue = "0"),
        }
)
public class ItemAttributesResource extends DataBaseTable<ItemAttributesResource> {

    public static final String ITEM_ID = "itemId";
    public static final String AUG_ATTRIBUTES = "augAttributes";
    public static final String AUG_SKILL_ID = "augSkillId";
    public static final String AUG_SKILL_LEVEL = "augSkillLevel";
    public static final String ELEM_TYPE = "elemType";
    public static final String ELEM_VALUE = "elemValue";
    public static final String ELEM0 = "elem0";
    public static final String ELEM1 = "elem1";
    public static final String ELEM2 = "elem2";
    public static final String ELEM3 = "elem3";
    public static final String ELEM4 = "elem4";
    public static final String ELEM5 = "elem5";

    public ItemAttributesResource() {
        super(ItemAttributesResource.class);
}

    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Integer getAugAttributes() {
        return get(AUG_ATTRIBUTES);
    }
    public Integer getAugSkillId() {
        return get(AUG_SKILL_ID);
    }
    public Integer getAugSkillLevel() {
        return get(AUG_SKILL_LEVEL);
    }
    public Integer getElemType() {
        return get(ELEM_TYPE);
    }
    public Integer getElemValue() {
        return get(ELEM_VALUE);
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

    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setAugAttributes(Integer value) {
        set(AUG_ATTRIBUTES, value);
    }
    public void setAugSkillId(Integer value) {
        set(AUG_SKILL_ID, value);
    }
    public void setAugSkillLevel(Integer value) {
        set(AUG_SKILL_LEVEL, value);
    }
    public void setElemType(Integer value) {
        set(ELEM_TYPE, value);
    }
    public void setElemValue(Integer value) {
        set(ELEM_VALUE, value);
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

}
