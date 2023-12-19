package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.DominionRewardsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class DominionRewardsBuilder extends ResourceBuilder<DominionRewardsResource> {

    public DominionRewardsBuilder() {
        try {
            resourceClass = DominionRewardsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DominionRewardsBuilder withId(Integer value) {
        with(DominionRewardsResource.ID, value);
        return this;
    }

    public DominionRewardsBuilder withObjectId(Integer value) {
        with(DominionRewardsResource.OBJECT_ID, value);
        return this;
    }

    public DominionRewardsBuilder withStaticBadges(Integer value) {
        with(DominionRewardsResource.STATIC_BADGES, value);
        return this;
    }

    public DominionRewardsBuilder withKillReward(Integer value) {
        with(DominionRewardsResource.KILL_REWARD, value);
        return this;
    }

    public DominionRewardsBuilder withOnlineReward(Integer value) {
        with(DominionRewardsResource.ONLINE_REWARD, value);
        return this;
    }

}
