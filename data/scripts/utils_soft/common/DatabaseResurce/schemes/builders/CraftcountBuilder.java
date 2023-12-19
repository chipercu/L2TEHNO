package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CraftcountResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CraftcountBuilder extends ResourceBuilder<CraftcountResource> {

    public CraftcountBuilder() {
        try {
            resourceClass = CraftcountResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CraftcountBuilder withCharId(Integer value) {
        with(CraftcountResource.CHAR_ID, value);
        return this;
    }

    public CraftcountBuilder withItemId(Integer value) {
        with(CraftcountResource.ITEM_ID, value);
        return this;
    }

    public CraftcountBuilder withCount(Long value) {
        with(CraftcountResource.COUNT, value);
        return this;
    }

}
