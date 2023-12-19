package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemsDelayedResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemsDelayedBuilder extends ResourceBuilder<ItemsDelayedResource> {

    public ItemsDelayedBuilder() {
        try {
            resourceClass = ItemsDelayedResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemsDelayedBuilder withPaymentId(Integer value) {
        with(ItemsDelayedResource.PAYMENT_ID, value);
        return this;
    }

    public ItemsDelayedBuilder withOwnerId(Integer value) {
        with(ItemsDelayedResource.OWNER_ID, value);
        return this;
    }

    public ItemsDelayedBuilder withItemId(Integer value) {
        with(ItemsDelayedResource.ITEM_ID, value);
        return this;
    }

    public ItemsDelayedBuilder withCount(Integer value) {
        with(ItemsDelayedResource.COUNT, value);
        return this;
    }

    public ItemsDelayedBuilder withEnchantLevel(Integer value) {
        with(ItemsDelayedResource.ENCHANT_LEVEL, value);
        return this;
    }

    public ItemsDelayedBuilder withAttribute(Integer value) {
        with(ItemsDelayedResource.ATTRIBUTE, value);
        return this;
    }

    public ItemsDelayedBuilder withAttributeLevel(Integer value) {
        with(ItemsDelayedResource.ATTRIBUTE_LEVEL, value);
        return this;
    }

    public ItemsDelayedBuilder withElem0(Integer value) {
        with(ItemsDelayedResource.ELEM0, value);
        return this;
    }

    public ItemsDelayedBuilder withElem1(Integer value) {
        with(ItemsDelayedResource.ELEM1, value);
        return this;
    }

    public ItemsDelayedBuilder withElem2(Integer value) {
        with(ItemsDelayedResource.ELEM2, value);
        return this;
    }

    public ItemsDelayedBuilder withElem3(Integer value) {
        with(ItemsDelayedResource.ELEM3, value);
        return this;
    }

    public ItemsDelayedBuilder withElem4(Integer value) {
        with(ItemsDelayedResource.ELEM4, value);
        return this;
    }

    public ItemsDelayedBuilder withElem5(Integer value) {
        with(ItemsDelayedResource.ELEM5, value);
        return this;
    }

    public ItemsDelayedBuilder withFlags(Integer value) {
        with(ItemsDelayedResource.FLAGS, value);
        return this;
    }

    public ItemsDelayedBuilder withPaymentStatus(Integer value) {
        with(ItemsDelayedResource.PAYMENT_STATUS, value);
        return this;
    }

    public ItemsDelayedBuilder withDescription(String value) {
        with(ItemsDelayedResource.DESCRIPTION, value);
        return this;
    }

}
