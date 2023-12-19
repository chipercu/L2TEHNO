package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemMallResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemMallBuilder extends ResourceBuilder<ItemMallResource> {

    public ItemMallBuilder() {
        try {
            resourceClass = ItemMallResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemMallBuilder withOrd(Integer value) {
        with(ItemMallResource.ORD, value);
        return this;
    }

    public ItemMallBuilder withName(String value) {
        with(ItemMallResource.NAME, value);
        return this;
    }

    public ItemMallBuilder withItemId(Integer value) {
        with(ItemMallResource.ITEM_ID, value);
        return this;
    }

    public ItemMallBuilder withCount(Integer value) {
        with(ItemMallResource.COUNT, value);
        return this;
    }

    public ItemMallBuilder withPrice(Integer value) {
        with(ItemMallResource.PRICE, value);
        return this;
    }

    public ItemMallBuilder withICategory2(Integer value) {
        with(ItemMallResource.I_CATEGORY2, value);
        return this;
    }

    public ItemMallBuilder withOnSale(Integer value) {
        with(ItemMallResource.ON_SALE, value);
        return this;
    }

    public ItemMallBuilder withIStartSale(Integer value) {
        with(ItemMallResource.I_START_SALE, value);
        return this;
    }

    public ItemMallBuilder withIEndSale(Integer value) {
        with(ItemMallResource.I_END_SALE, value);
        return this;
    }

    public ItemMallBuilder withIStartHour(Integer value) {
        with(ItemMallResource.I_START_HOUR, value);
        return this;
    }

    public ItemMallBuilder withIStartMin(Integer value) {
        with(ItemMallResource.I_START_MIN, value);
        return this;
    }

    public ItemMallBuilder withIEndHour(Integer value) {
        with(ItemMallResource.I_END_HOUR, value);
        return this;
    }

    public ItemMallBuilder withIEndMin(Integer value) {
        with(ItemMallResource.I_END_MIN, value);
        return this;
    }

    public ItemMallBuilder withIStock(Integer value) {
        with(ItemMallResource.I_STOCK, value);
        return this;
    }

    public ItemMallBuilder withIMaxStock(Integer value) {
        with(ItemMallResource.I_MAX_STOCK, value);
        return this;
    }

}
