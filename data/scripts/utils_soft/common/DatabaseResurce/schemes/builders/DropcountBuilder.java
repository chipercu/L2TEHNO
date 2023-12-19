package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.DropcountResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class DropcountBuilder extends ResourceBuilder<DropcountResource> {

    public DropcountBuilder() {
        try {
            resourceClass = DropcountResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DropcountBuilder withCharId(Integer value) {
        with(DropcountResource.CHAR_ID, value);
        return this;
    }

    public DropcountBuilder withItemId(Integer value) {
        with(DropcountResource.ITEM_ID, value);
        return this;
    }

    public DropcountBuilder withCount(Long value) {
        with(DropcountResource.COUNT, value);
        return this;
    }

}
