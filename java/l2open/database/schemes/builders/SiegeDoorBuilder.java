package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SiegeDoorResource;

public class SiegeDoorBuilder extends ResourceBuilder<SiegeDoorResource> {

    public SiegeDoorBuilder() {
        try {
            resourceClass = SiegeDoorResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SiegeDoorBuilder withUnitId(Integer value) {
        with(SiegeDoorResource.UNIT_ID, value);
        return this;
    }

    public SiegeDoorBuilder withId(Integer value) {
        with(SiegeDoorResource.ID, value);
        return this;
    }

}
