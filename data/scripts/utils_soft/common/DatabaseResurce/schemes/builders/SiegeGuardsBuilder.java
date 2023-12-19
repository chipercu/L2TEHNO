package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SiegeGuardsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SiegeGuardsBuilder extends ResourceBuilder<SiegeGuardsResource> {

    public SiegeGuardsBuilder() {
        try {
            resourceClass = SiegeGuardsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SiegeGuardsBuilder withUnitId(Integer value) {
        with(SiegeGuardsResource.UNIT_ID, value);
        return this;
    }

    public SiegeGuardsBuilder withId(Integer value) {
        with(SiegeGuardsResource.ID, value);
        return this;
    }

    public SiegeGuardsBuilder withNpcId(Integer value) {
        with(SiegeGuardsResource.NPC_ID, value);
        return this;
    }

    public SiegeGuardsBuilder withX(Integer value) {
        with(SiegeGuardsResource.X, value);
        return this;
    }

    public SiegeGuardsBuilder withY(Integer value) {
        with(SiegeGuardsResource.Y, value);
        return this;
    }

    public SiegeGuardsBuilder withZ(Integer value) {
        with(SiegeGuardsResource.Z, value);
        return this;
    }

    public SiegeGuardsBuilder withHeading(Integer value) {
        with(SiegeGuardsResource.HEADING, value);
        return this;
    }

    public SiegeGuardsBuilder withRespawnDelay(Integer value) {
        with(SiegeGuardsResource.RESPAWN_DELAY, value);
        return this;
    }

    public SiegeGuardsBuilder withIsHired(Integer value) {
        with(SiegeGuardsResource.IS_HIRED, value);
        return this;
    }

}
