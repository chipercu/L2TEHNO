package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemallResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemallBuilder extends ResourceBuilder<ItemallResource> {

    public ItemallBuilder() {
        try {
            resourceClass = ItemallResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemallBuilder withId(Integer value) {
        with(ItemallResource.ID, value);
        return this;
    }

    public ItemallBuilder withReuse(Integer value) {
        with(ItemallResource.REUSE, value);
        return this;
    }

    public ItemallBuilder withMagicWeapon(Integer value) {
        with(ItemallResource.MAGIC_WEAPON, value);
        return this;
    }

    public ItemallBuilder withIsOlympiadCanUse(Integer value) {
        with(ItemallResource.IS_OLYMPIAD_CAN_USE, value);
        return this;
    }

    public ItemallBuilder withImmediateEffect(Integer value) {
        with(ItemallResource.IMMEDIATE_EFFECT, value);
        return this;
    }

    public ItemallBuilder withExImmediateEffect(Integer value) {
        with(ItemallResource.EX_IMMEDIATE_EFFECT, value);
        return this;
    }

    public ItemallBuilder withDelayShareGroup(Integer value) {
        with(ItemallResource.DELAY_SHARE_GROUP, value);
        return this;
    }

    public ItemallBuilder withIsPremium(Integer value) {
        with(ItemallResource.IS_PREMIUM, value);
        return this;
    }

}
