package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.RaidbossPointsResource;

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
