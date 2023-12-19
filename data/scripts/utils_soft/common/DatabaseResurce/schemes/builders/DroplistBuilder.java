package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.DroplistResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class DroplistBuilder extends ResourceBuilder<DroplistResource> {

    public DroplistBuilder() {
        try {
            resourceClass = DroplistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DroplistBuilder withMobId(Integer value) {
        with(DroplistResource.MOB_ID, value);
        return this;
    }

    public DroplistBuilder withItemId(Integer value) {
        with(DroplistResource.ITEM_ID, value);
        return this;
    }

    public DroplistBuilder withMin(Long value) {
        with(DroplistResource.MIN, value);
        return this;
    }

    public DroplistBuilder withMax(Long value) {
        with(DroplistResource.MAX, value);
        return this;
    }

    public DroplistBuilder withSweep(Integer value) {
        with(DroplistResource.SWEEP, value);
        return this;
    }

    public DroplistBuilder withChance(Integer value) {
        with(DroplistResource.CHANCE, value);
        return this;
    }

    public DroplistBuilder withCategory(Integer value) {
        with(DroplistResource.CATEGORY, value);
        return this;
    }

}
