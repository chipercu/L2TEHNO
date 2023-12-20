package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.RangResource;

public class RangBuilder extends ResourceBuilder<RangResource> {

    public RangBuilder() {
        try {
            resourceClass = RangResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RangBuilder withCharId(Integer value) {
        with(RangResource.CHAR_ID, value);
        return this;
    }

    public RangBuilder withRangPoint(Long value) {
        with(RangResource.RANG_POINT, value);
        return this;
    }

}
