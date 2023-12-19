package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.MinionsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class MinionsBuilder extends ResourceBuilder<MinionsResource> {

    public MinionsBuilder() {
        try {
            resourceClass = MinionsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MinionsBuilder withBossId(Integer value) {
        with(MinionsResource.BOSS_ID, value);
        return this;
    }

    public MinionsBuilder withMinionId(Integer value) {
        with(MinionsResource.MINION_ID, value);
        return this;
    }

    public MinionsBuilder withAmount(Integer value) {
        with(MinionsResource.AMOUNT, value);
        return this;
    }

}
