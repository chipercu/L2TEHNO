package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ItemsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "items",
        primary_key = {OBJECT_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJECT_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OWNER_ID , defValue = "null"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ITEM_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = VISUAL_ITEM_ID , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 100 , name = NAME , defValue = "NULL"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ENCHANT_LEVEL , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = VISUAL_ENCHANT_LEVEL , defValue = "-1"),
                @Column(is_null = NO , type = ENUM , type_size = 10 , name = CLASS , defValue = "OTHER"),
                @Column(is_null = NO , type = ENUM , type_size = 13 , name = LOC , defValue = "null"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = LOC_DATA , defValue = "NULL"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CUSTOM_TYPE1 , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CUSTOM_TYPE2 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SHADOW_LIFE_TIME , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FLAGS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ENERGY , defValue = "0"),
                @Column(is_null = NO , type = ENUM , type_size = 5 , name = TEMPORAL , defValue = "false"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ENCHANT_TIME , defValue = "0"),
        }
)
public class ItemsResource extends DataBaseTable<ItemsResource> {

    public static final String OBJECT_ID = "object_id";
    public static final String OWNER_ID = "owner_id";
    public static final String ITEM_ID = "item_id";
    public static final String VISUAL_ITEM_ID = "visual_item_id";
    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String ENCHANT_LEVEL = "enchant_level";
    public static final String VISUAL_ENCHANT_LEVEL = "visual_enchant_level";
    public static final String CLASS = "class";
    public static final String LOC = "loc";
    public static final String LOC_DATA = "loc_data";
    public static final String CUSTOM_TYPE1 = "custom_type1";
    public static final String CUSTOM_TYPE2 = "custom_type2";
    public static final String SHADOW_LIFE_TIME = "shadow_life_time";
    public static final String FLAGS = "flags";
    public static final String ENERGY = "energy";
    public static final String TEMPORAL = "temporal";
    public static final String ENCHANT_TIME = "enchant_time";

    public ItemsResource() {
        super(ItemsResource.class);
}

    public Integer getObjectId() {
        return get(OBJECT_ID);
    }
    public Integer getOwnerId() {
        return get(OWNER_ID);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Integer getVisualItemId() {
        return get(VISUAL_ITEM_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Long getCount() {
        return get(COUNT);
    }
    public Integer getEnchantLevel() {
        return get(ENCHANT_LEVEL);
    }
    public Integer getVisualEnchantLevel() {
        return get(VISUAL_ENCHANT_LEVEL);
    }
    public String getClass_() {
        return get(CLASS);
    }
    public String getLoc() {
        return get(LOC);
    }
    public Integer getLocData() {
        return get(LOC_DATA);
    }
    public Integer getCustomType1() {
        return get(CUSTOM_TYPE1);
    }
    public Integer getCustomType2() {
        return get(CUSTOM_TYPE2);
    }
    public Integer getShadowLifeTime() {
        return get(SHADOW_LIFE_TIME);
    }
    public Integer getFlags() {
        return get(FLAGS);
    }
    public Integer getEnergy() {
        return get(ENERGY);
    }
    public Boolean getTemporal() {
        return get(TEMPORAL);
    }
    public Long getEnchantTime() {
        return get(ENCHANT_TIME);
    }

    public void setObjectId(Integer value) {
        set(OBJECT_ID, value);
    }
    public void setOwnerId(Integer value) {
        set(OWNER_ID, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setVisualItemId(Integer value) {
        set(VISUAL_ITEM_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setCount(Long value) {
        set(COUNT, value);
    }
    public void setEnchantLevel(Integer value) {
        set(ENCHANT_LEVEL, value);
    }
    public void setVisualEnchantLevel(Integer value) {
        set(VISUAL_ENCHANT_LEVEL, value);
    }
    public void setClass(String value) {
        set(CLASS, value);
    }
    public void setLoc(String value) {
        set(LOC, value);
    }
    public void setLocData(Integer value) {
        set(LOC_DATA, value);
    }
    public void setCustomType1(Integer value) {
        set(CUSTOM_TYPE1, value);
    }
    public void setCustomType2(Integer value) {
        set(CUSTOM_TYPE2, value);
    }
    public void setShadowLifeTime(Integer value) {
        set(SHADOW_LIFE_TIME, value);
    }
    public void setFlags(Integer value) {
        set(FLAGS, value);
    }
    public void setEnergy(Integer value) {
        set(ENERGY, value);
    }
    public void setTemporal(Boolean value) {
        set(TEMPORAL, value);
    }
    public void setEnchantTime(Long value) {
        set(ENCHANT_TIME, value);
    }

}
