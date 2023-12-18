package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "items",
        primary_key = {OBJECT_ID},
        fields = {
                @Field(name = OBJECT_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = OWNER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = ITEM_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = VISUAL_ITEM_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , type = VARCHAR , type_size = 100 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = COUNT , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = ENCHANT_LEVEL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = VISUAL_ENCHANT_LEVEL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = CLASS , type = ENUM , type_size = 10 , nullable = false , defValue = @DefValue(String = "OTHER")),
                @Field(name = LOC , type = ENUM , type_size = 13 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = LOC_DATA , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CUSTOM_TYPE1 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CUSTOM_TYPE2 , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SHADOW_LIFE_TIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FLAGS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ENERGY , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TEMPORAL , type = ENUM , type_size = 5 , nullable = false , defValue = @DefValue(Boolean = false)),
                @Field(name = ENCHANT_TIME , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
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


    public ItemsResource(String object_id){
        super(ItemsResource.class);
        getSTAT_SET().set(OBJECT_ID, object_id);
    }

    public Integer getObjectId() {
        return get(OBJECT_ID, Integer.class);
    }
    public Integer getOwnerId() {
        return get(OWNER_ID, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Integer getVisualItemId() {
        return get(VISUAL_ITEM_ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Long getCount() {
        return get(COUNT, Long.class);
    }
    public Integer getEnchantLevel() {
        return get(ENCHANT_LEVEL, Integer.class);
    }
    public Integer getVisualEnchantLevel() {
        return get(VISUAL_ENCHANT_LEVEL, Integer.class);
    }
    public String getClass_() {
        return get(CLASS, String.class);
    }
    public String getLoc() {
        return get(LOC, String.class);
    }
    public Integer getLocData() {
        return get(LOC_DATA, Integer.class);
    }
    public Integer getCustomType1() {
        return get(CUSTOM_TYPE1, Integer.class);
    }
    public Integer getCustomType2() {
        return get(CUSTOM_TYPE2, Integer.class);
    }
    public Integer getShadowLifeTime() {
        return get(SHADOW_LIFE_TIME, Integer.class);
    }
    public Integer getFlags() {
        return get(FLAGS, Integer.class);
    }
    public Integer getEnergy() {
        return get(ENERGY, Integer.class);
    }
    public Boolean getTemporal() {
        return get(TEMPORAL, Boolean.class);
    }
    public Long getEnchantTime() {
        return get(ENCHANT_TIME, Long.class);
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
