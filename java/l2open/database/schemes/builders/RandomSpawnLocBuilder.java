package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.RandomSpawnLocResource;

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
