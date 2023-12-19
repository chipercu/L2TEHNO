package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.LevelRewardsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class LevelRewardsBuilder extends ResourceBuilder<LevelRewardsResource> {

    public LevelRewardsBuilder() {
        try {
            resourceClass = LevelRewardsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LevelRewardsBuilder withObjectId(Integer value) {
        with(LevelRewardsResource.OBJECT_ID, value);
        return this;
    }

    public LevelRewardsBuilder withClassId(Integer value) {
        with(LevelRewardsResource.CLASS_ID, value);
        return this;
    }

    public LevelRewardsBuilder withClassLevel(Integer value) {
        with(LevelRewardsResource.CLASS_LEVEL, value);
        return this;
    }

}
