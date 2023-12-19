package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RaidbossPointsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RaidbossPointsBuilder extends ResourceBuilder<RaidbossPointsResource> {

    public RaidbossPointsBuilder() {
        try {
            resourceClass = RaidbossPointsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RaidbossPointsBuilder withOwnerId(Integer value) {
        with(RaidbossPointsResource.OWNER_ID, value);
        return this;
    }

    public RaidbossPointsBuilder withBossId(Integer value) {
        with(RaidbossPointsResource.BOSS_ID, value);
        return this;
    }

    public RaidbossPointsBuilder withPoints(Integer value) {
        with(RaidbossPointsResource.POINTS, value);
        return this;
    }

}
