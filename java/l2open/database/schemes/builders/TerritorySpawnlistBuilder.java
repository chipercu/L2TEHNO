package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.TerritorySpawnlistResource;

public class TerritorySpawnlistBuilder extends ResourceBuilder<TerritorySpawnlistResource> {

    public TerritorySpawnlistBuilder() {
        try {
            resourceClass = TerritorySpawnlistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TerritorySpawnlistBuilder withCastleId(Integer value) {
        with(TerritorySpawnlistResource.CASTLE_ID, value);
        return this;
    }

    public TerritorySpawnlistBuilder withNpcId(Integer value) {
        with(TerritorySpawnlistResource.NPC_ID, value);
        return this;
    }

    public TerritorySpawnlistBuilder withX(Integer value) {
        with(TerritorySpawnlistResource.X, value);
        return this;
    }

    public TerritorySpawnlistBuilder withY(Integer value) {
        with(TerritorySpawnlistResource.Y, value);
        return this;
    }

    public TerritorySpawnlistBuilder withZ(Integer value) {
        with(TerritorySpawnlistResource.Z, value);
        return this;
    }

    public TerritorySpawnlistBuilder withHeading(Integer value) {
        with(TerritorySpawnlistResource.HEADING, value);
        return this;
    }

    public TerritorySpawnlistBuilder withSpawnType(Integer value) {
        with(TerritorySpawnlistResource.SPAWN_TYPE, value);
        return this;
    }

}
