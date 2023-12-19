package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RangResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RangBuilder extends ResourceBuilder<RangResource> {

    public RangBuilder() {
        try {
            resourceClass = RangResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RangBuilder withCharId(Integer value) {
        with(RangResource.CHAR_ID, value);
        return this;
    }

    public RangBuilder withRangPoint(Long value) {
        with(RangResource.RANG_POINT, value);
        return this;
    }

}
