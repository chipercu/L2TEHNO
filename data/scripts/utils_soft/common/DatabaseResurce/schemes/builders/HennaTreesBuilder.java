package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.HennaTreesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class HennaTreesBuilder extends ResourceBuilder<HennaTreesResource> {

    public HennaTreesBuilder() {
        try {
            resourceClass = HennaTreesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HennaTreesBuilder withClassId(Integer value) {
        with(HennaTreesResource.CLASS_ID, value);
        return this;
    }

    public HennaTreesBuilder withSymbolId(Integer value) {
        with(HennaTreesResource.SYMBOL_ID, value);
        return this;
    }

}
