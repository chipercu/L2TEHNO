package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.LevelRewardsResource;

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
