package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RandomSpawnLocResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RandomSpawnLocBuilder extends ResourceBuilder<RandomSpawnLocResource> {

    public RandomSpawnLocBuilder() {
        try {
            resourceClass = RandomSpawnLocResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RandomSpawnLocBuilder withGroupId(Integer value) {
        with(RandomSpawnLocResource.GROUP_ID, value);
        return this;
    }

    public RandomSpawnLocBuilder withX(Integer value) {
        with(RandomSpawnLocResource.X, value);
        return this;
    }

    public RandomSpawnLocBuilder withY(Integer value) {
        with(RandomSpawnLocResource.Y, value);
        return this;
    }

    public RandomSpawnLocBuilder withZ(Integer value) {
        with(RandomSpawnLocResource.Z, value);
        return this;
    }

    public RandomSpawnLocBuilder withHeading(Integer value) {
        with(RandomSpawnLocResource.HEADING, value);
        return this;
    }

}
