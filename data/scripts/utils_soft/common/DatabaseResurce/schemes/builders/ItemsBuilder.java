package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemsBuilder extends ResourceBuilder<ItemsResource> {

    public ItemsBuilder() {
        try {
            resourceClass = ItemsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemsBuilder withObjectId(Integer value) {
        with(ItemsResource.OBJECT_ID, value);
        return this;
    }

    public ItemsBuilder withOwnerId(Integer value) {
        with(ItemsResource.OWNER_ID, value);
        return this;
    }

    public ItemsBuilder withItemId(Integer value) {
        with(ItemsResource.ITEM_ID, value);
        return this;
    }

    public ItemsBuilder withVisualItemId(Integer value) {
        with(ItemsResource.VISUAL_ITEM_ID, value);
        return this;
    }

    public ItemsBuilder withName(String value) {
        with(ItemsResource.NAME, value);
        return this;
    }

    public ItemsBuilder withCount(Long value) {
        with(ItemsResource.COUNT, value);
        return this;
    }

    public ItemsBuilder withEnchantLevel(Integer value) {
        with(ItemsResource.ENCHANT_LEVEL, value);
        return this;
    }

    public ItemsBuilder withVisualEnchantLevel(Integer value) {
        with(ItemsResource.VISUAL_ENCHANT_LEVEL, value);
        return this;
    }

    public ItemsBuilder withClass(Enum value) {
        with(ItemsResource.CLASS, value);
        return this;
    }

    public ItemsBuilder withLoc(Enum value) {
        with(ItemsResource.LOC, value);
        return this;
    }

    public ItemsBuilder withLocData(Integer value) {
        with(ItemsResource.LOC_DATA, value);
        return this;
    }

    public ItemsBuilder withCustomType1(Integer value) {
        with(ItemsResource.CUSTOM_TYPE1, value);
        return this;
    }

    public ItemsBuilder withCustomType2(Integer value) {
        with(ItemsResource.CUSTOM_TYPE2, value);
        return this;
    }

    public ItemsBuilder withShadowLifeTime(Integer value) {
        with(ItemsResource.SHADOW_LIFE_TIME, value);
        return this;
    }

    public ItemsBuilder withFlags(Integer value) {
        with(ItemsResource.FLAGS, value);
        return this;
    }

    public ItemsBuilder withEnergy(Integer value) {
        with(ItemsResource.ENERGY, value);
        return this;
    }

    public ItemsBuilder withTemporal(Enum value) {
        with(ItemsResource.TEMPORAL, value);
        return this;
    }

    public ItemsBuilder withEnchantTime(Long value) {
        with(ItemsResource.ENCHANT_TIME, value);
        return this;
    }

}
