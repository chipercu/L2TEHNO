package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SiegeTerritoryMembersResource;

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
