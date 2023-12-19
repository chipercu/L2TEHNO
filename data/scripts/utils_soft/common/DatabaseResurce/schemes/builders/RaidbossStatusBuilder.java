package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RaidbossStatusResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RaidbossStatusBuilder extends ResourceBuilder<RaidbossStatusResource> {

    public RaidbossStatusBuilder() {
        try {
            resourceClass = RaidbossStatusResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RaidbossStatusBuilder withId(Integer value) {
        with(RaidbossStatusResource.ID, value);
        return this;
    }

    public RaidbossStatusBuilder withCurrentHp(Integer value) {
        with(RaidbossStatusResource.CURRENT_HP, value);
        return this;
    }

    public RaidbossStatusBuilder withCurrentMp(Integer value) {
        with(RaidbossStatusResource.CURRENT_MP, value);
        return this;
    }

    public RaidbossStatusBuilder withRespawnDelay(Integer value) {
        with(RaidbossStatusResource.RESPAWN_DELAY, value);
        return this;
    }

}
