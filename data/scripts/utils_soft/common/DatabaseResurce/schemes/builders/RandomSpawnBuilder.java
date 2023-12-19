package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RandomSpawnResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RandomSpawnBuilder extends ResourceBuilder<RandomSpawnResource> {

    public RandomSpawnBuilder() {
        try {
            resourceClass = RandomSpawnResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RandomSpawnBuilder withGroupId(Integer value) {
        with(RandomSpawnResource.GROUP_ID, value);
        return this;
    }

    public RandomSpawnBuilder withNpcId(Integer value) {
        with(RandomSpawnResource.NPC_ID, value);
        return this;
    }

    public RandomSpawnBuilder withCount(Integer value) {
        with(RandomSpawnResource.COUNT, value);
        return this;
    }

    public RandomSpawnBuilder withInitialDelay(Long value) {
        with(RandomSpawnResource.INITIAL_DELAY, value);
        return this;
    }

    public RandomSpawnBuilder withRespawnDelay(Long value) {
        with(RandomSpawnResource.RESPAWN_DELAY, value);
        return this;
    }

    public RandomSpawnBuilder withDespawnDelay(Long value) {
        with(RandomSpawnResource.DESPAWN_DELAY, value);
        return this;
    }

    public RandomSpawnBuilder withBroadcastSpawn(Enum value) {
        with(RandomSpawnResource.BROADCAST_SPAWN, value);
        return this;
    }

    public RandomSpawnBuilder withRandomSpawn(Enum value) {
        with(RandomSpawnResource.RANDOM_SPAWN, value);
        return this;
    }

}
