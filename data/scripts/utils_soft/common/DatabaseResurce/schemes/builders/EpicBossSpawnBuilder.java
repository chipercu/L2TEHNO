package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.EpicBossSpawnResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
