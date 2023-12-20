package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.LastimperialtombSpawnlistResource;

public class LastimperialtombSpawnlistBuilder extends ResourceBuilder<LastimperialtombSpawnlistResource> {

    public LastimperialtombSpawnlistBuilder() {
        try {
            resourceClass = LastimperialtombSpawnlistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LastimperialtombSpawnlistBuilder withCount(Integer value) {
        with(LastimperialtombSpawnlistResource.COUNT, value);
        return this;
    }

    public LastimperialtombSpawnlistBuilder withNpcTemplateid(Integer value) {
        with(LastimperialtombSpawnlistResource.NPC_TEMPLATEID, value);
        return this;
    }

    public LastimperialtombSpawnlistBuilder withLocx(Integer value) {
        with(LastimperialtombSpawnlistResource.LOCX, value);
        return this;
    }

    public LastimperialtombSpawnlistBuilder withLocy(Integer value) {
        with(LastimperialtombSpawnlistResource.LOCY, value);
        return this;
    }

    public LastimperialtombSpawnlistBuilder withLocz(Integer value) {
        with(LastimperialtombSpawnlistResource.LOCZ, value);
        return this;
    }

    public LastimperialtombSpawnlistBuilder withHeading(Integer value) {
        with(LastimperialtombSpawnlistResource.HEADING, value);
        return this;
    }

    public LastimperialtombSpawnlistBuilder withRespawnDelay(Integer value) {
        with(LastimperialtombSpawnlistResource.RESPAWN_DELAY, value);
        return this;
    }

}
