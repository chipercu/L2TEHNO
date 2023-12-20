package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.RaidbossStatusResource;

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
