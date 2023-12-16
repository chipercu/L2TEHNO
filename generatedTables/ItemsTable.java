package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.ItemsTable.*;

@Table(
        name = "items",
        fields = {
                @Field(name = OBJECT_ID,
                @Field(name = OWNER_ID,
                @Field(name = ITEM_ID,
                @Field(name = VISUAL_ITEM_ID,
                @Field(name = NAME,
                @Field(name = COUNT,
                @Field(name = ENCHANT_LEVEL,
                @Field(name = VISUAL_ENCHANT_LEVEL,
                @Field(name = CLASS,
                @Field(name = LOC,
                @Field(name = LOC_DATA,
                @Field(name = CUSTOM_TYPE1,
                @Field(name = CUSTOM_TYPE2,
                @Field(name = SHADOW_LIFE_TIME,
                @Field(name = FLAGS,
                @Field(name = ENERGY,
                @Field(name = TEMPORAL,
                @Field(name = ENCHANT_TIME,
        }
)
public class ItemsTable extends DataBaseTable<ItemsTable> {

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
    public ItemsTable() {
super(ItemsTable.class);
}
    public IntegergetObject_id() {
        return get(OBJECT_ID, Integer.class);
    }
    public IntegergetOwner_id() {
        return get(OWNER_ID, Integer.class);
    }
    public IntegergetItem_id() {
        return get(ITEM_ID, Integer.class);
    }
    public IntegergetVisual_item_id() {
        return get(VISUAL_ITEM_ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public ObjectgetCount() {
        return get(COUNT, Object.class);
    }
    public IntegergetEnchant_level() {
        return get(ENCHANT_LEVEL, Integer.class);
    }
    public IntegergetVisual_enchant_level() {
        return get(VISUAL_ENCHANT_LEVEL, Integer.class);
    }
    public ObjectgetClass() {
        return get(CLASS, Object.class);
    }
    public ObjectgetLoc() {
        return get(LOC, Object.class);
    }
    public IntegergetLoc_data() {
        return get(LOC_DATA, Integer.class);
    }
    public IntegergetCustom_type1() {
        return get(CUSTOM_TYPE1, Integer.class);
    }
    public IntegergetCustom_type2() {
        return get(CUSTOM_TYPE2, Integer.class);
    }
    public IntegergetShadow_life_time() {
        return get(SHADOW_LIFE_TIME, Integer.class);
    }
    public IntegergetFlags() {
        return get(FLAGS, Integer.class);
    }
    public IntegergetEnergy() {
        return get(ENERGY, Integer.class);
    }
    public ObjectgetTemporal() {
        return get(TEMPORAL, Object.class);
    }
    public ObjectgetEnchant_time() {
        return get(ENCHANT_TIME, Object.class);
    }

    public void setObject_id() {
        set(OBJECT_ID, value);
    }
    public void setOwner_id() {
        set(OWNER_ID, value);
    }
    public void setItem_id() {
        set(ITEM_ID, value);
    }
    public void setVisual_item_id() {
        set(VISUAL_ITEM_ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setEnchant_level() {
        set(ENCHANT_LEVEL, value);
    }
    public void setVisual_enchant_level() {
        set(VISUAL_ENCHANT_LEVEL, value);
    }
    public void setClass() {
        set(CLASS, value);
    }
    public void setLoc() {
        set(LOC, value);
    }
    public void setLoc_data() {
        set(LOC_DATA, value);
    }
    public void setCustom_type1() {
        set(CUSTOM_TYPE1, value);
    }
    public void setCustom_type2() {
        set(CUSTOM_TYPE2, value);
    }
    public void setShadow_life_time() {
        set(SHADOW_LIFE_TIME, value);
    }
    public void setFlags() {
        set(FLAGS, value);
    }
    public void setEnergy() {
        set(ENERGY, value);
    }
    public void setTemporal() {
        set(TEMPORAL, value);
    }
    public void setEnchant_time() {
        set(ENCHANT_TIME, value);
    }

}
