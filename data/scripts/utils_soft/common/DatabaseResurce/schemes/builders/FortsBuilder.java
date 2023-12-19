package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.FortsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class FortsBuilder extends ResourceBuilder<FortsResource> {

    public FortsBuilder() {
        try {
            resourceClass = FortsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FortsBuilder withId(Integer value) {
        with(FortsResource.ID, value);
        return this;
    }

    public FortsBuilder withName(String value) {
        with(FortsResource.NAME, value);
        return this;
    }

    public FortsBuilder withLastSiegeDate(Integer value) {
        with(FortsResource.LAST_SIEGE_DATE, value);
        return this;
    }

    public FortsBuilder withSiegeDate(Integer value) {
        with(FortsResource.SIEGE_DATE, value);
        return this;
    }

    public FortsBuilder withSkills(String value) {
        with(FortsResource.SKILLS, value);
        return this;
    }

    public FortsBuilder withOwnDate(Integer value) {
        with(FortsResource.OWN_DATE, value);
        return this;
    }

    public FortsBuilder withState(Integer value) {
        with(FortsResource.STATE, value);
        return this;
    }

    public FortsBuilder withCastleId(Integer value) {
        with(FortsResource.CASTLE_ID, value);
        return this;
    }

    public FortsBuilder withMercenaryId(Integer value) {
        with(FortsResource.MERCENARY_ID, value);
        return this;
    }

    public FortsBuilder withMercenaryLoc(String value) {
        with(FortsResource.MERCENARY_LOC, value);
        return this;
    }

}
