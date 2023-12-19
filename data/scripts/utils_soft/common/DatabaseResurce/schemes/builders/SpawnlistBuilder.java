package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SpawnlistResource;
import java.lang.reflect.Field;

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
        try {
            final Field field = resourceClass.getDeclaredField("LOCATION");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withCount(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("COUNT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withNpcTemplateid(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("NPC_TEMPLATEID");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withLocx(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("LOCX");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withLocy(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("LOCY");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withLocz(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("LOCZ");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withHeading(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("HEADING");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withRespawnDelay(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("RESPAWN_DELAY");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withRespawnDelayRnd(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("RESPAWN_DELAY_RND");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withLocId(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("LOC_ID");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withBanedLocId(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("BANED_LOC_ID");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withPeriodOfDay(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("PERIOD_OF_DAY");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withReflection(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("REFLECTION");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withStatParam(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("STAT_PARAM");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SpawnlistBuilder withAiParam(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("AI_PARAM");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

}
