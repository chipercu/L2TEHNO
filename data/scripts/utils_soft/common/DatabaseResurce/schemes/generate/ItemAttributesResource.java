package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemAttributesResource.*;

@Table(
        name = "item_attributes",
        primary_key = {ITEM_ID},
        fields = {
                @Field(name = ITEM_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = AUG_ATTRIBUTES , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = AUG_SKILL_ID , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = AUG_SKILL_LEVEL , data_type = "int" , defValue = @DefValue(Integer = -1)),
                @Field(name = ELEM_TYPE , data_type = "tinyint" , defValue = @DefValue(Integer = -1)),
                @Field(name = ELEM_VALUE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM0 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM1 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM2 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM3 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM4 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ELEM5 , data_type = "int" , defValue = @DefValue(Integer = 0)),
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
        return get(ITEM_ID, Integer.class);
    }
    public Integer getAugAttributes() {
        return get(AUG_ATTRIBUTES, Integer.class);
    }
    public Integer getAugSkillId() {
        return get(AUG_SKILL_ID, Integer.class);
    }
    public Integer getAugSkillLevel() {
        return get(AUG_SKILL_LEVEL, Integer.class);
    }
    public Integer getElemType() {
        return get(ELEM_TYPE, Integer.class);
    }
    public Integer getElemValue() {
        return get(ELEM_VALUE, Integer.class);
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
