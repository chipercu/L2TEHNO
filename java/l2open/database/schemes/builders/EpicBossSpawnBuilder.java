package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.EpicBossSpawnResource;

public class EpicBossSpawnBuilder extends ResourceBuilder<EpicBossSpawnResource> {

    public EpicBossSpawnBuilder() {
        try {
            resourceClass = EpicBossSpawnResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public EpicBossSpawnBuilder withBossId(Integer value) {
        with(EpicBossSpawnResource.BOSS_ID, value);
        return this;
    }

    public EpicBossSpawnBuilder withRespawnDate(Integer value) {
        with(EpicBossSpawnResource.RESPAWN_DATE, value);
        return this;
    }

    public EpicBossSpawnBuilder withState(Integer value) {
        with(EpicBossSpawnResource.STATE, value);
        return this;
    }

}
