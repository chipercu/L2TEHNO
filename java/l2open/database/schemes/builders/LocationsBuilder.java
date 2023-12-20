package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.LocationsResource;

public class LocationsBuilder extends ResourceBuilder<LocationsResource> {

    public LocationsBuilder() {
        try {
            resourceClass = LocationsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LocationsBuilder withLocId(Integer value) {
        with(LocationsResource.LOC_ID, value);
        return this;
    }

    public LocationsBuilder withName(String value) {
        with(LocationsResource.NAME, value);
        return this;
    }

    public LocationsBuilder withLocX(Integer value) {
        with(LocationsResource.LOC_X, value);
        return this;
    }

    public LocationsBuilder withLocY(Integer value) {
        with(LocationsResource.LOC_Y, value);
        return this;
    }

    public LocationsBuilder withLocZmin(Integer value) {
        with(LocationsResource.LOC_ZMIN, value);
        return this;
    }

    public LocationsBuilder withLocZmax(Integer value) {
        with(LocationsResource.LOC_ZMAX, value);
        return this;
    }

    public LocationsBuilder withRadius(Integer value) {
        with(LocationsResource.RADIUS, value);
        return this;
    }

}
