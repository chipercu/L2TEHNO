package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SpawnlistResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SpawnlistBuilder extends ResourceBuilder<SpawnlistResource> {

    public SpawnlistBuilder() {
        try {
            resourceClass = SpawnlistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SpawnlistBuilder withLocation(String value) {
        with(SpawnlistResource.LOCATION, value);
        return this;
    }

    public SpawnlistBuilder withCount(Integer value) {
        with(SpawnlistResource.COUNT, value);
        return this;
    }

    public SpawnlistBuilder withNpcTemplateid(Integer value) {
        with(SpawnlistResource.NPC_TEMPLATEID, value);
        return this;
    }

    public SpawnlistBuilder withLocx(Integer value) {
        with(SpawnlistResource.LOCX, value);
        return this;
    }

    public SpawnlistBuilder withLocy(Integer value) {
        with(SpawnlistResource.LOCY, value);
        return this;
    }

    public SpawnlistBuilder withLocz(Integer value) {
        with(SpawnlistResource.LOCZ, value);
        return this;
    }

    public SpawnlistBuilder withHeading(Integer value) {
        with(SpawnlistResource.HEADING, value);
        return this;
    }

    public SpawnlistBuilder withRespawnDelay(Integer value) {
        with(SpawnlistResource.RESPAWN_DELAY, value);
        return this;
    }

    public SpawnlistBuilder withRespawnDelayRnd(Integer value) {
        with(SpawnlistResource.RESPAWN_DELAY_RND, value);
        return this;
    }

    public SpawnlistBuilder withLocId(String value) {
        with(SpawnlistResource.LOC_ID, value);
        return this;
    }

    public SpawnlistBuilder withBanedLocId(String value) {
        with(SpawnlistResource.BANED_LOC_ID, value);
        return this;
    }

    public SpawnlistBuilder withPeriodOfDay(Integer value) {
        with(SpawnlistResource.PERIOD_OF_DAY, value);
        return this;
    }

    public SpawnlistBuilder withReflection(Integer value) {
        with(SpawnlistResource.REFLECTION, value);
        return this;
    }

    public SpawnlistBuilder withStatParam(String value) {
        with(SpawnlistResource.STAT_PARAM, value);
        return this;
    }

    public SpawnlistBuilder withAiParam(String value) {
        with(SpawnlistResource.AI_PARAM, value);
        return this;
    }

}
