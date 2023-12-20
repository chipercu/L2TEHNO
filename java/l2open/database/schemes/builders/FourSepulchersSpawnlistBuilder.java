package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.FourSepulchersSpawnlistResource;

public class FourSepulchersSpawnlistBuilder extends ResourceBuilder<FourSepulchersSpawnlistResource> {

    public FourSepulchersSpawnlistBuilder() {
        try {
            resourceClass = FourSepulchersSpawnlistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FourSepulchersSpawnlistBuilder withId(Integer value) {
        with(FourSepulchersSpawnlistResource.ID, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withLocation(String value) {
        with(FourSepulchersSpawnlistResource.LOCATION, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withCount(Integer value) {
        with(FourSepulchersSpawnlistResource.COUNT, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withNpcTemplateid(Integer value) {
        with(FourSepulchersSpawnlistResource.NPC_TEMPLATEID, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withLocx(Integer value) {
        with(FourSepulchersSpawnlistResource.LOCX, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withLocy(Integer value) {
        with(FourSepulchersSpawnlistResource.LOCY, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withLocz(Integer value) {
        with(FourSepulchersSpawnlistResource.LOCZ, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withRandomx(Integer value) {
        with(FourSepulchersSpawnlistResource.RANDOMX, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withRandomy(Integer value) {
        with(FourSepulchersSpawnlistResource.RANDOMY, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withHeading(Integer value) {
        with(FourSepulchersSpawnlistResource.HEADING, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withRespawnDelay(Integer value) {
        with(FourSepulchersSpawnlistResource.RESPAWN_DELAY, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withKeyNpcId(Integer value) {
        with(FourSepulchersSpawnlistResource.KEY_NPC_ID, value);
        return this;
    }

    public FourSepulchersSpawnlistBuilder withSpawntype(Integer value) {
        with(FourSepulchersSpawnlistResource.SPAWNTYPE, value);
        return this;
    }

}
