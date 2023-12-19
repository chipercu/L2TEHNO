package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SiegeTerritoryMembersResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SiegeTerritoryMembersBuilder extends ResourceBuilder<SiegeTerritoryMembersResource> {

    public SiegeTerritoryMembersBuilder() {
        try {
            resourceClass = SiegeTerritoryMembersResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SiegeTerritoryMembersBuilder withObjId(Integer value) {
        with(SiegeTerritoryMembersResource.OBJ__ID, value);
        return this;
    }

    public SiegeTerritoryMembersBuilder withSide(Integer value) {
        with(SiegeTerritoryMembersResource.SIDE, value);
        return this;
    }

    public SiegeTerritoryMembersBuilder withType(Integer value) {
        with(SiegeTerritoryMembersResource.TYPE, value);
        return this;
    }

}
