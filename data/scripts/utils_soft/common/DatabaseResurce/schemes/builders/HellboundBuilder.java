package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.HellboundResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class HellboundBuilder extends ResourceBuilder<HellboundResource> {

    public HellboundBuilder() {
        try {
            resourceClass = HellboundResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HellboundBuilder withName(Integer value) {
        with(HellboundResource.NAME, value);
        return this;
    }

    public HellboundBuilder withHbPoints(Integer value) {
        with(HellboundResource.HB_POINTS, value);
        return this;
    }

    public HellboundBuilder withHbLevel(Integer value) {
        with(HellboundResource.HB_LEVEL, value);
        return this;
    }

    public HellboundBuilder withUnlocked(Integer value) {
        with(HellboundResource.UNLOCKED, value);
        return this;
    }

    public HellboundBuilder withDummy(Integer value) {
        with(HellboundResource.DUMMY, value);
        return this;
    }

}
