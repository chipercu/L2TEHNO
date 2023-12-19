package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SiegeDoorResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SiegeDoorBuilder extends ResourceBuilder<SiegeDoorResource> {

    public SiegeDoorBuilder() {
        try {
            resourceClass = SiegeDoorResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SiegeDoorBuilder withUnitId(Integer value) {
        with(SiegeDoorResource.UNIT_ID, value);
        return this;
    }

    public SiegeDoorBuilder withId(Integer value) {
        with(SiegeDoorResource.ID, value);
        return this;
    }

}
