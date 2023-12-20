package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.DominionRewardsResource;

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
